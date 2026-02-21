package com.app.dao;

import java.util.List;

import com.app.dto.TourResponseDTO;

public interface AreaBasedList2Repository {

	public List<TourResponseDTO.PlaceDto> requestApi_areaBasedList2(int pageNo, int numOfRows) throws Exception;
}
