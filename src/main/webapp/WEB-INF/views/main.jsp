<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Main</title>
<style>
body {
    margin: 0;
    font-family: Pretendard, sans-serif;
    background: #f4f6f9;
}
.wrap {
    max-width: 720px;
    margin: 80px auto;
    background: #fff;
    border-radius: 16px;
    padding: 30px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}
h1 {
    margin-top: 0;
}
.name {
    color: #0064ff;
    font-weight: 700;
}
button {
    margin-top: 20px;
    border: 0;
    border-radius: 10px;
    padding: 12px 16px;
    background: #0064ff;
    color: #fff;
    cursor: pointer;
}
</style>
</head>
<body>
    <div class="wrap">
        <h1>로그인 성공</h1>
        <p>
            현재 로그인 아이디:
            <span class="name">${pageContext.request.userPrincipal.name}</span>
        </p>

        <form action="${pageContext.request.contextPath}/auth/logout" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <button type="submit">로그아웃</button>
        </form>
    </div>
</body>
</html>
