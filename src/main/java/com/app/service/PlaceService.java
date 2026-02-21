package com.app.service;

public interface PlaceService {

	public int updateAreaBasedListPlaces() throws Exception;

	public int updateAreaBasedListPlaces(int maxPages, int pageSize, int batchSize) throws Exception;
}
