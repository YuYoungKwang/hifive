package com.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.app.dto.PlaceHoursDTO;

@Component
public class PlaceHoursParser {

	private static final String KR_BREAK = "\uBE0C\uB808\uC774\uD06C";
	private static final String KR_BREAK_TIME = "\uD734\uAC8C\\s*\uC2DC\uAC04";
	private static final String KR_LAST_ORDER = "\uB77C\uC2A4\uD2B8\\s*\uC624\uB354";
	private static final String KR_EVERY_DAY = "\uB9E4\uC77C";
	private static final String KR_WEEKDAY = "\uD3C9\uC77C";
	private static final String KR_WEEKEND = "\uC8FC\uB9D0";
	private static final String KR_OPEN_ALL_YEAR = "\uC5F0\uC911\uBB34\uD734";
	private static final String KR_NO_CLOSED = "\uD734\uBB34\uC5C6\uC74C";
	private static final String KR_CLOSED = "\uD734\uBB34";
	private static final String KR_CLOSED_FACILITY = "\uD734\uAD00";
	private static final String KR_REGULAR_CLOSED = "\uC815\uAE30\uD734\uBB34";

	private static final String KR_MON = "\uC6D4";
	private static final String KR_TUE = "\uD654";
	private static final String KR_WED = "\uC218";
	private static final String KR_THU = "\uBAA9";
	private static final String KR_FRI = "\uAE08";
	private static final String KR_SAT = "\uD1A0";
	private static final String KR_SUN = "\uC77C";

