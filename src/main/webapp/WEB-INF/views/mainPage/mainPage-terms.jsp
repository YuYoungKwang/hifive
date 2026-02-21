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

/* ================== 2. 이용약관 ================== */
.terms-wrapper {
    background-color: #f9f9f9; /* 배경색 통일 */
    padding: 80px 20px;
    min-height: 100vh;
}

.terms-container {
    max-width: 800px;
    margin: 0 auto;
    background: #fff;
    padding: 60px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.terms-container h1 {
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

.terms-section {
    margin-bottom: 40px;
}

.terms-section h2 {
    font-size: 18px;
    color: #333;
    margin-bottom: 15px;
    background-color: #f8f9fa; /* 조항 강조를 위해 배경색 추가 */
    padding: 10px 15px;
    border-radius: 6px;
    border-left: 4px solid #555; /* 개인정보와 차별화된 다크 포인트 */
}

.terms-section p, .terms-section li {
    font-size: 14px;
    color: #555;
    line-height: 1.8;
}

.terms-section ul {
    padding-left: 20px;
    margin-top: 10px;
}

.terms-footer {
    margin-top: 50px;
    padding: 20px;
    text-align: center;
    border-top: 1px solid #eee;
}

.terms-footer p {
    font-size: 13px;
    color: #999;
}
</style>
</head>
<body>
	<!-- header -->
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<!-- header -->

	<div class="terms-wrapper">
		<div class="terms-container">
			<h1>이용약관</h1>
			<p class="effective-date">시행일자: 2026년 1월 1일</p>

			<section class="terms-section">
				<h2>제 1 조 (목적)</h2>
				<p>
					이 약관은 <strong>#Trip</strong>(이하 "회사")가 운영하는 웹사이트 및 서비스를 이용함에 있어 회사와
					이용자의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.
				</p>
			</section>

			<section class="terms-section">
				<h2>제 2 조 (용어의 정의)</h2>
				<ul>
					<li><strong>이용자:</strong> 약관에 따라 회사가 제공하는 서비스를 받는 회원 및 비회원을
						말합니다.</li>
					<li><strong>회원:</strong> 회사에 개인정보를 제공하여 회원등록을 한 자로서, 회사의 정보를
						지속적으로 제공받으며 서비스를 계속적으로 이용할 수 있는 자를 말합니다.</li>
					<li><strong>서비스:</strong> 회사가 구현하여 이용자가 이용할 수 있는 여행 성향 분석 및 관련
						콘텐츠 일체를 의미합니다.</li>
				</ul>
			</section>

			<section class="terms-section">
				<h2>제 3 조 (약관의 명시와 개정)</h2>
				<p>회사는 이 약관의 내용을 이용자가 쉽게 알 수 있도록 서비스 초기 화면에 게시합니다. 회사는 관련법을 위배하지
					않는 범위에서 이 약관을 개정할 수 있습니다.</p>
			</section>

			<section class="terms-section">
				<h2>제 4 조 (서비스의 제공 및 변경)</h2>
				<p>회사는 이용자에게 다음과 같은 업무를 수행합니다.</p>
				<ul>
					<li>여행 성향 테스트 및 결과 분석 제공</li>
					<li>개인별 맞춤 여행지 및 콘텐츠 추천</li>
					<li>기타 회사가 정하는 업무</li>
				</ul>
			</section>

			<section class="terms-section">
				<h2>제 5 조 (서비스 이용의 제한)</h2>
				<p>회사는 이용자가 다음 각 호에 해당하는 경우 서비스 이용을 제한하거나 회원자격을 상실시킬 수 있습니다.</p>
				<ul>
					<li>가입 신청 시 허위 내용을 등록한 경우</li>
					<li>타인의 서비스 이용을 방해하거나 정보를 도용하는 경우</li>
					<li>법령 또는 이 약관이 금지하는 행위를 하는 경우</li>
				</ul>
			</section>

			<div class="terms-footer">
				<p>본 약관에 명시되지 않은 사항은 관계법령 및 상관례에 따릅니다.</p>
			</div>
		</div>
	</div>
</body>
</html>