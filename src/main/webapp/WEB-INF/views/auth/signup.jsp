<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

    <div class="auth-container">
        <div class="auth-logo">
            <h1>회원가입</h1>
            <p>필수값을 입력하고 가입을 완료해 주세요.</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <p class="auth-message error">${errorMessage}</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth/signup" method="post" enctype="multipart/form-data">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="input-group">
                <label>아이디 (필수)</label>
                <input type="text" name="userId" maxlength="100" required>
            </div>

            <div class="input-group">
                <label>이메일 (필수)</label>
                <input type="email" name="email" maxlength="100" required>
            </div>

            <div class="input-group">
                <label>비밀번호 (필수)</label>
                <input type="password" name="password" minlength="8" maxlength="100" required>
            </div>

            <div class="input-group">
                <label>이름 (필수)</label>
                <input type="text" name="userName" maxlength="100" required>
            </div>

            <div class="input-group">
                <label>닉네임 (필수)</label>
                <input type="text" name="userNickName" maxlength="20" required>
            </div>

            <div class="input-group">
                <label>연락처 (필수)</label>
                <input type="text" name="userPhoneNumber" maxlength="50" placeholder="010-0000-0000" required>
            </div>

            <div class="input-group">
                <label>생년월일 (필수)</label>
                <input type="text" name="userRegistrationNo" maxlength="100" placeholder="YYYYMMDD" required>
            </div>

            <div class="input-group">
                <label>프로필 이미지 등록 (선택)</label>
                <input type="file" name="profileImage" accept="image/*">
            </div>

            <div class="input-group">
                <label>성별 (선택, M/F)</label>
                <input type="text" name="userGender" maxlength="1" placeholder="M 또는 F">
            </div>

            <div class="input-group">
                <label>우편번호 (선택)</label>
                <input type="text" name="userZipCode" maxlength="6">
            </div>

            <div class="input-group">
                <label>기본 주소 (선택)</label>
                <input type="text" name="userBaseAddress" maxlength="255">
            </div>

            <div class="input-group">
                <label>상세 주소 (선택)</label>
                <input type="text" name="userDetailAddress" maxlength="255">
            </div>

            <button type="submit" class="auth-btn">가입하기</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/auth/login">로그인으로 돌아가기</a>
        </div>
    </div>

</body>
</html>
