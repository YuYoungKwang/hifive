<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>아이디 찾기</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

    <div class="auth-container">
        <div class="auth-logo">
            <h1>아이디 찾기</h1>
            <p>가입 시 사용한 이메일을 입력하세요.</p>
        </div>

        <form action="${pageContext.request.contextPath}/auth/find-id" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="input-group">
                <label>이메일</label>
                <input type="email" name="email" required>
            </div>

            <button type="submit" class="auth-btn">아이디 찾기</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/login">로그인으로 돌아가기</a>
        </div>
    </div>

</body>
</html>
