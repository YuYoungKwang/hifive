<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>비밀번호 찾기 결과</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
    <main class="auth-page">
    <div class="auth-container">
        <div class="auth-logo">
            <h1>#trip</h1>
            <p>비밀번호 안내</p>
        </div>

        <div class="result-box">
            <h2>${message}</h2>
            <c:if test="${not empty temporaryPassword}">
                <p class="result-value">${temporaryPassword}</p>
                <p class="result-sub">로그인 후 반드시 비밀번호를 변경해 주세요.</p>
            </c:if>
        </div>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/login">로그인으로 이동</a>
            <a href="${pageContext.request.contextPath}/auth/find-password">다시 시도</a>
        </div>
    </div>
    </main>
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />
</body>
</html>
