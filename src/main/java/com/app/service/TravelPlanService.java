package com.app.service;

import java.util.List;

import com.app.dto.TravelPlanDTO;

public interface TravelPlanService {

    List<TravelPlanDTO> findPublicTravelPlans();

    TravelPlanDTO findTravelPlan(Long planNo);

    List<TravelPlanDTO> findUserTravelPlans(Long userNo);
}
