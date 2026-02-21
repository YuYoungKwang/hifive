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

/* ================== 2. 위치기반서비스 ================== */
.terms-wrapper {
    background-color: #f9f9f9;
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

.terms-section h2 {
    font-size: 18px;
    color: #333;
    margin-bottom: 15px;
    background-color: #f0f7ff; /* 위치기반 서비스는 연한 블루 톤 배경 */
    padding: 10px 15px;
    border-radius: 6px;
    border-left: 4px solid #007bff; /* #Trip 포인트 컬러 */
}

</style>
</head>
<body>
	<!-- header -->
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<!-- header -->

	<div class="terms-wrapper">
		<div class="terms-container">
			<h1>위치기반서비스 이용약관</h1>
			<p class="effective-date">시행일자: 2026년 1월 1일</p>

			<section class="terms-section">
				<h2>제 1 조 (목적)</h2>
				<p>
					본 약관은 회원(<strong>#Trip</strong> 서비스 약관에 동의한 자)이 <strong>#Trip</strong>(이하
					"회사")이 제공하는 위치기반서비스(이하 "서비스")를 이용함에 있어 회사와 회원의 권리·의무 및 책임사항을 규정함을
					목적으로 합니다.
				</p>
			</section>

			<section class="terms-section">
				<h2>제 2 조 (서비스의 내용)</h2>
				<p>회사는 위치정보사업자로부터 수집한 위치정보를 활용하여 다음 각 호의 서비스를 제공합니다.</p>
				<ul>
					<li><strong>주변 여행지 추천:</strong> 이용자의 현재 위치를 기반으로 근거리 관광지, 맛집,
						숙박 시설 정보 제공</li>
					<li><strong>지역별 성향 분석:</strong> 특정 지역에서의 이용자 선호도를 분석한 맞춤형 콘텐츠
						제공</li>
					<li><strong>위치 기반 검색 결과:</strong> 검색어 입력 시 현재 위치를 기준으로 한 거리순
						결과 정렬</li>
				</ul>
			</section>

			<section class="terms-section">
				<h2>제 3 조 (위치정보 이용 요금)</h2>
				<p>
					회사가 제공하는 서비스는 기본적으로 <strong>무료</strong>입니다. 단, 무선 서비스 이용 시 발생하는 데이터
					통신료는 이용자가 가입한 통신사의 정책에 따릅니다.
				</p>
			</section>

			<section class="terms-section">
				<h2>제 4 조 (위치정보의 보존 및 파기)</h2>
				<p>
					회사는 위치정보의 보호 및 이용 등에 관한 법률 제16조 제2항에 따라 위치정보 이용·제공사실 확인자료를 위치정보시스템에
					자동으로 기록하며, 해당 자료는 <strong>6개월</strong>간 보관 후 지체 없이 파기합니다.
				</p>
			</section>

			<section class="terms-section">
				<h2>제 5 조 (이용자의 권리)</h2>
				<p>이용자는 언제든지 위치기반서비스 이용에 대한 동의의 전부 또는 일부를 철회할 수 있으며, 서비스 제공의
					일시적인 중지를 요구할 수 있습니다.</p>
			</section>

			<div class="terms-footer">
				<p>고객센터: dpcks2553@naver.com | 대표번호: 02-123-4567</p>
			</div>
		</div>
	</div>
</body>
</html>