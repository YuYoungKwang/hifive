<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav class="main-header">
    <div class="header-container">
        <a class="header-logo" href="<c:url value='/hashTrip' />">
            <strong style="color: #007bff;">#Trip</strong>
        </a>

        <div class="header-menu-wrapper">
            <ul class="nav-menu">
                <li><a href="<c:url value='/hashTrip' />">홈</a></li>
                <li><a href="<c:url value='/routes' />">추천 루트</a></li>
                <li><a href="<c:url value='/plan' />">여행 일정</a></li>
                <li><a href="<c:url value='/mypage' />">마이페이지</a></li>
            </ul>

            <div class="user-auth">
                <c:choose>
                    <c:when test="${pageContext.request.userPrincipal == null}">
                        <a href="<c:url value='/auth/login' />" class="btn-login">로그인</a>
                        <a href="<c:url value='/auth/signup' />" class="btn-signup">회원가입</a>
                    </c:when>
                    <c:otherwise>
                        <span class="user-info">
                            <strong><c:out value="${empty headerDisplayName ? pageContext.request.userPrincipal.name : headerDisplayName}" /></strong>님 환영합니다!
                        </span>
                        <form class="logout-form" action="<c:url value='/auth/logout' />" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <button type="submit" class="btn-logout">로그아웃</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
