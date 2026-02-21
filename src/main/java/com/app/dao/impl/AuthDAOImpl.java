package com.app.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.app.dao.AuthDAO;
import com.app.dto.UsersDTO;

@Repository
public class AuthDAOImpl implements AuthDAO {

    private static final String NAMESPACE = "auth.";
    private static final String ACTIVE_STATUS = "A";
    private static final String DEFAULT_SNS_TYPE = "LOCAL";

    private final org.mybatis.spring.SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public AuthDAOImpl(org.mybatis.spring.SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Override
    public boolean existsAuthId(String userId) {
        Integer count = sqlSessionTemplate.selectOne(NAMESPACE + "countByAuthId", userId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsEmail(String email) {
        Integer count = sqlSessionTemplate.selectOne(NAMESPACE + "countByEmail", email);
        return count != null && count > 0;
    }

    @Override
    public Long nextUserNo() {
        Long seqValue = selectNextSequenceOrNull(
                NAMESPACE + "nextUserNo",
                NAMESPACE + "nextUserNoAlt");
        Long tableBasedValue = sqlSessionTemplate.selectOne(NAMESPACE + "nextUserNoByTable");
        if (seqValue == null) {
            return tableBasedValue;
        }
        return Math.max(seqValue, tableBasedValue);
    }

    @Override
    public Long nextUserAuthNo() {
        Long seqValue = selectNextSequenceOrNull(
                NAMESPACE + "nextUserAuthNo",
                NAMESPACE + "nextUserAuthNoAlt");
        Long tableBasedValue = sqlSessionTemplate.selectOne(NAMESPACE + "nextUserAuthNoByTable");
        if (seqValue == null) {
            return tableBasedValue;
        }
        return Math.max(seqValue, tableBasedValue);
    }

    @Override
    public Long nextUserAddressNo() {
        Long seqValue = selectNextSequenceOrNull(
                NAMESPACE + "nextUserAddressNo",
                NAMESPACE + "nextUserAddressNoAlt");
        Long tableBasedValue = sqlSessionTemplate.selectOne(NAMESPACE + "nextUserAddressNoByTable");
        if (seqValue == null) {
            return tableBasedValue;
        }
        return Math.max(seqValue, tableBasedValue);
    }

    @Override
    public int insertUser(UsersDTO user) {
        return sqlSessionTemplate.insert(NAMESPACE + "insertUser", user);
    }

    @Override
    public int insertUserAuthentication(UsersDTO user, Long userAuthNo, String encodedPassword) {
        if (user.getAuthSnsType() == null) {
            user.setAuthSnsType(DEFAULT_SNS_TYPE);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("userAuthNo", userAuthNo);
        params.put("encodedPassword", encodedPassword);
        return sqlSessionTemplate.insert(NAMESPACE + "insertUserAuthentication", params);
    }

    @Override
    public int insertUserAddress(UsersDTO user) {
        return sqlSessionTemplate.insert(NAMESPACE + "insertUserAddress", user);
    }

    @Override
    public UsersDTO findByAuthId(String userId) {
        return sqlSessionTemplate.selectOne(NAMESPACE + "findByAuthId", userId);
    }

    @Override
    public String findAuthIdByEmail(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("activeStatus", ACTIVE_STATUS);
        return sqlSessionTemplate.selectOne(NAMESPACE + "findAuthIdByEmail", params);
    }

    @Override
    public int updatePasswordByAuthIdAndEmail(String userId, String email, String encodedPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("email", email);
        params.put("encodedPassword", encodedPassword);
        params.put("activeStatus", ACTIVE_STATUS);
        return sqlSessionTemplate.update(NAMESPACE + "updatePasswordByAuthIdAndEmail", params);
    }

    @Override
    public int updateSocialAdditionalInfoByAuthId(String userId, String userPhoneNumber, String userRegistrationNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("userPhoneNumber", userPhoneNumber);
        params.put("userRegistrationNo", userRegistrationNo);
        params.put("defaultSnsType", DEFAULT_SNS_TYPE);
        params.put("activeStatus", ACTIVE_STATUS);
        return sqlSessionTemplate.update(NAMESPACE + "updateSocialAdditionalInfoByAuthId", params);
    }

    private Long selectNextSequenceOrNull(String... statementIds) {
        DataAccessException last = null;
        for (String statementId : statementIds) {
            try {
                return sqlSessionTemplate.selectOne(statementId);
            } catch (DataAccessException e) {
                String message = e.getMessage();
                if (message != null && message.contains("ORA-02289")) {
                    last = e;
                    continue;
                }
                throw e;
            }
        }
        if (last != null) {
            return null;
        }
        throw new IllegalStateException("No available sequence statement.");
    }
}
