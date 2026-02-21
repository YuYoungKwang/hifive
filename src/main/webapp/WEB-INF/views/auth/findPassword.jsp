<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>비밀번호 찾기</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
    <main class="auth-page">
    <div class="auth-container">
        <div class="auth-logo">
            <h1>비밀번호 찾기</h1>
            <p>아이디와 이메일을 입력하면 임시 비밀번호를 발급합니다.</p>
        </div>

        <form action="${pageContext.request.contextPath}/auth/find-password" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="input-group">
                <label>아이디</label>
                <input type="text" name="userId" required>
            </div>

            <div class="input-group">
                <label>이메일</label>
                <input type="email" name="email" required>
            </div>

            <button type="submit" class="auth-btn">임시 비밀번호 발급</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/login">로그인으로 돌아가기</a>
        </div>
    </div>
    </main>
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />
</body>
</html>
