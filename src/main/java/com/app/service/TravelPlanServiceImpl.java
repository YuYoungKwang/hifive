package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.TravelPlanDAO;
import com.app.dto.TravelPlanDTO;

@Service
public class TravelPlanServiceImpl implements TravelPlanService {

    @Autowired
    private TravelPlanDAO travelPlanDAO;

    @Override
    public List<TravelPlanDTO> findPublicTravelPlans() {
        return travelPlanDAO.getPublicTravelPlans();
    }

    @Override
    public TravelPlanDTO findTravelPlan(Long planNo) {
        return travelPlanDAO.getTravelPlanById(planNo);
    }

    @Override
    public List<TravelPlanDTO> findUserTravelPlans(Long userNo) {
        return travelPlanDAO.getTravelPlansByUserNo(userNo);
    }
}
