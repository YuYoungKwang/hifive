package com.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.app.dao.CommunityDAO;
import com.app.dto.CommunityDTO;

@Service
public class CommunityService {

    @Autowired
    private CommunityDAO communityDAO;

    public List<CommunityDTO> getCommunityReviewsByPlanNo(Long planNo) {
        return communityDAO.getCommunityReviewsByPlanNo(planNo);
    }

    public CommunityDTO addCommunityReview(Long planNo, Long userNo, String reviewContent, Integer rating) {
        if (reviewContent == null || reviewContent.trim().isEmpty()) {
            throw new IllegalArgumentException("리뷰 내용을 입력해 주세요.");
        }

        CommunityDTO dto = new CommunityDTO();
        dto.setPlanNo(planNo);
        dto.setUserNo(userNo);
        dto.setReviewContent(reviewContent.trim());
        dto.setRating(normalizeRating(rating));

        communityDAO.insertCommunityReview(dto);

        List<CommunityDTO> reviews = communityDAO.getCommunityReviewsByPlanNo(planNo);
        if (reviews != null && !reviews.isEmpty()) {
            return reviews.get(0);
        }
        return dto;
    }

    public int getMyCommunityReviewCount(String authId) {
        String safeAuthId = normalizeAuthId(authId);
        return communityDAO.countCommunityReviewsByAuthId(safeAuthId);
    }

    public List<CommunityDTO> getMyCommunityReviews(String authId, int page, int pageSize, String sortType) {
        String safeAuthId = normalizeAuthId(authId);
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        int startRow = ((safePage - 1) * safePageSize) + 1;
        int endRow = safePage * safePageSize;
        String safeSortType = normalizeSortType(sortType);
        return communityDAO.getCommunityReviewsByAuthIdPaged(safeAuthId, startRow, endRow, safeSortType);
    }

    private int normalizeRating(Integer rating) {
        if (rating == null) {
            return 5;
        }
        if (rating < 1) {
            return 1;
        }
        if (rating > 5) {
            return 5;
        }
        return rating;
    }

    private String normalizeAuthId(String authId) {
        if (!StringUtils.hasText(authId)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        String trimmed = authId.trim();
        return trimmed.length() <= 100 ? trimmed : trimmed.substring(0, 100);
    }

    private String normalizeSortType(String sortType) {
        if (!StringUtils.hasText(sortType)) {
            return "latest";
        }
        String normalized = sortType.trim().toLowerCase();
        if ("oldest".equals(normalized) || "rating".equals(normalized)) {
            return normalized;
        }
        return "latest";
    }
}
