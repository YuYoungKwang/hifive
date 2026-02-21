<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>#Trip</title>
<style type="text/css">

/* ================== 1. 상단 헤더바 영역 ================== */
.main-header {
	background-color: #ffffff;
	border-bottom: 1px solid #eee;
	padding: 10px 0;
	position: sticky;
	top: 0;
	z-index: 1000;
}

.header-container {
	display: flex;
	justify-content: space-between;
	align-items: center;
	max-width: 1200px;
	margin: 0 auto;
	padding: 0 20px;
}

.header-logo {
	font-size: 24px;
	text-decoration: none;
	flex: 1;
}

.header-menu-wrapper {
	flex: 2;
	display: flex;
	justify-content: center;
}

.nav-menu {
	display: flex;
	list-style: none;
	margin: 0;
	padding: 0;
	gap: 30px;
}

.nav-menu li a {
	text-decoration: none;
	color: #333;
	font-weight: 500;
}

.nav-menu li a:hover {
	color: #007bff;
}

.user-auth {
	flex: 1;
	display: flex;
	justify-content: flex-end;
	align-items: center;
	gap: 15px;
}

.btn-login, .btn-signup, .btn-logout {
	text-decoration: none;
	font-size: 14px;
	padding: 6px 12px;
	border-radius: 4px;
}

.btn-login {
	color: #555;
}

.btn-signup {
	background-color: #007bff;
	color: #fff;
}

.user-info {
	font-size: 14px;
	color: #666;
}

/* ================== 2. 1:1 문의	 ================== */
.contact-wrapper {
    background-color: #f9f9f9;
    padding: 80px 20px;
    min-height: 100vh;
}

.contact-container {
    max-width: 700px;
    margin: 0 auto;
    background: #fff;
    padding: 50px;
    border-radius: 15px;
    box-shadow: 0 4px 25px rgba(0,0,0,0.06);
}

.contact-header {
    text-align: center;
    margin-bottom: 40px;
}

.contact-header h1 {
    font-size: 28px;
    color: #222;
    margin-bottom: 10px;
}

.contact-form .form-group {
    margin-bottom: 25px;
}

.form-group label {
    display: block;
    font-weight: 600;
    margin-bottom: 10px;
    color: #444;
    font-size: 14px;
}

/* 입력창 공통 스타일 */
.form-group input, 
.form-group select, 
.form-group textarea {
    width: 100%;
    padding: 12px 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 15px;
    box-sizing: border-box; /* 패딩이 넓이에 영향 안 주게 */
    transition: border-color 0.3s;
}

.form-group input:focus, 
.form-group select:focus, 
.form-group textarea:focus {
    outline: none;
    border-color: #007bff;
}

/* 동의 체크박스 */
.form-agreement {
    margin-bottom: 30px;
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 14px;
    color: #666;
}

/* 버튼 영역 */
.form-actions {
    display: flex;
    gap: 10px;
}

.submit-btn {
    flex: 2;
    padding: 15px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
    transition: background 0.2s;
}

.submit-btn:hover {
    background-color: #0056b3;
}

.cancel-btn {
    flex: 1;
    padding: 15px;
    background-color: #eee;
    color: #555;
    border: none;
    border-radius: 8px;
    cursor: pointer;
}

.contact-info {
    margin-top: 40px;
    padding-top: 25px;
    border-top: 1px solid #eee;
    text-align: center;
    font-size: 13px;
    color: #999;
    line-height: 1.6;
}
</style>
</head>
<body>
	<!-- header -->
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<!-- header -->

	<div class="contact-wrapper">
		<div class="contact-container">
			<div class="contact-header">
				<h1>1:1 문의하기</h1>
				<p>궁금한 점이나 건의사항을 남겨주시면 정성껏 답변해 드리겠습니다.</p>
			</div>

			<form action="/contact/submit" method="post" class="contact-form">
				<div class="form-group">
					<label for="category">문의 유형</label> <select id="category"
						name="category" required>
						<option value="">유형을 선택해 주세요</option>
						<option value="service">서비스 이용 문의</option>
						<option value="account">계정/로그인 관련</option>
						<option value="error">오류 제보</option>
						<option value="proposal">제휴 및 건의사항</option>
						<option value="etc">기타</option>
					</select>
				</div>

				<div class="form-group">
					<label for="title">제목</label> <input type="text" id="title"
						name="title" placeholder="제목을 입력해 주세요" required>
				</div>

				<div class="form-group">
					<label for="email">답변 받을 이메일</label> <input type="email" id="email"
						name="email" placeholder="example@mail.com" required>
				</div>

				<div class="form-group">
					<label for="content">문의 내용</label>
					<textarea id="content" name="content" rows="10"
						placeholder="내용을 상세히 작성해 주세요. (최대 2000자)" required></textarea>
				</div>

				<div class="form-agreement">
					<input type="checkbox" id="agree" required> <label
						for="agree">개인정보 수집 및 이용에 동의합니다. (문의 답변 목적)</label>
				</div>

				<div class="form-actions">
					<button type="submit" class="submit-btn">문의 접수하기</button>
					<button type="button" class="cancel-btn" onclick="history.back();">취소</button>
				</div>
			</form>

			<div class="contact-info">
				<p>운영시간: 평일 09:00 ~ 18:00 (주말/공휴일 제외)</p>
				<p>평균 답변 기간: 접수 후 1~2 영업일 이내</p>
			</div>
		</div>
	</div>
</body>
</html>