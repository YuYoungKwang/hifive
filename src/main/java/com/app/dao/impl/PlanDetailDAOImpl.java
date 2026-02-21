package com.app.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.PlanDetailDAO;
import com.app.dto.PlanDetailDTO;

@Repository
public class PlanDetailDAOImpl implements PlanDetailDAO {

	private static final String INSERT_PLAN_DETAIL_STATEMENT_ID = "plan_detail_mapper.insertPlanDetail";
	private static final String GET_PLAN_DETAILS_BY_PLAN_NO_STATEMENT_ID = "plan_detail_mapper.getPlanDetailsByPlanNo";
	private static final String GET_PLAN_DETAIL_BY_PLAN_DETAIL_NO_STATEMENT_ID = "plan_detail_mapper.getPlanDetailByPlanDetailNo";
	private static final String GET_MAX_VISIT_ORDER_BY_PLAN_NO_STATEMENT_ID = "plan_detail_mapper.getMaxVisitOrderByPlanNo";
	private static final String DELETE_PLAN_DETAILS_BY_PLAN_NO_STATEMENT_ID = "plan_detail_mapper.deletePlanDetailsByPlanNo";
	private static final String GET_STEP_NAMES_BY_PLAN_NO_STATEMENT_ID = "plan_detail_mapper.getStepNamesByPlanNo";
	private static final String GET_TAG_NAMES_BY_PLAN_NO_STATEMENT_ID = "plan_detail_mapper.getTagNamesByPlanNo";
	private static final String GET_TAG_CATEGORIES_BY_PLAN_NO_STATEMENT_ID = "plan_detail_mapper.getTagCategoriesByPlanNo";
	private static final String GET_REPRESENTATIVE_MEMO_BY_PLAN_NO_STATEMENT_ID = "plan_detail_mapper.getRepresentativeMemoByPlanNo";

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public int insertPlanDetail(PlanDetailDTO planDetail) {
        return sqlSessionTemplate.insert(INSERT_PLAN_DETAIL_STATEMENT_ID, planDetail);
    }

    @Override
    public List<PlanDetailDTO> getPlanDetailsByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList(GET_PLAN_DETAILS_BY_PLAN_NO_STATEMENT_ID, planNo);
    }

    @Override
    public PlanDetailDTO getPlanDetailByPlanDetailNo(Long planDetailNo) {
        return sqlSessionTemplate.selectOne(GET_PLAN_DETAIL_BY_PLAN_DETAIL_NO_STATEMENT_ID, planDetailNo);
    }

    @Override
    public int getMaxVisitOrderByPlanNo(Long planNo) {
        Integer maxVisitOrder = sqlSessionTemplate.selectOne(GET_MAX_VISIT_ORDER_BY_PLAN_NO_STATEMENT_ID, planNo);
        return maxVisitOrder == null ? 0 : maxVisitOrder;
    }

    @Override
    public int deletePlanDetailsByPlanNo(Long planNo) {
        return sqlSessionTemplate.delete(DELETE_PLAN_DETAILS_BY_PLAN_NO_STATEMENT_ID, planNo);
    }

    @Override
    public List<String> getStepNamesByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList(GET_STEP_NAMES_BY_PLAN_NO_STATEMENT_ID, planNo);
    }

    @Override
    public List<String> getTagNamesByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList(GET_TAG_NAMES_BY_PLAN_NO_STATEMENT_ID, planNo);
    }

    @Override
    public List<String> getTagCategoriesByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList(GET_TAG_CATEGORIES_BY_PLAN_NO_STATEMENT_ID, planNo);
    }

    @Override
    public String getRepresentativeMemoByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectOne(GET_REPRESENTATIVE_MEMO_BY_PLAN_NO_STATEMENT_ID, planNo);
    }
}
