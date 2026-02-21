package com.app.dao;

import java.util.List;

import com.app.dto.UserTagMapDTO;

public interface UserTagMapDAO {

	void deleteByUserId(Long userNo); // 기존 결과 삭제

	void insertTag(UserTagMapDTO dto); // 태그 저장

	List<UserTagMapDTO> findByUserId(Long userNo); // 마이페이지 조회
}
