<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>#Trip</title>

<style type="text/css">
/* 1. 스크롤 스냅 활성화 */
html {
	scroll-snap-type: y proximity; /* 강제 고정 대신 근접했을 때만 고정 */
	scroll-behavior: smooth; /* 부드러운 스크롤 */
}

body {
	margin: 0;
	padding: 0;
}

/* 2. 각 섹션을 화면 전체 크기로 설정 */
section {
	scroll-snap-align: start;
	scroll-snap-stop: always; /* 스크롤 한 번에 여러 섹션을 지나치지 않도록 방지 */
	height: 100vh;
	width: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
	box-sizing: border-box;
	/* [추가] 헤더 높이가 보통 60px 정도이므로 그만큼 위쪽 여백을 줍니다 */
	padding-top: 60px;
}

/* 헤더가 고정(sticky)되어 있다면 스냅 위치가 어긋날 수 있으므로 
   헤더 높이만큼 여백을 고려하거나, 헤더를 제외하고 구성하는 것이 좋습니다. */

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

/* ================== 2. 성향 분석 영역 ================== */
.analysis-section {
	background: linear-gradient(135deg, #f0f7ff 0%, #ffffff 100%);
	padding: 80px 0;
	display: flex;
	justify-content: center;
}

.analysis-container {
	max-width: 1200px;
	width: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 20px;
}

.analysis-content {
	flex: 1;
}

.badge {
	background-color: #e7f1ff;
	color: #007bff;
	padding: 5px 12px;
	border-radius: 20px;
	font-size: 14px;
	font-weight: 600;
	margin-bottom: 20px;
	display: inline-block;
}

.analysis-content h1 {
	font-size: 42px;
	color: #222;
	margin-bottom: 20px;
	line-height: 1.2;
}

.analysis-content p {
	font-size: 18px;
	color: #666;
	line-height: 1.6;
	margin-bottom: 40px;
}

.btn-analysis {
	display: inline-block;
	background-color: #007bff;
	color: white;
	padding: 16px 32px;
	font-size: 18px;
	font-weight: 600;
	text-decoration: none;
	border-radius: 8px;
	transition: transform 0.2s, background-color 0.2s;
	box-shadow: 0 4px 15px rgba(0, 123, 255, 0.3);
}

.btn-analysis:hover {
	background-color: #0056b3;
	transform: translateY(-3px);
}

.sub-text {
	margin-top: 15px;
	font-size: 13px;
	color: #999;
}

/* ================== 3. 지도 및 플로팅 카드 애니메이션 ================== */
.analysis-image {
	flex: 1;
	display: flex;
	justify-content: center;
	position: relative;
	height: 450px; /* 지도가 보일 수 있도록 높이 확보 */
}

.map-background {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 100%;
	height: 100%;
	background-image: url('/img/korea.png');
	background-size: contain;
	background-repeat: no-repeat;
	background-position: center;
	opacity: 0.2;
	filter: grayscale(100%) brightness(1.1);
	z-index: 1;
	animation: map-fade 2s ease-in-out forwards;
}

.floating-card {
	position: absolute;
	background: white;
	padding: 12px 22px;
	border-radius: 50px;
	box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
	font-weight: bold;
	font-size: 15px;
	z-index: 10;
	animation: floating 4s infinite ease-in-out;
}

/* 개별 카드 위치 설정 - 지도 레이아웃에 맞춰 분산 */
.floating-card:nth-of-type(1) {
	top: 10%;
	left: 10%;
	color: #007bff;
	animation-delay: 0s;
}

.floating-card:nth-of-type(2) {
	top: 35%;
	right: 5%;
	color: #ff4757;
	animation-delay: 0.5s;
	font-size: 18px;
}

.floating-card:nth-of-type(3) {
	bottom: 20%;
	left: 15%;
	color: #2ed573;
	animation-delay: 1.2s;
}

.floating-card:nth-of-type(4) {
	top: 25%;
	left: 45%;
	color: #ffa502;
	animation-delay: 0.8s;
}

.floating-card:nth-of-type(5) {
	bottom: 45%;
	left: 5%;
	color: #747d8c;
	animation-delay: 1.5s;
	font-size: 13px;
}

.floating-card:nth-of-type(6) {
	top: 65%;
	right: 20%;
	color: #a4b0be;
	animation-delay: 2s;
}

.floating-card:nth-of-type(7) {
	bottom: 5%;
	right: 30%;
	color: #5352ed;
	animation-delay: 0.3s;
}

/* 애니메이션 키프레임 (하나로 통합) */
@
keyframes floating { 0%, 100% {
	transform: translate(0, 0);
}

50




%
{
transform




:




translate


(




0
,
-15px




)


;
}
}
@
keyframes map-fade {from { opacity:0;
	transform: translate(-50%, -48%);
}

to {
	opacity: 0.2;
	transform: translate(-50%, -50%);
}

}

.sub-text {
    /* 기존에 가지고 계신 스타일 코드들... (font-size, color 등) */
    
    transition: opacity 0.5s ease-in-out; /* 이 줄만 추가해 주세요! */
    opacity: 1;
}

/* 페이드 아웃 상태를 위한 클래스 */
.fade-out {
    opacity: 0;
}

/* ================== 4. 여행지 추천 영역 ================== */
/* 검색창 스타일 */
.search-bar-wrapper {
	max-width: 500px;
	margin: 0 auto 50px auto; /* 중앙 정렬 및 하단 여백 */
}

.search-input-box {
	position: relative;
	display: flex;
	align-items: center;
	background: #fff;
	border: 2px solid #007bff;
	border-radius: 50px;
	padding: 5px 20px;
	box-shadow: 0 4px 15px rgba(0, 123, 255, 0.1);
	transition: all 0.3s ease;
}

.search-input-box:focus-within {
	box-shadow: 0 4px 20px rgba(0, 123, 255, 0.2);
	transform: translateY(-2px);
}

.search-input-box input {
	border: none;
	outline: none;
	width: 100%;
	padding: 12px 10px;
	font-size: 16px;
}

.search-icon {
	font-style: normal;
	font-size: 18px;
	margin-right: 10px;
}

/* 여행지 카드 레이아웃 */
.recommend-cards {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
	gap: 30px;
	padding: 20px 0;
}

.travel-card {
	background: #fff;
	border-radius: 20px;
	overflow: hidden;
	box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
	transition: transform 0.3s ease;
}

.travel-card:hover {
	transform: translateY(-10px);
}

.card-image {
	height: 200px;
	background-size: cover;
	background-position: center;
	position: relative;
}

.tag {
	position: absolute;
	top: 15px;
	left: 15px;
	background: #007bff;
	color: #fff;
	padding: 4px 12px;
	border-radius: 20px;
	font-size: 12px;
}

.card-info {
	padding: 20px;
}

.card-info h3 {
	margin: 0 0 10px 0;
	font-size: 18px;
}

.card-info p {
	color: #777;
	font-size: 14px;
	margin-bottom: 20px;
}

.card-footer {
	display: flex;
	justify-content: space-between;
	font-size: 13px;
	font-weight: bold;
	color: #333;
}

/* 추천 섹션 전체의 상단 여백 줄이기 */
.recommend-section {
	/* 기존 80px에서 20px~40px 정도로 대폭 줄임 */
	padding-top: 30px !important;
	padding-bottom: 80px;
	background-color: #ffffff;
	scroll-snap-align: start;
	height: 100vh; /* 전체 화면 높이 유지 */
	box-sizing: border-box;
}

.recommend-container {
	max-width: 1200px; /* 부모 넓이를 충분히 넓게 잡아주세요 */
	width: 100%;
	margin: 0 auto;
	padding: 0 20px;
	box-sizing: border-box; /* 패딩이 넓이에 영향을 주지 않도록 설정 */
}

/* 제목 영역의 위쪽 마진 제거 */
.section-title {
	text-align: center;
	margin-top: 0; /* 위쪽 마진을 아예 없앰 */
	margin-bottom: 30px; /* 검색창과의 간격 */
}

.section-title h2 {
	margin-top: 0; /* h2 태그 자체의 기본 마진 제거 */
	font-size: 32px;
	line-height: 1.2;
}

/* ================== 5. footer ================== */
.main-footer {
    background-color: #ffffff;
    color: #333;
    border-top: 1px solid #eee;
    padding: 25px 0 15px 0; 
    scroll-snap-align: end; 
}

.footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
}

