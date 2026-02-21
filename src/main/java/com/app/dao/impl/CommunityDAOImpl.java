package com.app.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.CommunityDAO;
import com.app.dto.CommunityDTO;

@Repository
public class CommunityDAOImpl implements CommunityDAO {

    private static final String GET_COMMUNITY_REVIEWS_BY_PLAN_NO_STATEMENT_ID = "community_mapper.getCommunityReviewsByPlanNo";
    private static final String INSERT_COMMUNITY_REVIEW_STATEMENT_ID = "community_mapper.insertCommunityReview";
    private static final String COUNT_COMMUNITY_REVIEWS_BY_AUTH_ID_STATEMENT_ID = "community_mapper.countCommunityReviewsByAuthId";
    private static final String GET_COMMUNITY_REVIEWS_BY_AUTH_ID_PAGED_STATEMENT_ID = "community_mapper.getCommunityReviewsByAuthIdPaged";

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<CommunityDTO> getCommunityReviewsByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList(GET_COMMUNITY_REVIEWS_BY_PLAN_NO_STATEMENT_ID, planNo);
    }

    @Override
    public int insertCommunityReview(CommunityDTO review) {
        return sqlSessionTemplate.insert(INSERT_COMMUNITY_REVIEW_STATEMENT_ID, review);
    }

    @Override
    public int countCommunityReviewsByAuthId(String authId) {
        Integer count = sqlSessionTemplate.selectOne(COUNT_COMMUNITY_REVIEWS_BY_AUTH_ID_STATEMENT_ID, authId);
        return count == null ? 0 : count;
    }

    @Override
    public List<CommunityDTO> getCommunityReviewsByAuthIdPaged(String authId, int startRow, int endRow, String sortType) {
        Map<String, Object> params = new HashMap<>();
        params.put("authId", authId);
        params.put("startRow", startRow);
        params.put("endRow", endRow);
        params.put("sortType", sortType);
        return sqlSessionTemplate.selectList(GET_COMMUNITY_REVIEWS_BY_AUTH_ID_PAGED_STATEMENT_ID, params);
    }
}
