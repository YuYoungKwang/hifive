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

/* ================== 2. 개인정보처리방침 ================== */
.privacy-wrapper {
    background-color: #f9f9f9;
    padding: 80px 20px;
    min-height: 100vh;
}

.privacy-container {
    max-width: 800px;
    margin: 0 auto;
    background: #fff;
    padding: 60px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.privacy-container h1 {
    font-size: 32px;
    color: #222;
    margin-bottom: 10px;
}

.effective-date {
    color: #888;
    font-size: 14px;
    margin-bottom: 40px;
    border-bottom: 1px solid #eee;
    padding-bottom: 20px;
}

.privacy-section {
    margin-bottom: 40px;
}

.privacy-section h2 {
    font-size: 20px;
    color: #333;
    margin-bottom: 15px;
    border-left: 4px solid #007bff;
    padding-left: 15px;
}

.privacy-section p, .privacy-section li {
    font-size: 15px;
    color: #555;
    line-height: 1.8;
}

.privacy-section ul {
    padding-left: 20px;
    margin-top: 10px;
}

.privacy-contact {
    margin-top: 50px;
    padding: 20px;
    background-color: #f0f7ff;
    border-radius: 8px;
    text-align: center;
}

</style>
</head>
<body>
	<!-- header -->
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<!-- header -->

	<div class="privacy-wrapper">
		<div class="privacy-container">
			<h1>개인정보처리방침</h1>
			<p class="effective-date">시행일자: 2026년 1월 1일</p>

			<section class="privacy-section">
				<h2>1. 수집하는 개인정보 항목</h2>
				<p>회사는 서비스 제공을 위해 아래와 같은 개인정보를 수집하고 있습니다.</p>
				<ul>
					<li><strong>회원가입 시:</strong> 이메일, 비밀번호, 닉네임</li>
					<li><strong>성향 분석 시:</strong> 여행 선호도 답변 데이터</li>
					<li><strong>서비스 이용 과정:</strong> 접속 로그, 쿠키, 서비스 이용 기록</li>
				</ul>
			</section>

			<section class="privacy-section">
				<h2>2. 개인정보의 수집 및 이용 목적</h2>
				<p>수집한 개인정보를 다음의 목적을 위해 활용합니다.</p>
				<ul>
					<li>회원 관리 및 본인 확인</li>
					<li>맞춤형 여행지 추천 및 성향 분석 결과 제공</li>
					<li>신규 서비스 개발 및 마케팅 활용(동의 시)</li>
				</ul>
			</section>

			<section class="privacy-section">
				<h2>3. 개인정보의 보유 및 이용기간</h2>
				<p>원칙적으로 개인정보 수집 및 이용목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다. 단, 관계법령의
					규정에 의하여 보존할 필요가 있는 경우 일정 기간 보관합니다.</p>
			</section>

			<section class="privacy-section">
				<h2>4. 이용자의 권리와 그 행사방법</h2>
				<p>이용자는 언제든지 등록되어 있는 자신의 개인정보를 조회하거나 수정할 수 있으며 가입해지를 요청할 수도
					있습니다.</p>
			</section>

			<div class="privacy-contact">
				<p>
					개인정보 보호 관련 문의: <a href="mailto:support@waygo.com">dpcks2553@naver.com</a>
				</p>
			</div>
		</div>
	</div>
</body>
</html>