.footer-top {
    display: flex;
    justify-content: space-between;
    align-items: flex-start; /* 상단 정렬 */
    flex-wrap: wrap;
    margin-bottom: 15px; /* 카피라이트와의 간격 최소화 */
    gap: 20px;
}

.footer-brand {
    flex: 1.5;
    min-width: 200px;
}

.footer-logo {
    font-size: 20px; /* 로고 크기도 조금 더 작게 */
    font-weight: 800;
    color: #007bff;
    margin-bottom: 5px; /* 간격 축소 */
}

.brand-desc {
    font-size: 13px;
    font-weight: 600;
    margin: 0; /* 마진 제거 */
    color: #444;
}

.brand-sub {
    font-size: 12px;
    color: #888;
}

/* 메뉴 영역 간격 압축 */
.footer-links h3 {
    font-size: 13px;
    font-weight: 700;
    margin-bottom: 8px; /* 제목 아래 간격 축소 */
    color: #222;
}

.footer-links ul li {
    margin-bottom: 4px; /* 리스트 사이 간격 최소화 */
}

.footer-links ul li a {
    color: #666;
    font-size: 12px; /* 폰트 크기 살짝 축소 */
}

/* 하단 카피라이트 영역 압축 */
.footer-bottom {
    border-top: 1px solid #f4f4f4;
    padding-top: 10px; /* 위쪽 라인과의 간격 축소 */
    text-align: center;
}

.footer-bottom p {
    font-size: 11px;
    color: #aaa;
    margin: 0;
}
</style>

