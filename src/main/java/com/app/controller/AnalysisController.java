package com.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.dto.UserTagMapDTO;
import com.app.dto.UsersDTO;
import com.app.service.UserTagMapService;

@Controller
public class AnalysisController {
	
	@Autowired
	UserTagMapService userTagMapService;
	
	@GetMapping("/hashTrip/analysis")
	public String hashTrip_analysis() {
		
		return "analysis/analysis";
	}
	
//	@GetMapping("/hashTrip/analysisResult")
	public String hashTrip_analysisResult() {
		
		return "analysis/analysisResult";
	}
	
	@PostMapping("/hashTrip/analysisResult")
	@ResponseBody // 페이지 이동이 아닌 데이터를 리턴하기 위해 추가
    public String showTestResult(@RequestBody List<UserTagMapDTO> selections, 
                                 HttpSession session, 
                                 Model model) {
        
        // 1. 세션에서 로그인한 유저 정보를 가져옴
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        
        // 2. 회원이면 userNo를, 비회원이면 null을 서비스에 전달
        Long userNo = (loginUser != null) ? (long) loginUser.getUserNo() : null;

        // 3. 서비스 실행 (저장 + 분석 결과 반환)
        String finalResult = userTagMapService.processUserAnalysis(userNo, selections);

        // 4. 결과를 모델에 담아 결과 페이지(JSP)로 전달
        model.addAttribute("travelType", finalResult);
        model.addAttribute("isGuest", userNo == null);
        
        return "analysis/analysisResult"; // 결과 화면 JSP 경로
    }
	
	@GetMapping("/hashTrip/analysisResult")
	public String hashTrip_analysisResult(HttpSession session, Model model) {
	    // 세션에서 아까 저장한 결과 꺼내기
	    String finalResult = (String) session.getAttribute("finalResult");
	    model.addAttribute("travelType", finalResult);
	    
	    return "analysis/analysisResult";
	}
}
