package com.app.service;

import java.util.List;

import com.app.dto.PlaceDTO;
import com.app.dto.PlaceHoursDTO;
import com.app.dto.PlaceReviewDTO;

public interface PlaceService {

	public int updateAreaBasedListPlaces() throws Exception;

	public int updateAreaBasedListPlaces(int maxPages, int pageSize, int batchSize) throws Exception;

	public PlaceDTO getPlaceByPlaceNo(Long placeNo) throws Exception;

	public Long resolvePlaceNoForPlan(String placeName, String placeAddress, Double placeLatitude, Double placeLongitude)
			throws Exception;

	public List<String> getPlaceTagNamesByPlaceNo(Long placeNo) throws Exception;

	public List<String> getPlacePhotoUrlsByPlaceNo(Long placeNo) throws Exception;

	public List<PlaceReviewDTO> getPlaceReviewsByPlaceNo(Long placeNo) throws Exception;

	public int getMyPlaceReviewCount(String createdBy) throws Exception;

	public List<PlaceReviewDTO> getMyPlaceReviews(String createdBy, int page, int pageSize) throws Exception;

	public List<PlaceReviewDTO> getMyPlaceReviews(String createdBy, int page, int pageSize, String sortType) throws Exception;

	public List<PlaceHoursDTO> getPlaceHoursByPlaceNo(Long placeNo) throws Exception;

	public int updatePlaceHours() throws Exception;

	public int updatePlaceHours(int batchSize) throws Exception;

	public int updatePlaceHours(int batchSize, int parallelism) throws Exception;

	public PlaceReviewDTO createPlaceReview(Long placeNo, String commentContent, Integer rating, String createdBy) throws Exception;

	public boolean updatePlaceReview(Long placeNo, Long commentNo, String commentContent, Integer rating, String createdBy) throws Exception;

	public boolean deletePlaceReview(Long placeNo, Long commentNo, String createdBy) throws Exception;
}