	private static final Pattern TIME_RANGE_PATTERN = Pattern.compile(
			"(?<!\\d)(\\d{1,2})[:.]?(\\d{2})\\s*[~\\-]\\s*(\\d{1,2})[:.]?(\\d{2})(?!\\d)");
	private static final Pattern BREAK_PATTERN = Pattern.compile(
			"(?:" + KR_BREAK + "\\s*\uD0C0\uC784|" + KR_BREAK_TIME + "|break\\s*time)\\s*[:\\uFF1A]?\\s*(\\d{1,2})[:.]?(\\d{2})\\s*[~\\-]\\s*(\\d{1,2})[:.]?(\\d{2})",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern LAST_ORDER_PATTERN = Pattern.compile(
			"(?:" + KR_LAST_ORDER + "|last\\s*order)\\s*[:\\uFF1A]?\\s*(\\d{1,2})[:.]?(\\d{2})",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern DAY_RANGE_PATTERN = Pattern.compile(
			"([\\uC6D4\\uD654\\uC218\\uBAA9\\uAE08\\uD1A0\\uC77C])\\s*[~\\-]\\s*([\\uC6D4\\uD654\\uC218\\uBAA9\\uAE08\\uD1A0\\uC77C])");

	private static final Map<String, Integer> DAY_MAP = createDayMap();

	public List<PlaceHoursDTO> parse(Long placeNo, String rawHoursText) {
		if (rawHoursText == null || rawHoursText.isBlank()) {
			return new ArrayList<>();
		}

		List<PlaceHoursDTO> result = initWeek(placeNo);
		String normalized = normalize(rawHoursText);
		List<String> segments = splitSegments(normalized);

		boolean anySegmentApplied = false;
		for (String segment : segments) {
			if (segment.isBlank()) {
				continue;
			}
			Set<Integer> targetDays = resolveTargetDays(segment);
			if (targetDays.isEmpty()) {
				targetDays.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
			}

			boolean isClosedSegment = containsClosedKeyword(segment);
			TimeRange openClose = parseFirstTimeRange(segment);
			TimeRange breakTime = parseBreakTime(segment);
			String lastOrder = parseLastOrder(segment);

			if (!isClosedSegment && openClose == null && breakTime == null && lastOrder == null) {
				continue;
			}

			applySegment(result, targetDays, isClosedSegment, openClose, breakTime, lastOrder);
			anySegmentApplied = true;
		}

		if (!anySegmentApplied) {
			Set<Integer> allDays = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
			boolean isClosed = containsClosedKeyword(normalized);
			TimeRange openClose = parseFirstTimeRange(normalized);
			TimeRange breakTime = parseBreakTime(normalized);
			String lastOrder = parseLastOrder(normalized);
			if (!isClosed && openClose == null && breakTime == null && lastOrder == null) {
				return new ArrayList<>();
			}
			applySegment(result, allDays, isClosed, openClose, breakTime, lastOrder);
		}

		return result;
	}

	private List<PlaceHoursDTO> initWeek(Long placeNo) {
		List<PlaceHoursDTO> list = new ArrayList<>();
		for (int day = 1; day <= 7; day++) {
			PlaceHoursDTO dto = new PlaceHoursDTO();
			dto.setPlaceNo(placeNo);
			dto.setDayOfWeek(day);
			dto.setIsClosed("N");
			list.add(dto);
		}
		return list;
	}

	private void applySegment(
			List<PlaceHoursDTO> week,
			Set<Integer> targetDays,
			boolean isClosed,
			TimeRange openClose,
			TimeRange breakTime,
			String lastOrder) {
		for (Integer day : targetDays) {
			if (day == null || day < 1 || day > 7) {
				continue;
			}
			PlaceHoursDTO dto = week.get(day - 1);
			if (isClosed) {
				dto.setIsClosed("Y");
				dto.setOpenTime(null);
				dto.setCloseTime(null);
				dto.setBreakStratTime(null);
				dto.setBreakEndTime(null);
				dto.setLastOrder(null);
				continue;
			}

			dto.setIsClosed("N");
			if (openClose != null) {
				dto.setOpenTime(openClose.start);
				dto.setCloseTime(openClose.end);
			}
			if (breakTime != null) {
				dto.setBreakStratTime(breakTime.start);
				dto.setBreakEndTime(breakTime.end);
			}
			if (lastOrder != null) {
				dto.setLastOrder(lastOrder);
			}
		}
	}

	private String normalize(String text) {
		return text.replace("\r", "\n")
				.replace('~', '-')
				.replace("\u2013", "-")
				.replace("\u2014", "-")
				.trim();
	}

	private List<String> splitSegments(String normalized) {
		String[] raw = normalized.split("[\\n|;,]");
		List<String> segments = new ArrayList<>();
		for (String part : raw) {
			String segment = part.trim();
			if (!segment.isEmpty()) {
				segments.add(segment);
			}
		}
		return segments;
	}

	private Set<Integer> resolveTargetDays(String segment) {
		Set<Integer> days = new LinkedHashSet<>();

		if (segment.contains(KR_EVERY_DAY)) {
			days.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
		}
		if (segment.contains(KR_WEEKDAY)) {
			days.addAll(Arrays.asList(1, 2, 3, 4, 5));
		}
		if (segment.contains(KR_WEEKEND)) {
			days.addAll(Arrays.asList(6, 7));
		}

		Matcher rangeMatcher = DAY_RANGE_PATTERN.matcher(segment);
		while (rangeMatcher.find()) {
			Integer start = DAY_MAP.get(rangeMatcher.group(1));
			Integer end = DAY_MAP.get(rangeMatcher.group(2));
			if (start != null && end != null) {
				addDayRange(days, start, end);
			}
		}

		for (Map.Entry<String, Integer> entry : DAY_MAP.entrySet()) {
			if (segment.contains(entry.getKey())) {
				days.add(entry.getValue());
			}
		}
		return days;
	}

	private void addDayRange(Set<Integer> target, int start, int end) {
		if (start <= end) {
			for (int day = start; day <= end; day++) {
				target.add(day);
			}
			return;
		}
		for (int day = start; day <= 7; day++) {
			target.add(day);
		}
		for (int day = 1; day <= end; day++) {
			target.add(day);
		}
	}

	private boolean containsClosedKeyword(String text) {
		String lowered = text.toLowerCase();
		if (lowered.contains(KR_OPEN_ALL_YEAR) || lowered.contains(KR_NO_CLOSED)) {
			return false;
		}
		return lowered.contains(KR_CLOSED)
				|| lowered.contains(KR_CLOSED_FACILITY)
				|| lowered.contains(KR_REGULAR_CLOSED)
				|| lowered.contains("closed");
	}

	private TimeRange parseFirstTimeRange(String text) {
		Matcher matcher = TIME_RANGE_PATTERN.matcher(text);
		if (!matcher.find()) {
			return null;
		}
		String start = formatTime(matcher.group(1), matcher.group(2));
		String end = formatTime(matcher.group(3), matcher.group(4));
		if (start == null || end == null) {
			return null;
		}
		return new TimeRange(start, end);
	}

	private TimeRange parseBreakTime(String text) {
		Matcher matcher = BREAK_PATTERN.matcher(text);
		if (!matcher.find()) {
			return null;
		}
		String start = formatTime(matcher.group(1), matcher.group(2));
		String end = formatTime(matcher.group(3), matcher.group(4));
		if (start == null || end == null) {
			return null;
		}
		return new TimeRange(start, end);
	}

	private String parseLastOrder(String text) {
		Matcher matcher = LAST_ORDER_PATTERN.matcher(text);
		if (!matcher.find()) {
			return null;
		}
		return formatTime(matcher.group(1), matcher.group(2));
	}

	private String formatTime(String hourValue, String minuteValue) {
		if (hourValue == null || minuteValue == null) {
			return null;
		}
		try {
			int hour = Integer.parseInt(hourValue);
			int minute = Integer.parseInt(minuteValue);
			if (hour < 0 || hour > 24 || minute < 0 || minute > 59) {
				return null;
			}
			return String.format("%02d:%02d", hour, minute);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Map<String, Integer> createDayMap() {
		Map<String, Integer> map = new LinkedHashMap<>();
		map.put(KR_MON + "\uC694\uC77C", 1);
		map.put(KR_TUE + "\uC694\uC77C", 2);
		map.put(KR_WED + "\uC694\uC77C", 3);
		map.put(KR_THU + "\uC694\uC77C", 4);
		map.put(KR_FRI + "\uC694\uC77C", 5);
		map.put(KR_SAT + "\uC694\uC77C", 6);
		map.put(KR_SUN + "\uC694\uC77C", 7);
		map.put(KR_MON, 1);
		map.put(KR_TUE, 2);
		map.put(KR_WED, 3);
		map.put(KR_THU, 4);
		map.put(KR_FRI, 5);
		map.put(KR_SAT, 6);
		map.put(KR_SUN, 7);
		return map;
	}

	private static class TimeRange {
		private final String start;
		private final String end;

		private TimeRange(String start, String end) {
			this.start = start;
			this.end = end;
		}
	}
}
