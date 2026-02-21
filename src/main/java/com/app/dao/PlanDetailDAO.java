package com.app.dao;

import java.util.List;

import com.app.dto.PlanDetailDTO;

public interface PlanDetailDAO {

    List<PlanDetailDTO> getPlanDetailsByPlanNo(Long planNo);

    List<String> getStepNamesByPlanNo(Long planNo);

    List<String> getTagNamesByPlanNo(Long planNo);

    List<String> getTagCategoriesByPlanNo(Long planNo);

    String getRepresentativeMemoByPlanNo(Long planNo);
}
