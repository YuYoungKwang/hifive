package com.app.dao;

import java.util.List;

import com.app.dto.CategoryDTO;
import com.app.dto.PlaceDTO;
import com.app.dto.WishListDTO;

public interface WishListDAO {

	public Long selectUserNoByAuthId(String authId) throws Exception;

	public Long getNextCategoryNo() throws Exception;

	public Long getNextWishNo() throws Exception;

	public int insertCategory(CategoryDTO categoryDTO) throws Exception;

	public int updateCategoryUsageByOwner(Long categoryNo, Long userNo, String categoryIsUsed) throws Exception;

	public List<CategoryDTO> selectCategoriesByUserNo(Long userNo) throws Exception;

	public int insertWishList(WishListDTO wishListDTO) throws Exception;

	public int deleteWishListByOwner(Long wishNo, Long userNo) throws Exception;

	public List<WishListDTO> selectWishListByUserAndPlace(Long userNo, Long placeNo) throws Exception;

	public int countWishListByUserAndPlaceAndCategory(Long userNo, Long placeNo, Long categoryNo) throws Exception;

	public int countWishUsersByPlaceNo(Long placeNo) throws Exception;

	public int countWishPlacesByAuthId(String authId) throws Exception;

	public List<PlaceDTO> selectWishPlacesByAuthId(String authId) throws Exception;

	public List<WishListDTO> selectWishListWithPlaceByAuthId(String authId) throws Exception;
}
