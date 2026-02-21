package com.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.app.dao.AreaBasedList2Repository;
import com.app.dao.PlaceDAO;
import com.app.dto.PlaceDTO;
import com.app.dto.PlaceHoursDTO;
import com.app.dto.PlaceReviewDTO;
import com.app.dto.PlaceTagMapDTO;
import com.app.dto.TourResponseDTO;
import com.app.service.ImportProgressTracker;
import com.app.service.PlaceHoursParser;
import com.app.service.PlaceService;
import com.app.service.PlaceTagClassifier;

@Service
public class PlaceServiceImpl implements PlaceService {

	private static final int DEFAULT_MAX_PAGES = 1;
	private static final int DEFAULT_PAGE_SIZE = 300;
	private static final int DEFAULT_BATCH_SIZE = 300;
	private static final int DEFAULT_HOURS_BATCH_SIZE = 500;
	private static final int DEFAULT_HOURS_PARALLELISM = 6;
	private static final int MAX_HOURS_PARALLELISM = 12;
	private static final String FALLBACK_PLACE_CATEGORY = "USER_MAP";

	@Autowired
	private AreaBasedList2Repository apiRepository;

	@Autowired
	private PlaceDAO placeDAO;

	@Autowired
	private PlaceTagClassifier placeTagClassifier;

	@Autowired
	private PlaceHoursParser placeHoursParser;

	@Autowired
	private ImportProgressTracker importProgressTracker;

	@Override
	public int updateAreaBasedListPlaces() throws Exception {
		return updateAreaBasedListPlaces(DEFAULT_MAX_PAGES, DEFAULT_PAGE_SIZE, DEFAULT_BATCH_SIZE);
	}

	@Override
	public int updateAreaBasedListPlaces(int maxPages, int pageSize, int batchSize) throws Exception {
		int safeMaxPages = Math.max(1, maxPages);
		int safePageSize = Math.max(1, pageSize);
		int safeBatchSize = Math.max(1, batchSize);
		importProgressTracker.startPlaceImport(safePageSize, safeMaxPages);

		try {
			// Full refresh: clear PLACE/PLACE_TAG_MAP and reset sequences.
			placeDAO.resetPlaceImportData();

			int result = 0;
			List<PlaceDTO> buffer = new ArrayList<>(safeBatchSize);
			List<PlaceTagMapDTO> tagMapBuffer = new ArrayList<>(safeBatchSize * 10);

			for (int pageNo = 1; pageNo <= safeMaxPages; pageNo++) {
				List<TourResponseDTO.PlaceDto> items = apiRepository.requestApi_areaBasedList2(pageNo, safePageSize);
				if (items == null || items.isEmpty()) {
					break;
				}
				importProgressTracker.onPlacePageFetched(pageNo, items.size());

				for (TourResponseDTO.PlaceDto item : items) {
					Long placeNo = placeDAO.getNextPlaceNo();
					List<String> tagCodes = placeTagClassifier.classifyTagCodes(item);
					buffer.add(toPlaceDTO(item, placeNo, tagCodes));
					tagMapBuffer.addAll(toPlaceTagMapDTOList(placeNo, tagCodes));

					if (buffer.size() >= safeBatchSize) {
						int inserted = placeDAO.updateAreaBasedListPlacesBatch(buffer);
						result += inserted;
						importProgressTracker.addInserted(inserted);
						placeDAO.insertPlaceTagMapBatch(tagMapBuffer);
						buffer.clear();
						tagMapBuffer.clear();
					}
				}

				if (items.size() < safePageSize) {
					break;
				}
			}

			if (!buffer.isEmpty()) {
				int inserted = placeDAO.updateAreaBasedListPlacesBatch(buffer);
				result += inserted;
				importProgressTracker.addInserted(inserted);
				placeDAO.insertPlaceTagMapBatch(tagMapBuffer);
			}

			importProgressTracker.complete("Place import completed (" + result + " rows)");
			return result;
		} catch (Exception e) {
			importProgressTracker.fail(e);
			throw e;
		}
	}

	@Override
	public int updatePlaceHours() throws Exception {
		return updatePlaceHours(DEFAULT_HOURS_BATCH_SIZE, DEFAULT_HOURS_PARALLELISM);
	}

