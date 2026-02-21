<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<footer class="main-footer">
    <div class="footer-container">
        <div class="footer-top">
            <div class="footer-brand">
                <h2 class="footer-logo">#Trip</h2>
                <p class="brand-desc">취향 기반 여행 유형 분석 플랫폼</p>
                <p class="brand-sub">나만의 여행을 찾아드립니다.</p>
            </div>

            <div class="footer-links">
                <h3>법적 고지</h3>
                <ul>
                    <li><a href="<c:url value='/hashTrip/privacy' />">개인정보처리방침</a></li>
                    <li><a href="<c:url value='/hashTrip/terms' />">이용약관</a></li>
                    <li><a href="<c:url value='/hashTrip/location' />">위치기반서비스</a></li>
                </ul>
            </div>

            <div class="footer-links">
                <h3>고객 지원</h3>
                <ul>
                    <li><a href="<c:url value='/hashTrip/faq' />">자주 묻는 질문</a></li>
                    <li><a href="<c:url value='/hashTrip/contact' />">1:1 문의</a></li>
                    <li><a href="<c:url value='/hashTrip/notice' />">공지사항</a></li>
                </ul>
            </div>
        </div>

        <div class="footer-bottom">
            <p>&copy; 2026 HASHTRIP. All rights reserved.</p>
        </div>
    </div>
</footer>
