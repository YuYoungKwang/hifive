package com.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw new IllegalArgumentException("Review content is required.");
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
}
