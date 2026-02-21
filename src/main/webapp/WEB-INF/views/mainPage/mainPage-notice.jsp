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

/* ================== 2. 공지사항 레이아웃 추가 ================== */
.notice-wrapper {
	background-color: #f9f9f9;
	padding: 60px 20px;
	min-height: 80vh;
}

.notice-container {
	max-width: 1000px; /* 전체적인 너비 설정 */
	margin: 0 auto;
}

.notice-header {
	text-align: center;
	margin-bottom: 40px;
}

.notice-header h1 {
	font-size: 32px;
	color: #222;
	margin-bottom: 10px;
}

.notice-table-container {
	background: #fff;
	border-radius: 12px;
	box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.notice-table {
	width: 100%;
	border-collapse: collapse;
}

.notice-table th {
	background-color: #fcfcfc;
	border-bottom: 1px solid #eee;
	padding: 15px 20px;
	font-size: 14px;
	color: #555;
	text-align: center;
}

.notice-table td {
	padding: 20px;
	border-bottom: 1px solid #eee;
}

/* 칼럼 비율 설정 */
.col-num {
	width: 10%;
	text-align: center;
}

.col-title {
	width: 75%;
	text-align: left;
}

.col-date {
	width: 15%;
	text-align: center;
	color: #999;
}

/* @keyframes 오타 수정: @와 keyframes 사이의 공백 제거 */
@
keyframes fadeIn {from { opacity:0;
	transform: translateY(-5px);
}

to {
	opacity: 1;
	transform: translateY(0);
}
}

.notice-content-row {
    display: none; /* 초기 상태: 숨김 */
    background-color: #fafafa;
}

.notice-content-inner {
    padding: 30px 60px;
    line-height: 1.8;
    color: #555;
    font-size: 15px;
    /* 애니메이션 효과 */
    animation: fadeIn 0.3s ease-in-out;
}

</style>
</head>
<body>
	<!-- header -->
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<!-- header -->

	<div class="notice-wrapper">
		<div class="notice-container">
			<div class="notice-header">
				<h1>공지사항</h1>
				<p>클릭하여 상세 내용을 확인하세요.</p>
			</div>

			<div class="notice-table-container">
				<table class="notice-table">
					<thead>
						<tr>
							<th class="col-num">번호</th>
							<th class="col-title">제목</th>
							<th class="col-date">등록일</th>
						</tr>
					</thead>
					<tbody>

						<tr class="notice-row">
							<td class="col-num">3</td>
							<td class="col-title">신규 여행 성향 테스트 질문 업데이트</td>
							<td class="col-date">2026.01.15</td>
						</tr>
						<tr class="notice-content-row">
							<td colspan="3">
								<div class="notice-content-inner">
									<p>
										안녕하세요. #Trip 팀입니다. <br> <br> 겨울 시즌을 맞아 새로운 성향 분석
										알고리즘이 업데이트되었습니다. 더욱 정교해진 질문들로 나만의 겨울 여행지를 찾아보세요!
									</p>
								</div>
							</td>
						</tr>

						<tr class="notice-row">
							<td class="col-num">2</td>
							<td class="col-title">시스템 점검 작업 안내 (01월 05일 02:00 ~ 04:00)</td>
							<td class="col-date">2026.01.03</td>
						</tr>
						<tr class="notice-content-row">
							<td colspan="3">
								<div class="notice-content-inner">
									<p>
										안정적인 서비스 제공을 위해 서버 점검이 진행될 예정입니다.<br> 해당 시간에는 서비스 접속이
										원활하지 않을 수 있으니 양해 부탁드립니다.
									</p>
								</div>
							</td>
						</tr>

						<tr class="notice-row">
							<td class="col-num">1</td>
							<td class="col-title">☀️ 2026 새해 맞이, #Trip과 함께하는 새로운 여정</td>
							<td class="col-date">2026.01.01</td>
						</tr>
						<tr class="notice-content-row">
							<td colspan="3">
								<div class="notice-content-inner">
									<p>
										안녕하세요, <strong>#Trip</strong>입니다. 어느덧 새로운 한 해가 밝았습니다! <br>
										<br> 새해를 맞아 본인의 여행 취향을 다시 한번 점검해 보실 수 있도록 <strong>'신년
											맞이 성향 분석 알고리즘'</strong>을 정교하게 업데이트했습니다.<br> 올 한 해, 여러분의 발길이 닿는
										곳마다 행복이 가득하기를 #Trip이 응원하겠습니다.<br>
										<br> 지금 바로 새로워진 질문들로 2026년 첫 여행지를 발견해 보세요!
									</p>
								</div>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	// 스크립트 시작 부분에 추가
	document.querySelectorAll('.notice-content-row').forEach(row => {
	    row.style.display = 'none';
	});

	document.querySelectorAll('.notice-row').forEach(row => {
	    row.addEventListener('click', () => {
	        const contentRow = row.nextElementSibling;
	        const isVisible = contentRow.style.display === 'table-row';
	        
	        // 나머지는 모두 닫기
	        document.querySelectorAll('.notice-content-row').forEach(content => {
	            content.style.display = 'none';
	        });
	        document.querySelectorAll('.notice-row').forEach(r => {
	            r.classList.remove('active');
	        });

	        // 클릭한 것만 토글
	        if (!isVisible) {
	            contentRow.style.display = 'table-row';
	            row.classList.add('active');
	        }
	    });
	});
	</script>
</body>
</html>