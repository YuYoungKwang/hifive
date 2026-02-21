package com.app.service;

import java.util.List;

import com.app.dto.PlanDetailDTO;
import com.app.dto.TravelPlanDTO;

public interface TravelPlanService {

    List<TravelPlanDTO> findPublicTravelPlans();

    TravelPlanDTO findTravelPlan(Long planNo);

    List<TravelPlanDTO> findUserTravelPlans(Long userNo);

	int insertTravelPlan(TravelPlanDTO travelPlan);

    Long insertTravelPlanWithDetails(TravelPlanDTO travelPlan, List<PlanDetailDTO> planDetails);

    Long updateTravelPlanWithDetails(TravelPlanDTO travelPlan, List<PlanDetailDTO> planDetails, Long ownerUserNo);

    Long copyTravelPlanWithDetails(Long sourcePlanNo, Long targetUserNo, String copiedPlanTitle);

    int appendPlanDetailsToExistingPlan(Long sourcePlanNo, Long targetPlanNo, Long targetUserNo);

    int appendSinglePlanDetailToExistingPlan(Long sourcePlanNo, Long sourcePlanDetailNo, Long targetPlanNo, Long targetUserNo);

    void deleteTravelPlan(Long planNo, Long ownerUserNo);
}
