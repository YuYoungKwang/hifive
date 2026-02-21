package com.app.dao;

import java.util.List;

import com.app.dto.PlaceDTO;
import com.app.dto.PlaceHoursDTO;
import com.app.dto.PlaceReviewDTO;
import com.app.dto.PlaceTagMapDTO;

public interface PlaceDAO {

	public void resetPlaceImportData() throws Exception;

	public void resetPlaceHoursImportData() throws Exception;

	public Long getNextPlaceNo() throws Exception;

	public int updateAreaBasedListPlaces(PlaceDTO placeDTO) throws Exception;

	public int updateAreaBasedListPlacesBatch(List<PlaceDTO> placeDTOList) throws Exception;

	public int insertPlaceTagMapBatch(List<PlaceTagMapDTO> placeTagMapDTOList) throws Exception;

	public PlaceDTO selectPlaceByPlaceNo(Long placeNo) throws Exception;

	public PlaceDTO selectPlaceByContentId(String placeContentId) throws Exception;

	public PlaceDTO selectPlaceByNameAddress(String placeName, String placeAddress) throws Exception;

	public PlaceDTO selectPlaceByNameNearLatLng(String placeName, Double placeLatitude, Double placeLongitude)
			throws Exception;

	public List<String> selectPlaceTagNamesByPlaceNo(Long placeNo) throws Exception;

	public List<String> selectPlacePhotoUrlsByPlaceNo(Long placeNo) throws Exception;

	public List<PlaceReviewDTO> selectPlaceReviewsByPlaceNo(Long placeNo) throws Exception;

	public int countPlaceReviewsByCreatedBy(String createdBy) throws Exception;

	public List<PlaceReviewDTO> selectPlaceReviewsByCreatedByPaged(String createdBy, int startRow, int endRow) throws Exception;

	public List<PlaceReviewDTO> selectPlaceReviewsByCreatedByPaged(String createdBy, int startRow, int endRow, String sortType)
			throws Exception;

	public List<PlaceHoursDTO> selectPlaceHoursByPlaceNo(Long placeNo) throws Exception;

	public Long getNextPlaceReviewCommentNo() throws Exception;

	public int insertPlaceReview(PlaceReviewDTO placeReviewDTO) throws Exception;

	public int updatePlaceReviewByOwner(PlaceReviewDTO placeReviewDTO) throws Exception;

	public int deletePlaceReviewByOwner(Long commentNo, Long placeNo, String createdBy) throws Exception;

	public int updatePlaceRatingByPlaceNo(Long placeNo) throws Exception;

	public int insertPlaceHoursBatch(List<PlaceHoursDTO> placeHoursDTOList) throws Exception;

	public List<PlaceDTO> selectPlacesForHoursImport() throws Exception;
}