	@Override
	public int updatePlaceHours(int batchSize) throws Exception {
		return updatePlaceHours(batchSize, DEFAULT_HOURS_PARALLELISM);
	}

	@Override
	public int updatePlaceHours(int batchSize, int parallelism) throws Exception {
		int safeBatchSize = Math.max(1, batchSize);
		int safeParallelism = Math.max(1, Math.min(parallelism, MAX_HOURS_PARALLELISM));
		ExecutorService executor = Executors.newFixedThreadPool(safeParallelism);

		try {
			placeDAO.resetPlaceHoursImportData();

			List<PlaceDTO> places = placeDAO.selectPlacesForHoursImport();
			importProgressTracker.startHoursImport(places == null ? 0 : places.size(), safeBatchSize);
			if (places == null || places.isEmpty()) {
				importProgressTracker.complete("No places found for hours import.");
				return 0;
			}

			int insertedRows = 0;
			List<PlaceHoursDTO> hoursBuffer = new ArrayList<>(safeBatchSize);
			ExecutorCompletionService<List<PlaceHoursDTO>> completionService = new ExecutorCompletionService<>(executor);

			int submitted = 0;
			int completed = 0;
			for (PlaceDTO place : places) {
				completionService.submit(() -> loadAndParseHours(place));
				submitted++;

				if (submitted - completed >= safeParallelism) {
					List<PlaceHoursDTO> parsed = takeCompletedHours(completionService);
					completed++;
					importProgressTracker.onHoursPlaceProcessed(completed);
					insertedRows += appendHoursAndFlush(parsed, hoursBuffer, safeBatchSize);
				}
			}

			while (completed < submitted) {
				List<PlaceHoursDTO> parsed = takeCompletedHours(completionService);
				completed++;
				importProgressTracker.onHoursPlaceProcessed(completed);
				insertedRows += appendHoursAndFlush(parsed, hoursBuffer, safeBatchSize);
			}

			if (!hoursBuffer.isEmpty()) {
				int inserted = placeDAO.insertPlaceHoursBatch(hoursBuffer);
				insertedRows += inserted;
				importProgressTracker.addInserted(inserted);
			}
			importProgressTracker.complete("Hours import completed (" + insertedRows + " rows)");
			return insertedRows;
		} catch (Exception e) {
			importProgressTracker.fail(e);
			throw e;
		} finally {
			executor.shutdownNow();
		}
	}

	private List<PlaceHoursDTO> loadAndParseHours(PlaceDTO place) {
		String operatingHoursRawText = fetchOperatingHoursRawText(place.getPlaceContentId(), place.getPlaceCategory());
		List<PlaceHoursDTO> parsed = placeHoursParser.parse(place.getPlaceNo(), operatingHoursRawText);
		return parsed == null ? Collections.emptyList() : parsed;
	}

