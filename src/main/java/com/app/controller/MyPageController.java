package com.app.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.dto.CommunityDTO;
import com.app.dto.PlaceReviewDTO;
import com.app.dto.TagMasterDTO;
import com.app.dto.UserTagMapDTO;
import com.app.dto.UsersDTO;
import com.app.dto.WishListDTO;
import com.app.service.PlaceService;
import com.app.service.UsersService;
import com.app.service.WishListService;
import com.app.service.impl.CommunityService;

@Controller
public class MyPageController {

	private static final int REVIEW_PAGE_SIZE = 10;
	private static final int REVIEW_PREVIEW_SIZE = 4;

	@Autowired
	private UsersService usersService;

	@Autowired
	private PlaceService placeService;

	@Autowired
	private WishListService wishListService;

	@Autowired
	private CommunityService communityService;

	@GetMapping({ "/mypage", "/mypage/", "/myPage", "/my-page", "/hashTrip/mypage" })
	public String mypage(
			@RequestParam(name = "placePage", defaultValue = "1") int placePage,
			@RequestParam(name = "communityPage", defaultValue = "1") int communityPage,
			@RequestParam(name = "placeSort", defaultValue = "latest") String placeSort,
			@RequestParam(name = "communitySort", defaultValue = "latest") String communitySort,
			@RequestParam(name = "placeExpanded", defaultValue = "N") String placeExpanded,
			@RequestParam(name = "communityExpanded", defaultValue = "N") String communityExpanded,
			Authentication authentication,
			Model model) throws Exception {
		String currentAuthId = resolveAuthenticatedAuthId(authentication);
		if (currentAuthId == null) {
			return "redirect:/auth/login";
		}

		UsersDTO usersDTO = usersService.getUserByAuthId(currentAuthId);
		List<UserTagMapDTO> userTagList = usersService.getUserTagsByAuthId(currentAuthId);
		List<TagMasterDTO> tagMasterList = usersService.getTagMasterList();

		String resolvedPlaceSort = normalizeReviewSort(placeSort);
		String resolvedCommunitySort = normalizeReviewSort(communitySort);
		boolean placeExpandedFlag = isExpanded(placeExpanded);
		boolean communityExpandedFlag = isExpanded(communityExpanded);

		int placeReviewCount = placeService.getMyPlaceReviewCount(currentAuthId);
		int placeTotalPages = Math.max(1, (int) Math.ceil(placeReviewCount / (double) REVIEW_PAGE_SIZE));
		int placeCurrentPage = Math.max(1, Math.min(placePage, placeTotalPages));
		List<PlaceReviewDTO> placeReviewList = placeReviewCount == 0
				? Collections.emptyList()
				: placeService.getMyPlaceReviews(currentAuthId, placeCurrentPage, REVIEW_PAGE_SIZE, resolvedPlaceSort);

		int communityReviewCount = communityService.getMyCommunityReviewCount(currentAuthId);
		int communityTotalPages = Math.max(1, (int) Math.ceil(communityReviewCount / (double) REVIEW_PAGE_SIZE));
		int communityCurrentPage = Math.max(1, Math.min(communityPage, communityTotalPages));
		List<CommunityDTO> communityReviewList = communityReviewCount == 0
				? Collections.emptyList()
				: communityService.getMyCommunityReviews(currentAuthId, communityCurrentPage, REVIEW_PAGE_SIZE, resolvedCommunitySort);

		int wishCount = wishListService.getWishPlaceCountByAuthId(currentAuthId);
		List<WishListDTO> wishEntryList = wishCount == 0
				? Collections.emptyList()
				: wishListService.getWishListWithPlaceByAuthId(currentAuthId);

		String kakaoMapAppKey = System.getenv("KAKAO_MAP_APP_KEY");
		if (kakaoMapAppKey == null || kakaoMapAppKey.isBlank()) {
			kakaoMapAppKey = System.getProperty("kakao.map.appkey", "");
		}

		model.addAttribute("usersDTO", usersDTO);
		model.addAttribute("userTagList", userTagList);
		model.addAttribute("tagMasterList", tagMasterList);
		model.addAttribute("currentAuthId", currentAuthId);
		model.addAttribute("placeReviewList", placeReviewList);
		model.addAttribute("placeReviewCount", placeReviewCount);
		model.addAttribute("placeCurrentPage", placeCurrentPage);
		model.addAttribute("placeTotalPages", placeTotalPages);
		model.addAttribute("placeSort", resolvedPlaceSort);
		model.addAttribute("placeExpanded", placeExpandedFlag);

		model.addAttribute("communityReviewList", communityReviewList);
		model.addAttribute("communityReviewCount", communityReviewCount);
		model.addAttribute("communityCurrentPage", communityCurrentPage);
		model.addAttribute("communityTotalPages", communityTotalPages);
		model.addAttribute("communitySort", resolvedCommunitySort);
		model.addAttribute("communityExpanded", communityExpandedFlag);

		model.addAttribute("reviewCount", placeReviewCount + communityReviewCount);
		model.addAttribute("wishCount", wishCount);
		model.addAttribute("wishEntryList", wishEntryList);
		model.addAttribute("reviewPageSize", REVIEW_PAGE_SIZE);
		model.addAttribute("reviewPreviewSize", REVIEW_PREVIEW_SIZE);
		model.addAttribute("kakaoMapAppKey", kakaoMapAppKey);
		return "mypage";
	}

