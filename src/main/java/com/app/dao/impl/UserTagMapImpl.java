package com.app.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.UserTagMapDAO;
import com.app.dto.UserTagMapDTO;

@Repository
public class UserTagMapImpl implements UserTagMapDAO {

	private static final String DELETE_USER_TAGS_BY_USER_NO_STATEMENT_ID = "users_mapper.deleteUserTagsByUserNo";
	private static final String INSERT_USER_TAG_BY_USER_NO_STATEMENT_ID = "users_mapper.insertUserTagByUserNo";
	private static final String FIND_USER_TAGS_BY_USER_NO_STATEMENT_ID = "users_mapper.findUserTagsByUserNo";

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public void deleteByUserId(Long userNo) {
		sqlSessionTemplate.delete(DELETE_USER_TAGS_BY_USER_NO_STATEMENT_ID, userNo);
	}

	@Override
	public void insertTag(UserTagMapDTO dto) {
		sqlSessionTemplate.insert(INSERT_USER_TAG_BY_USER_NO_STATEMENT_ID, dto);
	}

	@Override
	public List<UserTagMapDTO> findByUserId(Long userNo) {
		return sqlSessionTemplate.selectList(FIND_USER_TAGS_BY_USER_NO_STATEMENT_ID, userNo);
	}
}