	private List<PlaceHoursDTO> takeCompletedHours(ExecutorCompletionService<List<PlaceHoursDTO>> completionService) throws Exception {
		try {
			return completionService.take().get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw e;
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof Exception) {
				throw (Exception) cause;
			}
			throw new RuntimeException(cause);
		}
	}

	private int appendHoursAndFlush(List<PlaceHoursDTO> parsed, List<PlaceHoursDTO> hoursBuffer, int batchSize) throws Exception {
		if (parsed == null || parsed.isEmpty()) {
			return 0;
		}

		hoursBuffer.addAll(parsed);
		if (hoursBuffer.size() < batchSize) {
			return 0;
		}

		int inserted = placeDAO.insertPlaceHoursBatch(hoursBuffer);
		importProgressTracker.addInserted(inserted);
		hoursBuffer.clear();
		return inserted;
	}

	@Override
	public PlaceDTO getPlaceByPlaceNo(Long placeNo) throws Exception {
		return placeDAO.selectPlaceByPlaceNo(placeNo);
	}

	@Override
	public Long resolvePlaceNoForPlan(String placeName, String placeAddress, Double placeLatitude, Double placeLongitude)
			throws Exception {
		String safeName = truncate(normalizeText(placeName), 200);
		if (!StringUtils.hasText(safeName)) {
			return null;
		}

		String safeAddress = truncate(normalizeText(placeAddress), 500);
		Double safeLatitude = placeLatitude;
		Double safeLongitude = placeLongitude;

		PlaceDTO existing = findExistingPlace(null, safeName, safeAddress, safeLatitude, safeLongitude);
		if (existing != null && existing.getPlaceNo() != null) {
			return existing.getPlaceNo();
		}

		TourResponseDTO.PlaceDto candidate = null;
		try {
			List<TourResponseDTO.PlaceDto> candidates = apiRepository.requestApi_searchKeyword2(safeName, 1, 10);
			candidate = pickBestKeywordCandidate(candidates, safeName, safeAddress, safeLatitude, safeLongitude);
		} catch (Exception e) {
			candidate = null;
		}

		// Avoid mismatches like "천안역" -> "천안역점".
		if (candidate != null && isSuspiciousKeywordCandidate(safeName, candidate.getTitle())) {
			candidate = null;
		}

		if (candidate != null) {
			String contentId = truncate(normalizeText(candidate.getContentid()), 30);
			String candidateName = truncate(normalizeText(candidate.getTitle()), 200);
			String candidateAddress = truncate(buildCombinedAddress(candidate.getAddr1(), candidate.getAddr2()), 500);
			Double candidateLatitude = parseDoubleOrNull(candidate.getMapy());
			Double candidateLongitude = parseDoubleOrNull(candidate.getMapx());

			PlaceDTO matchedByCandidate = findExistingPlace(
					contentId,
					StringUtils.hasText(candidateName) ? candidateName : safeName,
					StringUtils.hasText(candidateAddress) ? candidateAddress : safeAddress,
					candidateLatitude != null ? candidateLatitude : safeLatitude,
					candidateLongitude != null ? candidateLongitude : safeLongitude);
			if (matchedByCandidate != null && matchedByCandidate.getPlaceNo() != null) {
				return matchedByCandidate.getPlaceNo();
			}

			PlaceDTO placeToInsert = toPlaceDTOForKeywordCandidate(candidate, safeName, safeAddress, safeLatitude, safeLongitude);
			return insertSinglePlaceWithTags(placeToInsert);
		}

		// Prevent low-quality PLACE rows that only keep a name.
		// If both address and coordinates are missing, keep it as memo-only in plan details.
		boolean hasAddress = StringUtils.hasText(safeAddress);
		boolean hasCoordinates = safeLatitude != null && safeLongitude != null;
		if (!hasAddress && !hasCoordinates) {
			return null;
		}

		return insertSinglePlaceWithTags(toPlaceDTOForMapSelection(safeName, safeAddress, safeLatitude, safeLongitude));
	}

	@Override
	public List<String> getPlaceTagNamesByPlaceNo(Long placeNo) throws Exception {
		return placeDAO.selectPlaceTagNamesByPlaceNo(placeNo);
	}

	@Override
	public List<String> getPlacePhotoUrlsByPlaceNo(Long placeNo) throws Exception {
		List<String> photoUrlList = placeDAO.selectPlacePhotoUrlsByPlaceNo(placeNo);
		if (photoUrlList == null || photoUrlList.isEmpty()) {
			return new ArrayList<>();
		}
		Set<String> uniquePhotoUrlSet = new LinkedHashSet<>();
		for (String photoUrl : photoUrlList) {
			if (photoUrl != null && !photoUrl.isBlank()) {
				uniquePhotoUrlSet.add(photoUrl.trim());
			}
		}
		return new ArrayList<>(uniquePhotoUrlSet);
	}

	@Override
	public List<PlaceReviewDTO> getPlaceReviewsByPlaceNo(Long placeNo) throws Exception {
		return placeDAO.selectPlaceReviewsByPlaceNo(placeNo);
	}

	@Override
	public int getMyPlaceReviewCount(String createdBy) throws Exception {
		String safeCreatedBy = normalizeCreatedBy(createdBy);
		return placeDAO.countPlaceReviewsByCreatedBy(safeCreatedBy);
	}

	@Override
	public List<PlaceReviewDTO> getMyPlaceReviews(String createdBy, int page, int pageSize) throws Exception {
		return getMyPlaceReviews(createdBy, page, pageSize, "latest");
	}

	@Override
	public List<PlaceReviewDTO> getMyPlaceReviews(String createdBy, int page, int pageSize, String sortType) throws Exception {
		String safeCreatedBy = normalizeCreatedBy(createdBy);
		int safePage = Math.max(1, page);
		int safePageSize = Math.max(1, pageSize);
		int startRow = ((safePage - 1) * safePageSize) + 1;
		int endRow = safePage * safePageSize;
		String safeSortType = normalizeReviewSort(sortType);
		return placeDAO.selectPlaceReviewsByCreatedByPaged(safeCreatedBy, startRow, endRow, safeSortType);
	}

	@Override
	public List<PlaceHoursDTO> getPlaceHoursByPlaceNo(Long placeNo) throws Exception {
		return placeDAO.selectPlaceHoursByPlaceNo(placeNo);
	}

	@Override
	public PlaceReviewDTO createPlaceReview(Long placeNo, String commentContent, Integer rating, String createdBy) throws Exception {
		String safeCreatedBy = normalizeCreatedBy(createdBy);
		String safeContent = normalizeReviewContent(commentContent);
		int safeRating = normalizeRating(rating);

		PlaceReviewDTO placeReviewDTO = new PlaceReviewDTO();
		placeReviewDTO.setCommentNo(placeDAO.getNextPlaceReviewCommentNo());
		placeReviewDTO.setPlaceNo(placeNo);
		placeReviewDTO.setLogNo(null);
		placeReviewDTO.setCommentContent(safeContent);
		placeReviewDTO.setRating(safeRating);
		placeReviewDTO.setCreatedBy(safeCreatedBy);

		int inserted = placeDAO.insertPlaceReview(placeReviewDTO);
		if (inserted <= 0) {
			throw new IllegalStateException("Failed to insert place review.");
		}
		placeDAO.updatePlaceRatingByPlaceNo(placeNo);
		return placeReviewDTO;
	}

	@Override
	public boolean updatePlaceReview(Long placeNo, Long commentNo, String commentContent, Integer rating, String createdBy) throws Exception {
		String safeCreatedBy = normalizeCreatedBy(createdBy);
		String safeContent = normalizeReviewContent(commentContent);
		int safeRating = normalizeRating(rating);

		PlaceReviewDTO placeReviewDTO = new PlaceReviewDTO();
		placeReviewDTO.setCommentNo(commentNo);
		placeReviewDTO.setPlaceNo(placeNo);
		placeReviewDTO.setCommentContent(safeContent);
		placeReviewDTO.setRating(safeRating);
		placeReviewDTO.setCreatedBy(safeCreatedBy);

		boolean updated = placeDAO.updatePlaceReviewByOwner(placeReviewDTO) > 0;
		if (updated) {
			placeDAO.updatePlaceRatingByPlaceNo(placeNo);
		}
		return updated;
	}

	@Override
	public boolean deletePlaceReview(Long placeNo, Long commentNo, String createdBy) throws Exception {
		String safeCreatedBy = normalizeCreatedBy(createdBy);
		boolean deleted = placeDAO.deletePlaceReviewByOwner(commentNo, placeNo, safeCreatedBy) > 0;
		if (deleted) {
			placeDAO.updatePlaceRatingByPlaceNo(placeNo);
		}
		return deleted;
	}

	private PlaceDTO toPlaceDTO(TourResponseDTO.PlaceDto item, Long placeNo, List<String> tagCodes) {
		PlaceDTO placeDTO = new PlaceDTO();
		placeDTO.setPlaceNo(placeNo);
		placeDTO.setPlaceContentId(truncate(item.getContentid(), 30));
		placeDTO.setPlaceName(truncate(item.getTitle(), 200));
		placeDTO.setPlaceCategory(truncate(item.getContenttypeid(), 50));
		placeDTO.setPlaceAddress(truncate(buildCombinedAddress(item.getAddr1(), item.getAddr2()), 500));
		placeDTO.setPlaceLatitude(parseDoubleOrNull(item.getMapy()));
		placeDTO.setPlaceLongitude(parseDoubleOrNull(item.getMapx()));
		placeDTO.setPlaceNumber(preserveOriginalPlaceNumber(item.getTel()));
		placeDTO.setPlaceThumbnailUrl(truncate(item.getFirstimage2(), 1000));
		return placeDTO;
	}

	private List<PlaceTagMapDTO> toPlaceTagMapDTOList(Long placeNo, List<String> tagCodes) {
		List<PlaceTagMapDTO> list = new ArrayList<>();
		for (String tagCode : tagCodes) {
			PlaceTagMapDTO placeTagMapDTO = new PlaceTagMapDTO();
			placeTagMapDTO.setPlaceNo(placeNo);
			placeTagMapDTO.setTagCode(tagCode);
			placeTagMapDTO.setTagWeight(1.0);
			placeTagMapDTO.setTagSource("RULE");
			placeTagMapDTO.setTagConfidence(1.0);
			list.add(placeTagMapDTO);
		}
		return list;
	}

	private String fetchOperatingHoursRawText(String contentId, String contentTypeId) {
		try {
			return apiRepository.requestApi_detailIntro2OperatingHours(contentId, contentTypeId);
		} catch (Exception e) {
			return "";
		}
	}

	private PlaceDTO findExistingPlace(String contentId,
			String placeName,
			String placeAddress,
			Double placeLatitude,
			Double placeLongitude) throws Exception {
		if (StringUtils.hasText(contentId)) {
			PlaceDTO byContentId = placeDAO.selectPlaceByContentId(contentId);
			if (byContentId != null) {
				return byContentId;
			}
		}

		if (StringUtils.hasText(placeName)) {
			PlaceDTO byNameAddress = placeDAO.selectPlaceByNameAddress(placeName, placeAddress);
			if (byNameAddress != null) {
				return byNameAddress;
			}
		}

		if (StringUtils.hasText(placeName) && placeLatitude != null && placeLongitude != null) {
			PlaceDTO byNameLatLng = placeDAO.selectPlaceByNameNearLatLng(placeName, placeLatitude, placeLongitude);
			if (byNameLatLng != null) {
				return byNameLatLng;
			}
		}

		return null;
	}

	private Long insertSinglePlaceWithTags(PlaceDTO placeDTO) throws Exception {
		Long placeNo = placeDAO.getNextPlaceNo();
		placeDTO.setPlaceNo(placeNo);

		int inserted = placeDAO.updateAreaBasedListPlaces(placeDTO);
		if (inserted <= 0) {
			throw new IllegalStateException("Failed to insert place.");
		}

		List<String> tagCodes = placeTagClassifier.classifyTagCodes(toClassifiablePlace(placeDTO));
		if (tagCodes != null && !tagCodes.isEmpty()) {
			placeDAO.insertPlaceTagMapBatch(toPlaceTagMapDTOList(placeNo, tagCodes));
		}

		insertPlaceHoursSafely(placeNo, placeDTO.getPlaceContentId(), placeDTO.getPlaceCategory());

		return placeNo;
	}

	private TourResponseDTO.PlaceDto toClassifiablePlace(PlaceDTO placeDTO) {
		TourResponseDTO.PlaceDto placeDto = new TourResponseDTO.PlaceDto();
		placeDto.setContentid(placeDTO.getPlaceContentId());
		placeDto.setTitle(placeDTO.getPlaceName());
		placeDto.setContenttypeid(placeDTO.getPlaceCategory());
		placeDto.setAddr1(placeDTO.getPlaceAddress());
		placeDto.setMapy(placeDTO.getPlaceLatitude() == null ? null : String.valueOf(placeDTO.getPlaceLatitude()));
		placeDto.setMapx(placeDTO.getPlaceLongitude() == null ? null : String.valueOf(placeDTO.getPlaceLongitude()));
		placeDto.setTel(placeDTO.getPlaceNumber());
		placeDto.setFirstimage2(placeDTO.getPlaceThumbnailUrl());
		return placeDto;
	}

	private PlaceDTO toPlaceDTOForKeywordCandidate(TourResponseDTO.PlaceDto candidate,
			String fallbackName,
			String fallbackAddress,
			Double fallbackLatitude,
			Double fallbackLongitude) {
		PlaceDTO placeDTO = new PlaceDTO();
		placeDTO.setPlaceContentId(truncate(normalizeText(candidate.getContentid()), 30));
		// Prefer user-selected values from map for stable UX.
		placeDTO.setPlaceName(truncate(resolveFirstText(fallbackName, candidate.getTitle(), "Unknown Place"), 200));
		placeDTO.setPlaceCategory(truncate(resolveFirstText(candidate.getContenttypeid(), FALLBACK_PLACE_CATEGORY), 50));
		placeDTO.setPlaceAddress(truncate(resolveFirstText(
				fallbackAddress,
				buildCombinedAddress(candidate.getAddr1(), candidate.getAddr2())), 500));

		Double latitude = parseDoubleOrNull(candidate.getMapy());
		Double longitude = parseDoubleOrNull(candidate.getMapx());
		placeDTO.setPlaceLatitude(fallbackLatitude != null ? fallbackLatitude : latitude);
		placeDTO.setPlaceLongitude(fallbackLongitude != null ? fallbackLongitude : longitude);

		placeDTO.setPlaceNumber(preserveOriginalPlaceNumber(candidate.getTel()));
		placeDTO.setPlaceThumbnailUrl(truncate(normalizeText(candidate.getFirstimage2()), 1000));
		return placeDTO;
	}

	private PlaceDTO toPlaceDTOForMapSelection(String placeName,
			String placeAddress,
			Double placeLatitude,
			Double placeLongitude) {
		PlaceDTO placeDTO = new PlaceDTO();
		placeDTO.setPlaceContentId(null);
		placeDTO.setPlaceName(truncate(resolveFirstText(placeName, "Unknown Place"), 200));
		placeDTO.setPlaceCategory(FALLBACK_PLACE_CATEGORY);
		placeDTO.setPlaceAddress(truncate(placeAddress, 500));
		placeDTO.setPlaceLatitude(placeLatitude);
		placeDTO.setPlaceLongitude(placeLongitude);
		placeDTO.setPlaceNumber(null);
		placeDTO.setPlaceThumbnailUrl(null);
		return placeDTO;
	}

	private TourResponseDTO.PlaceDto pickBestKeywordCandidate(List<TourResponseDTO.PlaceDto> candidates,
			String requestedName,
			String requestedAddress,
			Double requestedLatitude,
			Double requestedLongitude) {
		if (candidates == null || candidates.isEmpty()) {
			return null;
		}

		TourResponseDTO.PlaceDto best = null;
		int bestScore = Integer.MIN_VALUE;

		String normalizedName = normalizeCompareKey(requestedName);
		String normalizedAddress = normalizeCompareKey(requestedAddress);

		for (TourResponseDTO.PlaceDto candidate : candidates) {
			if (candidate == null) {
				continue;
			}

			int score = 0;
			String candidateName = normalizeCompareKey(candidate.getTitle());
			String candidateAddress = normalizeCompareKey(buildCombinedAddress(candidate.getAddr1(), candidate.getAddr2()));

			if (StringUtils.hasText(normalizedName) && normalizedName.equals(candidateName)) {
				score += 60;
			} else if (StringUtils.hasText(normalizedName)
					&& (candidateName.contains(normalizedName) || normalizedName.contains(candidateName))) {
				score += 35;
			}

			if (StringUtils.hasText(normalizedAddress) && normalizedAddress.equals(candidateAddress)) {
				score += 30;
			} else if (StringUtils.hasText(normalizedAddress)
					&& (candidateAddress.contains(normalizedAddress) || normalizedAddress.contains(candidateAddress))) {
				score += 15;
			}

			Double candidateLatitude = parseDoubleOrNull(candidate.getMapy());
			Double candidateLongitude = parseDoubleOrNull(candidate.getMapx());
			if (requestedLatitude != null && requestedLongitude != null
					&& candidateLatitude != null && candidateLongitude != null) {
				double diff = Math.abs(requestedLatitude - candidateLatitude)
						+ Math.abs(requestedLongitude - candidateLongitude);
				if (diff <= 0.0008d) {
					score += 35;
				} else if (diff <= 0.004d) {
					score += 20;
				} else if (diff <= 0.02d) {
					score += 10;
				}
			}

			if (StringUtils.hasText(candidate.getContentid())) {
				score += 5;
			}

			if (best == null || score > bestScore) {
				best = candidate;
				bestScore = score;
			}
		}

		return best;
	}

	private boolean isSuspiciousKeywordCandidate(String requestedName, String candidateName) {
		String req = normalizeText(requestedName);
		String cand = normalizeText(candidateName);
		if (!StringUtils.hasText(req) || !StringUtils.hasText(cand)) {
			return false;
		}

		String reqNorm = req.replace(" ", "").toLowerCase();
		String candNorm = cand.replace(" ", "").toLowerCase();

		if (reqNorm.equals(candNorm)) {
			return false;
		}

		boolean reqHasStoreSuffix = reqNorm.contains("점") || reqNorm.contains("지점");
		boolean candHasStoreSuffix = candNorm.contains("점") || candNorm.contains("지점");

		// Example: requested "천안역" but candidate "천안역점"
		if (!reqHasStoreSuffix && candHasStoreSuffix) {
			return true;
		}

		// Station-like keyword should not map to store branch name.
		if (reqNorm.endsWith("역") && candHasStoreSuffix) {
			return true;
		}

		return false;
	}

	private String normalizeCompareKey(String value) {
		String normalized = normalizeText(value);
		if (!StringUtils.hasText(normalized)) {
			return "";
		}
		return normalized.toLowerCase().replace(" ", "");
	}

	private String resolveFirstText(String... values) {
		if (values == null) {
			return null;
		}
		for (String value : values) {
			String normalized = normalizeText(value);
			if (StringUtils.hasText(normalized)) {
				return normalized;
			}
		}
		return null;
	}

	private Double parseDoubleOrNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String preserveOriginalPlaceNumber(String rawValue) {
		if (rawValue == null) {
			return null;
		}
		String value = rawValue.trim();
		if (value.isEmpty()) {
			return null;
		}
		return truncate(value, 255);
	}

	private String truncate(String value, int maxLength) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		if (trimmed.length() <= maxLength) {
			return trimmed;
		}
		return trimmed.substring(0, maxLength);
	}

	private void insertPlaceHoursSafely(Long placeNo, String contentId, String contentTypeId) {
		try {
			if (placeNo == null || !StringUtils.hasText(contentId) || !StringUtils.hasText(contentTypeId)) {
				return;
			}

			String operatingHoursRawText = fetchOperatingHoursRawText(contentId, contentTypeId);
			if (!StringUtils.hasText(operatingHoursRawText)) {
				return;
			}

			List<PlaceHoursDTO> parsedHours = placeHoursParser.parse(placeNo, operatingHoursRawText);
			if (parsedHours == null || parsedHours.isEmpty()) {
				return;
			}

			placeDAO.insertPlaceHoursBatch(parsedHours);
		} catch (Exception ignored) {
			// Place save should not fail because of hours fetch/parse issues.
		}
	}

	private String normalizeText(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		return value.trim();
	}

	private String buildCombinedAddress(String addr1, String addr2) {
		String a1 = normalizeText(addr1);
		String a2 = normalizeText(addr2);

		if (!StringUtils.hasText(a1) && !StringUtils.hasText(a2)) {
			return null;
		}
		if (!StringUtils.hasText(a1)) {
			return a2;
		}
		if (!StringUtils.hasText(a2)) {
			return a1;
		}
		return a1 + " " + a2;
	}

	private String normalizeCreatedBy(String createdBy) {
		if (createdBy == null || createdBy.trim().isEmpty()) {
			throw new IllegalArgumentException("Login user information is required.");
		}
		return truncate(createdBy, 100);
	}

	private String normalizeReviewContent(String commentContent) {
		if (commentContent == null || commentContent.trim().isEmpty()) {
			throw new IllegalArgumentException("Review content is required.");
		}
		return truncate(commentContent, 2000);
	}

	private int normalizeRating(Integer rating) {
		if (rating == null) {
			return 5;
		}
		if (rating < 1) {
			return 1;
		}
		if (rating > 5) {
			return 5;
		}
		return rating;
	}

	private String normalizeReviewSort(String sortType) {
		if (!StringUtils.hasText(sortType)) {
			return "latest";
		}
		String normalized = sortType.trim().toLowerCase();
		if ("oldest".equals(normalized) || "rating".equals(normalized)) {
			return normalized;
		}
		return "latest";
	}
}
