package com.app.dao;

import java.util.List;

import com.app.dto.PlaceDTO;
import com.app.dto.PlaceTagMapDTO;

public interface PlaceDAO {

	public void resetPlaceImportData() throws Exception;

	public Long getNextPlaceNo() throws Exception;

	public int updateAreaBasedListPlaces(PlaceDTO placeDTO) throws Exception;

	public int updateAreaBasedListPlacesBatch(List<PlaceDTO> placeDTOList) throws Exception;

	public int insertPlaceTagMapBatch(List<PlaceTagMapDTO> placeTagMapDTOList) throws Exception;
}
