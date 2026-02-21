package com.app.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.TravelPlanDAO;
import com.app.dto.TravelPlanDTO;

@Repository
public class TravelPlanDAOImpl implements TravelPlanDAO {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<TravelPlanDTO> getPublicTravelPlans() {
        return sqlSessionTemplate.selectList("travel_plan_mapper.getPublicTravelPlans");
    }

    @Override
    public TravelPlanDTO getTravelPlanById(Long planNo) {
        return sqlSessionTemplate.selectOne("travel_plan_mapper.getTravelPlanById", planNo);
    }

    @Override
    public List<TravelPlanDTO> getTravelPlansByUserNo(Long userNo) {
        return sqlSessionTemplate.selectList("travel_plan_mapper.getTravelPlansByUserNo", userNo);
    }
}
