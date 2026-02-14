package com.app.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.app.dao.CommunityDAO;
import com.app.dto.CommunityDTO;

@Repository
public class CommunityDAOImpl implements CommunityDAO {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<CommunityDTO> getCommunityReviewsByPlanNo(Long planNo) {
        return sqlSessionTemplate.selectList("community_mapper.getCommunityReviewsByPlanNo", planNo);
    }

    @Override
    public int insertCommunityReview(CommunityDTO review) {
        if (review.getReviewNo() == null) {
            review.setReviewNo(nextCommunityReviewNo());
        }
        return sqlSessionTemplate.insert("community_mapper.insertCommunityReview", review);
    }

    private Long nextCommunityReviewNo() {
        Long seqValue = selectNextSequenceOrNull("community_mapper.nextCommunityReviewNo");
        Long tableBasedValue = sqlSessionTemplate.selectOne("community_mapper.nextCommunityReviewNoByTable");

        if (seqValue == null) {
            return tableBasedValue;
        }
        if (tableBasedValue == null) {
            return seqValue;
        }
        return Math.max(seqValue, tableBasedValue);
    }

    private Long selectNextSequenceOrNull(String statementId) {
        try {
            return sqlSessionTemplate.selectOne(statementId);
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message != null && message.contains("ORA-02289")) {
                return null;
            }
            throw e;
        }
    }
}
