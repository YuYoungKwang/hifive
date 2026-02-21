<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>로그인</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
<link href="https://cdn.jsdelivr.net/npm/pretendard/dist/web/static/pretendard.css" rel="stylesheet">
</head>
<body>

    <div class="auth-container">
        <div class="auth-logo">
            <h1>#trip</h1>
            <p>여행을 더 쉽게, 더 체계적으로</p>
        </div>

        <c:if test="${param.error eq 'true'}">
            <p class="auth-message error">아이디 또는 비밀번호를 확인해 주세요.</p>
        </c:if>
        <c:if test="${param.logout eq 'true'}">
            <p class="auth-message success">로그아웃되었습니다.</p>
        </c:if>
        <c:if test="${param.signupSuccess eq 'true' || not empty message}">
            <p class="auth-message success">${empty message ? '회원가입이 완료되었습니다. 로그인해 주세요.' : message}</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="input-group">
                <label>아이디</label>
                <input type="text" name="username" required>
            </div>

            <div class="input-group">
                <label>비밀번호</label>
                <input type="password" name="password" required>
            </div>

            <div class="checkbox-group">
                <input id="remember-me" type="checkbox" name="remember-me">
                <label for="remember-me">로그인 상태 유지</label>
            </div>

            <button type="submit" class="auth-btn">로그인</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/signup">회원가입</a>
            <a href="${pageContext.request.contextPath}/auth/find-id">아이디 찾기</a>
            <a href="${pageContext.request.contextPath}/auth/find-password">비밀번호 찾기</a>
        </div>
    </div>

</body>
</html>