	@GetMapping("/mypage/edit")
	public String mypageEdit(
			Authentication authentication,
			Model model) {
		String currentAuthId = resolveAuthenticatedAuthId(authentication);
		if (currentAuthId == null) {
			return "redirect:/auth/login";
		}

		UsersDTO usersDTO = usersService.getUserByAuthId(currentAuthId);
		if (usersDTO == null) {
			return "redirect:/mypage";
		}
		model.addAttribute("usersDTO", usersDTO);
		model.addAttribute("currentAuthId", currentAuthId);
		return "mypage-edit";
	}

	@PostMapping("/mypage/edit")
	public String updateMypageProfile(
			Authentication authentication,
			@ModelAttribute("usersDTO") UsersDTO usersDTO,
			RedirectAttributes redirectAttributes,
			Model model) {
		String currentAuthId = resolveAuthenticatedAuthId(authentication);
		if (currentAuthId == null) {
			return "redirect:/auth/login";
		}

		try {
			usersService.updateProfileByAuthId(currentAuthId, usersDTO);
			redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
			return "redirect:/mypage";
		} catch (IllegalArgumentException e) {
			model.addAttribute("usersDTO", usersDTO);
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("currentAuthId", currentAuthId);
			return "mypage-edit";
		}
	}

	@PostMapping("/mypage/password")
	public String updateMypagePassword(
			Authentication authentication,
			@RequestParam String currentPassword,
			@RequestParam String newPassword,
			@RequestParam String confirmPassword,
			RedirectAttributes redirectAttributes,
			Model model) {
		String currentAuthId = resolveAuthenticatedAuthId(authentication);
		if (currentAuthId == null) {
			return "redirect:/auth/login";
		}
		if (newPassword == null || !newPassword.equals(confirmPassword)) {
			UsersDTO usersDTO = usersService.getUserByAuthId(currentAuthId);
			model.addAttribute("usersDTO", usersDTO);
			model.addAttribute("currentAuthId", currentAuthId);
			model.addAttribute("passwordErrorMessage", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
			return "mypage-edit";
		}

		try {
			usersService.changePasswordByAuthId(currentAuthId, currentPassword, newPassword);
			redirectAttributes.addFlashAttribute("passwordMessage", "비밀번호가 변경되었습니다.");
			return "redirect:/mypage/edit";
		} catch (IllegalArgumentException e) {
			UsersDTO usersDTO = usersService.getUserByAuthId(currentAuthId);
			model.addAttribute("usersDTO", usersDTO);
			model.addAttribute("currentAuthId", currentAuthId);
			model.addAttribute("passwordErrorMessage", e.getMessage());
			return "mypage-edit";
		}
	}

	@PostMapping("/mypage/tags/add")
	@ResponseBody
	public Map<String, Object> addMyTag(
			@RequestParam String tagCode,
			Authentication authentication) {
		Map<String, Object> response = new HashMap<>();
		String currentAuthId = resolveAuthenticatedAuthId(authentication);
		if (currentAuthId == null) {
			response.put("success", false);
			response.put("message", "로그인이 필요합니다.");
			return response;
		}

		try {
			boolean added = usersService.addUserTagByAuthId(currentAuthId, tagCode);
			response.put("success", added);
			response.put("message", added ? "태그가 추가되었습니다." : "이미 선택된 태그입니다.");
		} catch (IllegalArgumentException e) {
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}

	@PostMapping("/mypage/tags/remove")
	@ResponseBody
	public Map<String, Object> removeMyTag(
			@RequestParam String tagCode,
			Authentication authentication) {
		Map<String, Object> response = new HashMap<>();
		String currentAuthId = resolveAuthenticatedAuthId(authentication);
		if (currentAuthId == null) {
			response.put("success", false);
			response.put("message", "로그인이 필요합니다.");
			return response;
		}

		try {
			boolean removed = usersService.removeUserTagByAuthId(currentAuthId, tagCode);
			response.put("success", removed);
			response.put("message", removed ? "태그가 제거되었습니다." : "제거할 태그가 없습니다.");
		} catch (IllegalArgumentException e) {
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}

	private String resolveAuthenticatedAuthId(Authentication authentication) {
		if (authentication == null
				|| !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		String authId = authentication.getName();
		if (authId == null || authId.trim().isEmpty()) {
			return null;
		}
		return authId.trim();
	}

	private String normalizeReviewSort(String sortType) {
		if (sortType == null) {
			return "latest";
		}
		String normalized = sortType.trim().toLowerCase();
		if ("oldest".equals(normalized) || "rating".equals(normalized)) {
			return normalized;
		}
		return "latest";
	}

	private boolean isExpanded(String expanded) {
		return "Y".equalsIgnoreCase(expanded);
	}
}
