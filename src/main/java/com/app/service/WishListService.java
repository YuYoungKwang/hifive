package com.app.service;

import java.util.List;

import com.app.dto.CategoryDTO;
import com.app.dto.PlaceDTO;
import com.app.dto.WishListDTO;

public interface WishListService {

	public List<CategoryDTO> getCategoriesByAuthId(String authId) throws Exception;

	public List<WishListDTO> getWishListByAuthIdAndPlaceNo(String authId, Long placeNo) throws Exception;

	public CategoryDTO createCategory(String authId, String categoryType, String categoryIsUsed) throws Exception;

	public boolean updateCategoryUsage(String authId, Long categoryNo, String categoryIsUsed) throws Exception;

	public WishListDTO createWishList(String authId, Long placeNo, Long categoryNo) throws Exception;

	public boolean deleteWishList(String authId, Long wishNo) throws Exception;

	public int getWishUserCountByPlaceNo(Long placeNo) throws Exception;

	public int getWishPlaceCountByAuthId(String authId) throws Exception;

	public List<PlaceDTO> getWishPlacesByAuthId(String authId) throws Exception;

	public List<WishListDTO> getWishListWithPlaceByAuthId(String authId) throws Exception;
}
