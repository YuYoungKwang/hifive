package com.app.dao;

import java.util.List;

import com.app.dto.PlanDetailDTO;

public interface PlanDetailDAO {

    int insertPlanDetail(PlanDetailDTO planDetail);

    List<PlanDetailDTO> getPlanDetailsByPlanNo(Long planNo);

    PlanDetailDTO getPlanDetailByPlanDetailNo(Long planDetailNo);

    int getMaxVisitOrderByPlanNo(Long planNo);

    int deletePlanDetailsByPlanNo(Long planNo);

    List<String> getStepNamesByPlanNo(Long planNo);

    List<String> getTagNamesByPlanNo(Long planNo);

    List<String> getTagCategoriesByPlanNo(Long planNo);

    String getRepresentativeMemoByPlanNo(Long planNo);
}
