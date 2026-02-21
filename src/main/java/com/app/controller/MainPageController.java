package com.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
	 
	@GetMapping({"/", "/main", "/hashTrip"}) // 메인 페이지
	public String hashTag() {
		return "mainPage/mainPage";
	}
	
	@GetMapping("/hashTrip/privacy") // 메인 페이지 개인정보처리방침
	public String hashTrip_privacy() {
		return "mainPage/mainPage-privacy";
	}
	
	@GetMapping("/hashTrip/terms") // 메인 페이지 이용약관
	public String hashTrip_terms() {
	    return "mainPage/mainPage-terms"; 
	}
	
	@GetMapping("/hashTrip/location") // 메인 페이지 위치기반 서비스
	public String hashTrip_locationTerms() {
	    return "mainPage/mainPage-location"; 
	}
	
	@GetMapping("/hashTrip/faq") // 메인 페이지 자주묻는질문
	public String hashTrip_faq() {
	    return "mainPage/mainPage-faq"; 
	}
	
	@GetMapping("/hashTrip/contact") // 메인 페이지 1:1문의
	public String hashTrip_contact() {
	    return "mainPage/mainPage-contact"; 
	}
	
	@GetMapping("/hashTrip/notice") // 메인 페이지 공지사항
	public String hashTrip_notice() {
	    return "mainPage/mainPage-notice"; 
	}
}
