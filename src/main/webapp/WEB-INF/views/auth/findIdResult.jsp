<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>아이디 찾기 결과</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
    <main class="auth-page">
    <div class="auth-container">
        <div class="auth-logo">
            <h1>#trip</h1>
            <p>아이디 찾기 결과</p>
        </div>

        <div class="result-box">
            <c:choose>
                <c:when test="${not empty foundId}">
                    <h2>가입 아이디</h2>
                    <p class="result-value">${foundId}</p>
                </c:when>
                <c:otherwise>
                    <h2>일치하는 계정을 찾지 못했습니다.</h2>
                    <p class="result-sub">입력한 이메일: ${searchedEmail}</p>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/login">로그인으로 이동</a>
            <a href="${pageContext.request.contextPath}/auth/find-id">다시 찾기</a>
        </div>
    </div>
    </main>
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />
</body>
</html>
