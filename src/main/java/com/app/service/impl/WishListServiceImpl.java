package com.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.WishListDAO;
import com.app.dto.CategoryDTO;
import com.app.dto.PlaceDTO;
import com.app.dto.WishListDTO;
import com.app.service.WishListService;

@Service
public class WishListServiceImpl implements WishListService {

	@Autowired
	private WishListDAO wishListDAO;

	@Override
	public List<CategoryDTO> getCategoriesByAuthId(String authId) throws Exception {
		Long userNo = resolveUserNoByAuthId(authId);
		return wishListDAO.selectCategoriesByUserNo(userNo);
	}

	@Override
	public List<WishListDTO> getWishListByAuthIdAndPlaceNo(String authId, Long placeNo) throws Exception {
		Long userNo = resolveUserNoByAuthId(authId);
		return wishListDAO.selectWishListByUserAndPlace(userNo, placeNo);
	}

	@Override
	public CategoryDTO createCategory(String authId, String categoryType, String categoryIsUsed) throws Exception {
		Long userNo = resolveUserNoByAuthId(authId);
		String safeCategoryType = normalizeCategoryType(categoryType);
		String safeCategoryIsUsed = normalizeCategoryIsUsed(categoryIsUsed);

		CategoryDTO categoryDTO = new CategoryDTO();
		categoryDTO.setCategoryNo(wishListDAO.getNextCategoryNo());
		categoryDTO.setUserNo(userNo);
		categoryDTO.setCategoryType(safeCategoryType);
		categoryDTO.setCategoryIsUsed(safeCategoryIsUsed);

		int inserted = wishListDAO.insertCategory(categoryDTO);
		if (inserted <= 0) {
			throw new IllegalStateException("Failed to insert category.");
		}
		return categoryDTO;
	}

	@Override
	public boolean updateCategoryUsage(String authId, Long categoryNo, String categoryIsUsed) throws Exception {
		Long userNo = resolveUserNoByAuthId(authId);
		String safeCategoryIsUsed = normalizeCategoryIsUsed(categoryIsUsed);
		return wishListDAO.updateCategoryUsageByOwner(categoryNo, userNo, safeCategoryIsUsed) > 0;
	}

	@Override
	public WishListDTO createWishList(String authId, Long placeNo, Long categoryNo) throws Exception {
		Long userNo = resolveUserNoByAuthId(authId);
		if (placeNo == null || placeNo <= 0) {
			throw new IllegalArgumentException("placeNo is required.");
		}
		if (categoryNo == null || categoryNo <= 0) {
			throw new IllegalArgumentException("categoryNo is required.");
		}

		int alreadyCount = wishListDAO.countWishListByUserAndPlaceAndCategory(userNo, placeNo, categoryNo);
		if (alreadyCount > 0) {
			throw new IllegalArgumentException("이미 같은 카테고리에 찜한 장소입니다.");
		}

		WishListDTO wishListDTO = new WishListDTO();
		wishListDTO.setWishNo(wishListDAO.getNextWishNo());
		wishListDTO.setUserNo(userNo);
		wishListDTO.setPlaceNo(placeNo);
		wishListDTO.setCategoryNo(categoryNo);

		int inserted = wishListDAO.insertWishList(wishListDTO);
		if (inserted <= 0) {
			throw new IllegalStateException("Failed to insert wishlist.");
		}
		return wishListDTO;
	}

	@Override
	public boolean deleteWishList(String authId, Long wishNo) throws Exception {
		Long userNo = resolveUserNoByAuthId(authId);
		if (wishNo == null || wishNo <= 0) {
			throw new IllegalArgumentException("wishNo is required.");
		}
		return wishListDAO.deleteWishListByOwner(wishNo, userNo) > 0;
	}

	@Override
	public int getWishUserCountByPlaceNo(Long placeNo) throws Exception {
		if (placeNo == null || placeNo <= 0) {
			return 0;
		}
		return wishListDAO.countWishUsersByPlaceNo(placeNo);
	}

	@Override
	public int getWishPlaceCountByAuthId(String authId) throws Exception {
		String safeAuthId = normalizeAuthId(authId);
		return wishListDAO.countWishPlacesByAuthId(safeAuthId);
	}

	@Override
	public List<PlaceDTO> getWishPlacesByAuthId(String authId) throws Exception {
		String safeAuthId = normalizeAuthId(authId);
		return wishListDAO.selectWishPlacesByAuthId(safeAuthId);
	}

	@Override
	public List<WishListDTO> getWishListWithPlaceByAuthId(String authId) throws Exception {
		String safeAuthId = normalizeAuthId(authId);
		return wishListDAO.selectWishListWithPlaceByAuthId(safeAuthId);
	}

	private Long resolveUserNoByAuthId(String authId) throws Exception {
		String safeAuthId = normalizeAuthId(authId);
		Long userNo = wishListDAO.selectUserNoByAuthId(safeAuthId);
		if (userNo == null) {
			throw new IllegalArgumentException("Login user information is required.");
		}
		return userNo;
	}

	private String normalizeAuthId(String authId) {
		if (authId == null || authId.trim().isEmpty()) {
			throw new IllegalArgumentException("Login user information is required.");
		}
		return truncate(authId, 100);
	}

	private String normalizeCategoryType(String categoryType) {
		if (categoryType == null || categoryType.trim().isEmpty()) {
			throw new IllegalArgumentException("categoryType is required.");
		}
		return truncate(categoryType, 100);
	}

	private String normalizeCategoryIsUsed(String categoryIsUsed) {
		if (categoryIsUsed == null || categoryIsUsed.trim().isEmpty()) {
			return "Y";
		}
		String normalized = categoryIsUsed.trim().toUpperCase();
		if (!"Y".equals(normalized) && !"N".equals(normalized)) {
			throw new IllegalArgumentException("categoryIsUsed must be Y or N.");
		}
		return normalized;
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
}
