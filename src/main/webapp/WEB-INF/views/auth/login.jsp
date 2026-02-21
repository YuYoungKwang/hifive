<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>로그인</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
<link href="https://cdn.jsdelivr.net/npm/pretendard/dist/web/static/pretendard.css" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
<link href="https://hangeul.pstatic.net/hangeul_static/css/nanum-square.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
    <main class="auth-page">
    <div class="auth-container">
        <div class="auth-logo">
            <h1>#trip</h1>
            <p>여행을 쉽고 체계적으로 시작해보세요</p>
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
        <c:if test="${param.oauth2Error eq 'invalid_client'}">
            <p class="auth-message error">SNS client-id/client-secret 값이 올바르지 않습니다.</p>
        </c:if>
        <c:if test="${param.oauth2Error eq 'redirect_uri_mismatch'}">
            <p class="auth-message error">Redirect URI 불일치입니다. SNS 개발자 콘솔 콜백 URL을 확인하세요.</p>
        </c:if>
        <c:if test="${param.oauth2Error eq 'access_denied'}">
            <p class="auth-message error">SNS 로그인 동의가 취소되었습니다.</p>
        </c:if>
        <c:if test="${param.oauth2Error eq 'invalid_scope'}">
            <p class="auth-message error">요청한 SNS 권한(scope)이 앱 설정과 맞지 않습니다.</p>
        </c:if>
        <c:if test="${param.oauth2Error eq 'oauth2_login_failed'}">
            <p class="auth-message error">SNS 로그인에 실패했습니다. oauth2.properties와 앱 설정을 확인하세요.</p>
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

        <div class="social-login">
            <a class="social-btn google" href="${pageContext.request.contextPath}/oauth2/authorization/google">
                <span class="social-content">
                    <img class="social-icon" src="<c:url value='/resources/images/social/google.svg' />" alt="Google">
                    <span class="social-text">구글로 시작하기</span>
                </span>
            </a>
            <a class="social-btn kakao" href="${pageContext.request.contextPath}/oauth2/authorization/kakao">
                <span class="social-content">
                    <img class="social-icon" src="<c:url value='/resources/images/social/kakao.svg' />" alt="Kakao">
                    <span class="social-text">카카오로 시작하기</span>
                </span>
            </a>
            <a class="social-btn naver" href="${pageContext.request.contextPath}/oauth2/authorization/naver">
                <span class="social-content">
                    <img class="social-icon" src="<c:url value='/resources/images/social/naver.svg' />" alt="Naver">
                    <span class="social-text">네이버로 시작하기</span>
                </span>
            </a>
        </div>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/signup">회원가입</a>
            <a href="${pageContext.request.contextPath}/auth/find-id">아이디 찾기</a>
            <a href="${pageContext.request.contextPath}/auth/find-password">비밀번호 찾기</a>
        </div>
    </div>
    </main>
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />
</body>
</html>
