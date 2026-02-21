package com.app.dao;

import java.util.List;

import com.app.dto.TravelPlanDTO;

public interface TravelPlanDAO {

    List<TravelPlanDTO> getPublicTravelPlans();

    TravelPlanDTO getTravelPlanById(Long planNo);

    List<TravelPlanDTO> getTravelPlansByUserNo(Long userNo);
}
