package com.app.service;

import java.util.List;
import com.app.dto.UserTagMapDTO;

public interface UserTagMapService {

    /**
     * 사용자의 여행 성향 테스트 답변을 처리합니다.
     * 1. 회원일 경우: 기존 데이터 삭제 후 새로운 선택지 저장
     * 2. 공통: 답변 데이터를 분석하여 최종 여행자 타입 도출
     * * @param userNo 로그인한 사용자의 번호 (비회원일 경우 null)
     * @param selections 사용자가 선택한 10개 문항의 답변 리스트
     * @return 분석 결과 코드 또는 타입 명칭 (예: "CITY_EXPLORER")
     */
    String processUserAnalysis(Long userNo, List<UserTagMapDTO> selections);
    
    /**
     * 마이페이지 등에서 기존에 저장된 사용자의 성향 태그 리스트를 가져옵니다.
     * * @param userNo 사용자 번호
     * @return 저장된 태그 DTO 리스트
     */
    List<UserTagMapDTO> getUserTagHistory(Long userNo);
}