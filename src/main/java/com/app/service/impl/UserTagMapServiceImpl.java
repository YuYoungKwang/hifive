package com.app.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.UserTagMapDAO;
import com.app.dto.UserTagMapDTO;
import com.app.service.UserTagMapService;

@Service
public class UserTagMapServiceImpl implements UserTagMapService {

    @Autowired
    private UserTagMapDAO userTagMapDAO; // 작성하신 DAOImpl 주입

    @Override
    @Transactional // DB 작업 도중 에러가 나면 전부 취소(Rollback)합니다.
    public String processUserAnalysis(Long userNo, List<UserTagMapDTO> selections) {
        
        // 1. 회원 여부 판단 및 DB 저장
        if (userNo != null && userNo > 0) {
            // 기존 결과가 있으면 삭제 (최신 데이터 유지를 위해)
            userTagMapDAO.deleteByUserId(userNo);
            
            // 사용자가 선택한 10개의 태그를 하나씩 DB에 인서트
            for (UserTagMapDTO dto : selections) {
                dto.setUserNo(userNo); // DTO에 로그인한 유저 번호 세팅
                userTagMapDAO.insertTag(dto);
            }
        }

        // 2. 성향 분석 결과 계산 (회원/비회원 공통)
        // 실제로는 selections 리스트를 분석하는 로직이 들어갑니다.
        String resultType = calculateTravelType(selections);
        
        return resultType;
    }

    @Override
    public List<UserTagMapDTO> getUserTagHistory(Long userNo) {
        // 마이페이지용 조회 로직
        return userTagMapDAO.findByUserId(userNo);
    }

    // [내부 로직] 답변 데이터를 분석하여 타입을 결정하는 메서드
    private String calculateTravelType(List<UserTagMapDTO> selections) {
        // 예: 리스트에서 특정 카테고리가 가장 많은 것을 추출하거나 점수 합산
        // 지금은 우선 고정된 결과값을 반환하도록 설정합니다.
        return "액티브한 탐험가형 여행자"; 
    }
}