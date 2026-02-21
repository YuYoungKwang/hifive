package com.app.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.PlanDetailDAO;
import com.app.dto.PlanDetailDTO;

@Repository
public class PlanDetailDAOImpl implements PlanDetailDAO {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<PlanDetailDTO> getPlanDetailsByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList("plan_detail_mapper.getPlanDetailsByPlanNo", planNo);
    }

    @Override
    public List<String> getStepNamesByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList("plan_detail_mapper.getStepNamesByPlanNo", planNo);
    }

    @Override
    public List<String> getTagNamesByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList("plan_detail_mapper.getTagNamesByPlanNo", planNo);
    }

    @Override
    public List<String> getTagCategoriesByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList("plan_detail_mapper.getTagCategoriesByPlanNo", planNo);
    }

    @Override
    public String getRepresentativeMemoByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectOne("plan_detail_mapper.getRepresentativeMemoByPlanNo", planNo);
    }
}
