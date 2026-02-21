package com.app.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.WishListDAO;
import com.app.dto.CategoryDTO;
import com.app.dto.PlaceDTO;
import com.app.dto.WishListDTO;

@Repository
public class WishListDAOImpl implements WishListDAO {

	private static final String SELECT_USER_NO_BY_AUTH_ID_STATEMENT_ID = "place_mapper.selectUserNoByAuthId";
	private static final String NEXT_CATEGORY_NO_STATEMENT_ID = "place_mapper.getNextCategoryNo";
	private static final String NEXT_WISH_NO_STATEMENT_ID = "place_mapper.getNextWishNo";
	private static final String INSERT_CATEGORY_STATEMENT_ID = "place_mapper.insertWishlistCategory";
	private static final String UPDATE_CATEGORY_USAGE_BY_OWNER_STATEMENT_ID = "place_mapper.updateWishlistCategoryUsageByOwner";
	private static final String SELECT_CATEGORIES_BY_USER_NO_STATEMENT_ID = "place_mapper.selectWishlistCategoriesByUserNo";
	private static final String INSERT_WISHLIST_STATEMENT_ID = "place_mapper.insertWishlist";
	private static final String DELETE_WISHLIST_BY_OWNER_STATEMENT_ID = "place_mapper.deleteWishlistByOwner";
	private static final String SELECT_WISHLIST_BY_USER_AND_PLACE_STATEMENT_ID = "place_mapper.selectWishlistByUserAndPlace";
	private static final String COUNT_WISHLIST_BY_USER_AND_PLACE_AND_CATEGORY_STATEMENT_ID = "place_mapper.countWishlistByUserAndPlaceAndCategory";
	private static final String COUNT_WISH_USERS_BY_PLACE_NO_STATEMENT_ID = "place_mapper.countWishUsersByPlaceNo";
	private static final String COUNT_WISH_PLACES_BY_AUTH_ID_STATEMENT_ID = "place_mapper.countWishPlacesByAuthId";
	private static final String SELECT_WISH_PLACES_BY_AUTH_ID_STATEMENT_ID = "place_mapper.selectWishPlacesByAuthId";
	private static final String SELECT_WISH_LIST_WITH_PLACE_BY_AUTH_ID_STATEMENT_ID = "place_mapper.selectWishListWithPlaceByAuthId";

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public Long selectUserNoByAuthId(String authId) throws Exception {
		return sqlSessionTemplate.selectOne(SELECT_USER_NO_BY_AUTH_ID_STATEMENT_ID, authId);
	}

	@Override
	public Long getNextCategoryNo() throws Exception {
		return sqlSessionTemplate.selectOne(NEXT_CATEGORY_NO_STATEMENT_ID);
	}

	@Override
	public Long getNextWishNo() throws Exception {
		return sqlSessionTemplate.selectOne(NEXT_WISH_NO_STATEMENT_ID);
	}

	@Override
	public int insertCategory(CategoryDTO categoryDTO) throws Exception {
		return sqlSessionTemplate.insert(INSERT_CATEGORY_STATEMENT_ID, categoryDTO);
	}

	@Override
	public int updateCategoryUsageByOwner(Long categoryNo, Long userNo, String categoryIsUsed) throws Exception {
		return sqlSessionTemplate.update(UPDATE_CATEGORY_USAGE_BY_OWNER_STATEMENT_ID,
				buildCategoryUsageParams(categoryNo, userNo, categoryIsUsed));
	}

	@Override
	public List<CategoryDTO> selectCategoriesByUserNo(Long userNo) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_CATEGORIES_BY_USER_NO_STATEMENT_ID, userNo);
	}

	@Override
	public int insertWishList(WishListDTO wishListDTO) throws Exception {
		return sqlSessionTemplate.insert(INSERT_WISHLIST_STATEMENT_ID, wishListDTO);
	}

	@Override
	public int deleteWishListByOwner(Long wishNo, Long userNo) throws Exception {
		return sqlSessionTemplate.delete(DELETE_WISHLIST_BY_OWNER_STATEMENT_ID, buildWishOwnerParams(wishNo, userNo));
	}

	@Override
	public List<WishListDTO> selectWishListByUserAndPlace(Long userNo, Long placeNo) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_WISHLIST_BY_USER_AND_PLACE_STATEMENT_ID,
				buildWishLookupParams(userNo, placeNo));
	}

	@Override
	public int countWishListByUserAndPlaceAndCategory(Long userNo, Long placeNo, Long categoryNo) throws Exception {
		Integer count = sqlSessionTemplate.selectOne(COUNT_WISHLIST_BY_USER_AND_PLACE_AND_CATEGORY_STATEMENT_ID,
				buildWishCategoryCountParams(userNo, placeNo, categoryNo));
		return toCount(count);
	}

	@Override
	public int countWishUsersByPlaceNo(Long placeNo) throws Exception {
		return toCount(sqlSessionTemplate.selectOne(COUNT_WISH_USERS_BY_PLACE_NO_STATEMENT_ID, placeNo));
	}

	@Override
	public int countWishPlacesByAuthId(String authId) throws Exception {
		return toCount(sqlSessionTemplate.selectOne(COUNT_WISH_PLACES_BY_AUTH_ID_STATEMENT_ID, authId));
	}

	@Override
	public List<PlaceDTO> selectWishPlacesByAuthId(String authId) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_WISH_PLACES_BY_AUTH_ID_STATEMENT_ID, authId);
	}

	@Override
	public List<WishListDTO> selectWishListWithPlaceByAuthId(String authId) throws Exception {
		return sqlSessionTemplate.selectList(SELECT_WISH_LIST_WITH_PLACE_BY_AUTH_ID_STATEMENT_ID, authId);
	}

	private int toCount(Integer count) {
		return count == null ? 0 : count;
	}

	private Map<String, Object> buildCategoryUsageParams(Long categoryNo, Long userNo, String categoryIsUsed) {
		Map<String, Object> params = new HashMap<>();
		params.put("categoryNo", categoryNo);
		params.put("userNo", userNo);
		params.put("categoryIsUsed", categoryIsUsed);
		return params;
	}

	private Map<String, Object> buildWishOwnerParams(Long wishNo, Long userNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("wishNo", wishNo);
		params.put("userNo", userNo);
		return params;
	}

	private Map<String, Object> buildWishLookupParams(Long userNo, Long placeNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("userNo", userNo);
		params.put("placeNo", placeNo);
		return params;
	}

	private Map<String, Object> buildWishCategoryCountParams(Long userNo, Long placeNo, Long categoryNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("userNo", userNo);
		params.put("placeNo", placeNo);
		params.put("categoryNo", categoryNo);
		return params;
	}
}
