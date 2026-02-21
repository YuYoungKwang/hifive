package com.app.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.TravelPlanDAO;
import com.app.dto.TravelPlanDTO;

@Repository
public class TravelPlanDAOImpl implements TravelPlanDAO {

	private static final String GET_PUBLIC_TRAVEL_PLANS_STATEMENT_ID = "travel_plan_mapper.getPublicTravelPlans";
	private static final String GET_TRAVEL_PLAN_BY_ID_STATEMENT_ID = "travel_plan_mapper.getTravelPlanById";
	private static final String GET_TRAVEL_PLANS_BY_USER_NO_STATEMENT_ID = "travel_plan_mapper.getTravelPlansByUserNo";
	private static final String UPDATE_TRAVEL_PLAN_STATEMENT_ID = "travel_plan_mapper.updateTravelPlan";
    private static final String DELETE_TRAVEL_PLAN_BY_OWNER_STATEMENT_ID = "travel_plan_mapper.deleteTravelPlanByOwner";

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<TravelPlanDTO> getPublicTravelPlans() {
        return sqlSessionTemplate.selectList(GET_PUBLIC_TRAVEL_PLANS_STATEMENT_ID);
    }

    @Override
    public TravelPlanDTO getTravelPlanById(Long planNo) {
        return sqlSessionTemplate.selectOne(GET_TRAVEL_PLAN_BY_ID_STATEMENT_ID, planNo);
    }

    @Override
    public List<TravelPlanDTO> getTravelPlansByUserNo(Long userNo) {
        return sqlSessionTemplate.selectList(GET_TRAVEL_PLANS_BY_USER_NO_STATEMENT_ID, userNo);
    }
    
    @Override
    public int insertTravelPlan(TravelPlanDTO travelPlan) {
        return sqlSessionTemplate.insert("travel_plan_mapper.insertTravelPlan", travelPlan);
    }

    @Override
    public int updateTravelPlan(TravelPlanDTO travelPlan) {
        return sqlSessionTemplate.update(UPDATE_TRAVEL_PLAN_STATEMENT_ID, travelPlan);
    }

    @Override
    public int deleteTravelPlanByOwner(Long planNo, Long userNo) {
        return sqlSessionTemplate.delete(
                DELETE_TRAVEL_PLAN_BY_OWNER_STATEMENT_ID,
                Map.of("planNo", planNo, "userNo", userNo));
    }
    
}
