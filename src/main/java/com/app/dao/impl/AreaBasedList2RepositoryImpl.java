package com.app.dao.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.dao.AreaBasedList2Repository;
import com.app.dto.TourResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class AreaBasedList2RepositoryImpl implements AreaBasedList2Repository {

	private static final String API_URL = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2";
	private static final String DETAIL_INTRO_API_URL = "https://apis.data.go.kr/B551011/KorService2/detailIntro2";
	private static final String SEARCH_KEYWORD_API_URL = "https://apis.data.go.kr/B551011/KorService2/searchKeyword2";
	private static final String SERVICE_KEY = "721165d2fd5e42df4a23b761e4ae503eed80e61c9743082e420b2a7dfa55f34b";
	private static final int CONNECT_TIMEOUT_MILLIS = 10000;
	private static final int READ_TIMEOUT_MILLIS = 30000;
	private static final int MAX_RETRY_COUNT = 3;
	private static final long RETRY_SLEEP_MILLIS = 1200L;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<TourResponseDTO.PlaceDto> requestApi_areaBasedList2(int pageNo, int numOfRows) throws Exception {
		StringBuilder urlBuilder = new StringBuilder(API_URL);
		urlBuilder.append("?").append(encode("serviceKey")).append("=").append(SERVICE_KEY);
		urlBuilder.append("&").append(encode("numOfRows")).append("=").append(encode(Integer.toString(numOfRows)));
		urlBuilder.append("&").append(encode("pageNo")).append("=").append(encode(Integer.toString(pageNo)));
		urlBuilder.append("&").append(encode("MobileOS")).append("=").append(encode("WEB"));
		urlBuilder.append("&").append(encode("MobileApp")).append("=").append(encode("HashTrip"));
		urlBuilder.append("&").append(encode("_type")).append("=").append(encode("json"));

		String responseBody = requestWithRetry(urlBuilder.toString(), true);
		if (responseBody == null || responseBody.isBlank()) {
			return Collections.emptyList();
		}

		TourResponseDTO responseData = objectMapper.readValue(responseBody, TourResponseDTO.class);
		if (responseData.getResponse() != null
				&& responseData.getResponse().getBody() != null
				&& responseData.getResponse().getBody().getItems() != null
				&& responseData.getResponse().getBody().getItems().getItem() != null) {
			return responseData.getResponse().getBody().getItems().getItem();
		}
		return Collections.emptyList();
	}

	@Override
	public String requestApi_detailIntro2OperatingHours(String contentId, String contentTypeId) throws Exception {
		if (contentId == null || contentId.isBlank() || contentTypeId == null || contentTypeId.isBlank()) {
			return "";
		}

		StringBuilder urlBuilder = new StringBuilder(DETAIL_INTRO_API_URL);
		urlBuilder.append("?").append(encode("serviceKey")).append("=").append(SERVICE_KEY);
		urlBuilder.append("&").append(encode("numOfRows")).append("=").append(encode("10"));
		urlBuilder.append("&").append(encode("pageNo")).append("=").append(encode("1"));
		urlBuilder.append("&").append(encode("contentId")).append("=").append(encode(contentId));
		urlBuilder.append("&").append(encode("contentTypeId")).append("=").append(encode(contentTypeId));
		urlBuilder.append("&").append(encode("MobileOS")).append("=").append(encode("WEB"));
		urlBuilder.append("&").append(encode("MobileApp")).append("=").append(encode("HashTrip"));
		urlBuilder.append("&").append(encode("_type")).append("=").append(encode("json"));

		String responseBody = requestWithRetry(urlBuilder.toString(), false);
		if (responseBody == null || responseBody.isBlank()) {
			return "";
		}

		JsonNode root = objectMapper.readTree(responseBody);
		JsonNode itemNode = root.path("response").path("body").path("items").path("item");
		JsonNode item = itemNode.isArray() && itemNode.size() > 0 ? itemNode.get(0) : itemNode;
		if (item == null || item.isMissingNode()) {
			return "";
		}

		return mergeOperatingHourTexts(item);
	}

	@Override
	public List<TourResponseDTO.PlaceDto> requestApi_searchKeyword2(String keyword, int pageNo, int numOfRows)
			throws Exception {
		if (keyword == null || keyword.isBlank()) {
			return Collections.emptyList();
		}

		StringBuilder urlBuilder = new StringBuilder(SEARCH_KEYWORD_API_URL);
		urlBuilder.append("?").append(encode("serviceKey")).append("=").append(SERVICE_KEY);
		urlBuilder.append("&").append(encode("numOfRows")).append("=").append(encode(Integer.toString(Math.max(1, numOfRows))));
		urlBuilder.append("&").append(encode("pageNo")).append("=").append(encode(Integer.toString(Math.max(1, pageNo))));
		urlBuilder.append("&").append(encode("MobileOS")).append("=").append(encode("WEB"));
		urlBuilder.append("&").append(encode("MobileApp")).append("=").append(encode("HashTrip"));
		urlBuilder.append("&").append(encode("_type")).append("=").append(encode("json"));
		urlBuilder.append("&").append(encode("keyword")).append("=").append(encode(keyword.trim()));

		String responseBody = requestWithRetry(urlBuilder.toString(), false);
		if (responseBody == null || responseBody.isBlank()) {
			return Collections.emptyList();
		}

		TourResponseDTO responseData = objectMapper.readValue(responseBody, TourResponseDTO.class);
		if (responseData.getResponse() != null
				&& responseData.getResponse().getBody() != null
				&& responseData.getResponse().getBody().getItems() != null
				&& responseData.getResponse().getBody().getItems().getItem() != null) {
			return responseData.getResponse().getBody().getItems().getItem();
		}
		return Collections.emptyList();
	}

	private String requestWithRetry(String url, boolean throwOnFailure) throws Exception {
		Exception lastException = null;

		for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
			try {
				return requestOnce(url);
			} catch (SocketTimeoutException e) {
				lastException = e;
			} catch (IOException e) {
				lastException = e;
			}

			if (attempt < MAX_RETRY_COUNT) {
				sleepBeforeRetry();
			}
		}

		if (throwOnFailure) {
			throw lastException != null ? lastException : new IOException("API request failed without details.");
		}
		return "";
	}

	private String requestOnce(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		conn.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
		conn.setReadTimeout(READ_TIMEOUT_MILLIS);

		BufferedReader rd = null;
		try {
			int responseCode = conn.getResponseCode();
			if (responseCode >= 200 && responseCode <= 299) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
				return readAll(rd);
			}

			InputStreamReader errorReader = conn.getErrorStream() == null
					? null
					: new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8);
			String errorBody = "";
			if (errorReader != null) {
				rd = new BufferedReader(errorReader);
				errorBody = readAll(rd);
			}
			throw new IOException("API response code=" + responseCode + ", body=" + errorBody);
		} finally {
			if (rd != null) {
				rd.close();
			}
			conn.disconnect();
		}
	}

	private String readAll(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	private void sleepBeforeRetry() {
		try {
			Thread.sleep(RETRY_SLEEP_MILLIS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private String mergeOperatingHourTexts(JsonNode item) {
		StringBuilder merged = new StringBuilder();
		appendIfExists(merged, item, "usetime");
		appendIfExists(merged, item, "usetimeculture");
		appendIfExists(merged, item, "usetimeleports");
		appendIfExists(merged, item, "usetimefestival");
		appendIfExists(merged, item, "opentimefood");
		appendIfExists(merged, item, "restdate");
		appendIfExists(merged, item, "restdateculture");
		appendIfExists(merged, item, "restdateleports");
		appendIfExists(merged, item, "restdatefood");
		appendIfExists(merged, item, "restdatefestival");
		appendIfExists(merged, item, "checkintime");
		appendIfExists(merged, item, "checkouttime");
		return merged.toString();
	}

	private void appendIfExists(StringBuilder merged, JsonNode item, String fieldName) {
		String value = item.path(fieldName).asText("");
		if (value == null) {
			return;
		}
		String trimmed = value.trim();
		if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed)) {
			return;
		}
		if (merged.length() > 0) {
			merged.append(" | ");
		}
		merged.append(trimmed);
	}

	private String encode(String value) throws Exception {
		return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
	}
}
