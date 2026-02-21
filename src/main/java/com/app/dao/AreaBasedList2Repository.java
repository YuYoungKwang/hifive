package com.app.dao;

import java.util.List;

import com.app.dto.TourResponseDTO;

public interface AreaBasedList2Repository {

	public List<TourResponseDTO.PlaceDto> requestApi_areaBasedList2(int pageNo, int numOfRows) throws Exception;

	public String requestApi_detailIntro2OperatingHours(String contentId, String contentTypeId) throws Exception;

	public List<TourResponseDTO.PlaceDto> requestApi_searchKeyword2(String keyword, int pageNo, int numOfRows)
			throws Exception;
}