</head>
<body>

	<!-- header -->
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<!-- header -->

	<!-- 성향 분석 -->
	<section class="analysis-section">
		<div class="analysis-container">
			<div class="analysis-content">
				<span class="badge">Travel Type Test</span>
				<h1>나의 여행 성향이 궁금하다면?</h1>
				<p>
					10가지 카테고리 테스트로 나만의 여행자 유형을 찾고,<br> 비슷한 여행자들의 추천 루트를 발견해보세요.
				</p>
				<div class="analysis-action">
					<a href="/hashTrip/analysis" class="btn-analysis">여행유형 테스트 시작하기</a>
					<p id="changing-text" class="sub-text">"당신의 마음이 머물고 싶어 하는 그곳으로 안내해 드릴게요."</p>
				</div>
			</div>

			<div class="analysis-image">
				<div class="map-background"></div>

				<div class="floating-card type-1">#제주도</div>
				<div class="floating-card type-2">#힐링</div>
				<div class="floating-card type-3">#액티비티</div>
				<div class="floating-card type-1">#맛집탐방</div>
				<div class="floating-card type-2">#인생샷</div>
				<div class="floating-card type-3">#차박</div>
				<div class="floating-card type-1">#혼자여행</div>
			</div>
		</div>
	</section>
	<!-- 성향 분석 -->

	<!-- 추천 여행지 -->
	<section class="recommend-section">
		<div class="recommend-container">
			<div class="section-title">
				<h2>지금 인기 있는 #Trip 추천 여행지</h2>
			</div>

			<div class="search-bar-wrapper">
				<div class="search-input-box">
					<i class="search-icon">🔍</i> <input type="text"
						id="destinationSearch"
						placeholder="어디로 떠나고 싶으신가요? (예: 제주, 부산, 강릉)">
				</div>
			</div>

			<div class="recommend-cards" id="recommendCards">
				<div class="travel-card">
					<div class="card-image"
						style="background-image: url('https://images.unsplash.com/photo-1571566882372-1598d88abd90?q=80&w=500')">
						<span class="tag">인기</span>
					</div>
					<div class="card-info">
						<h3>제주도 성산일출봉</h3>
						<p>푸른 바다와 함께 즐기는 일출 명소</p>
						<div class="card-footer">
							<span class="location">📍 제주</span> <span class="rating">⭐
								4.8</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>
	<!-- 추천 여행지 -->

	<!-- footer -->
	<footer class="main-footer">
		<div class="footer-container">
			<div class="footer-top">
				<div class="footer-brand">
					<h2 class="footer-logo">#Trip</h2>
					<p class="brand-desc">취향 기반 여행 유형 분석 플랫폼</p>
					<p class="brand-sub">나만의 완벽한 여행을 찾아드립니다.</p>
				</div>

				<div class="footer-links">
					<h3>법적 고지</h3>
					<ul>
						<li><a href="/hashTrip/privacy">개인정보처리방침</a></li>
						<li><a href="/hashTrip/terms">이용약관</a></li>
						<li><a href="/hashTrip/location">위치기반서비스</a></li>
					</ul>
				</div>

				<div class="footer-links">
					<h3>고객 지원</h3>
					<ul>
						<li><a href="/hashTrip/faq">자주 묻는 질문</a></li>
						<li><a href="/hashTrip/contact">1:1 문의</a></li>
						<li><a href="/hashTrip/notice">공지사항</a></li>
					</ul>
				</div>
			</div>

			<div class="footer-bottom">
				<p>&copy; 2025 WAYGO. All rights reserved.</p>
			</div>
		</div>
	</footer>
	<!-- footer -->

	<script type="text/javascript">
		document
				.getElementById('destinationSearch')
				.addEventListener(
						'keyup',
						function() {
							let searchValue = this.value.toLowerCase();
							let cards = document
									.getElementsByClassName('travel-card');

							for (let i = 0; i < cards.length; i++) {
								let title = cards[i].querySelector('h3').innerText
										.toLowerCase();
								let location = cards[i]
										.querySelector('.location').innerText
										.toLowerCase();

								if (title.includes(searchValue)
										|| location.includes(searchValue)) {
									cards[i].style.display = "";
								} else {
									cards[i].style.display = "none";
								}
							}
						});
		
		const textElement = document.getElementById('changing-text');
		const messages = [
			"지금 가장 핫한 MZ 여행 트렌드, 당신의 유형은 무엇인가요?",
		    "* 당신도 몰랐던 당신의 숨겨진 여행 DNA를 찾아보세요.",
		    "* 지금 이 순간, 당신에게 가장 필요한 여행의 온도는?",
		    "* 지금 이 계절, 당신의 마음이 머물고 싶은 온도는?",
		    "* 설레는 여행의 시작, 당신의 취향에서부터 출발합니다.",
		    "* 2026년 첫 여행, 실패 없는 선택을 위한 가이드라인",
		    "* 당신이 길치여도 괜찮아요. 취향이 길을 안내할 테니까요."
		];

		let currentIndex = 0;

		function rotateText() {
		    textElement.classList.add('fade-out');

		    setTimeout(() => {
		        currentIndex = (currentIndex + 1) % messages.length;
		        textElement.textContent = messages[currentIndex];
		        textElement.classList.remove('fade-out');
		    }, 500); 
		}

		setInterval(rotateText, 5000);
	</script>

</body>
</html>