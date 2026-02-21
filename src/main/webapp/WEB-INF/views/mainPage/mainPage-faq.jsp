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

/* ================== 2. 자주묻는질문 ================== */
.faq-wrapper {
	background-color: #f9f9f9;
	padding: 80px 20px;
	min-height: 100vh;
}

.faq-container {
	max-width: 850px;
	margin: 0 auto;
}

.faq-header {
	text-align: center;
	margin-bottom: 50px;
}

.faq-header h1 {
	font-size: 32px;
	color: #222;
	margin-bottom: 10px;
}

.faq-header p {
	color: #666;
}

/* 아코디언 아이템 스타일 */
.faq-item {
	background: #fff;
	margin-bottom: 15px;
	border-radius: 12px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.faq-question {
	width: 100%;
	padding: 20px 25px;
	display: flex;
	align-items: center;
	background: none;
	border: none;
	cursor: pointer;
	text-align: left;
	transition: background 0.3s;
}

.faq-question:hover {
	background-color: #f0f7ff;
}

.category {
	font-size: 12px;
	color: #007bff;
	font-weight: bold;
	margin-right: 15px;
	min-width: 70px;
}

.faq-question .text {
	flex: 1;
	font-size: 16px;
	font-weight: 600;
	color: #333;
}

.arrow-icon {
	font-style: normal;
	color: #ccc;
	transition: transform 0.3s;
}

/* 답변 스타일 (기본적으로 숨김) */
.faq-answer {
	padding: 0 25px;
	max-height: 0;
	overflow: hidden;
	transition: all 0.3s ease-out;
	background-color: #fafafa;
}

.faq-answer p {
	padding: 20px 0;
	font-size: 15px;
	color: #555;
	line-height: 1.6;
	margin: 0;
}

/* 활성화 상태 (JS에서 toggle) */
.faq-item.active .faq-answer {
	max-height: 300px; /* 답변 길이에 따라 적절히 조절 */
	border-top: 1px solid #eee;
}

.faq-item.active .arrow-icon {
	transform: rotate(180deg);
}

.faq-footer {
	margin-top: 40px;
	text-align: center;
	color: #888;
	font-size: 14px;
}

.faq-footer a {
	color: #007bff;
	text-decoration: none;
	font-weight: bold;
}
</style>
</head>
<body>
	<!-- header -->
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<!-- header -->

	<div class="faq-wrapper">
		<div class="faq-container">
			<div class="faq-header">
				<h1>자주 묻는 질문</h1>
				<p>#Trip 서비스 이용에 대해 궁금한 점을 해결해 드립니다.</p>
			</div>

			<div class="faq-list">
				<div class="faq-item">
					<button class="faq-question">
						<span class="category">서비스 이용</span> <span class="text">#Trip은
							어떤 서비스인가요?</span> <i class="arrow-icon">▼</i>
					</button>
					<div class="faq-answer">
						<p>#Trip은 사용자의 여행 취향을 분석하여 최적의 여행지와 코스를 제안하는 취향 기반 여행 분석
							플랫폼입니다. 간단한 테스트를 통해 나만의 여행 스타일을 확인해 보세요!</p>
					</div>
				</div>

				<div class="faq-item">
					<button class="faq-question">
						<span class="category">분석 결과</span> <span class="text">여행
							성향 결과는 어디서 다시 볼 수 있나요?</span> <i class="arrow-icon">▼</i>
					</button>
					<div class="faq-answer">
						<p>로그인 후 '마이페이지 > 나의 분석 이력' 메뉴에서 언제든지 과거에 진행했던 성향 테스트 결과를 다시
							확인하고 비교해 보실 수 있습니다.</p>
					</div>
				</div>

				<div class="faq-item">
					<button class="faq-question">
						<span class="category">계정</span> <span class="text">회원 탈퇴는
							어떻게 하나요?</span> <i class="arrow-icon">▼</i>
					</button>
					<div class="faq-answer">
						<p>'마이페이지 > 회원 정보 수정' 하단의 '회원 탈퇴' 버튼을 통해 진행하실 수 있습니다. 탈퇴 시 기존의
							분석 데이터는 모두 삭제되며 복구가 불가능하니 유의해 주세요.</p>
					</div>
				</div>

				<div class="faq-item">
					<button class="faq-question">
						<span class="category">위치 정보</span> <span class="text">위치
							정보 권한이 꼭 필요한가요?</span> <i class="arrow-icon">▼</i>
					</button>
					<div class="faq-answer">
						<p>필수는 아니지만, 위치 권한을 허용하시면 현재 계신 곳을 중심으로 실시간 주변 여행지 및 맛집 추천
							서비스를 더욱 정확하게 받아보실 수 있습니다.</p>
					</div>
				</div>
			</div>

			<div class="faq-footer">
				<p>
					원하는 답변을 찾지 못하셨나요? <a href="/hashTrip/contact">1:1 문의하기</a>를 이용해 주세요.
				</p>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		document.querySelectorAll('.faq-question').forEach(button => {
		    button.addEventListener('click', () => {
		        const faqItem = button.parentElement;
		        
		        // 다른 질문들을 닫고 싶다면 아래 주석을 해제하세요
		        /*
		        document.querySelectorAll('.faq-item').forEach(item => {
		            if (item !== faqItem) item.classList.remove('active');
		        });
		        */
		        
		        faqItem.classList.toggle('active');
		    });
		});
	</script>
</body>
</html>