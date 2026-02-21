<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>#Trip | 성향 분석 테스트</title>
<style>
/* 1. 기본 스타일 */
* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	font-family: 'Pretendard', -apple-system, sans-serif;
	background-color: #f8f9fa;
	color: #333;
}

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

/* 2. 테스트 영역 */
.test-wrapper {
	max-width: 600px;
	margin: 50px auto;
	padding: 20px;
	background: #fff;
	border-radius: 20px;
	box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

/* 퍼센트 텍스트 추가를 위한 컨테이너 */
.progress-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 10px;
	font-size: 14px;
	font-weight: 600;
	color: #888;
}

#percentNum {
	color: #007bff;
}

.progress-track {
	width: 100%;
	height: 8px;
	background: #eee;
	border-radius: 10px;
	margin-bottom: 30px;
	overflow: hidden;
}

.progress-bar {
	width: 0%;
	height: 100%;
	background: #007bff;
	transition: 0.4s;
}

.question-header {
	text-align: center;
	margin-bottom: 30px;
}

#qNumber {
	color: #007bff;
	font-weight: bold;
	font-size: 18px;
	display: block;
	margin-bottom: 10px;
}

#qTitle {
	font-size: 22px;
	word-break: keep-all;
	line-height: 1.4;
}

.q-guide {
	font-size: 13px;
	color: #bbb;
	margin-top: 8px;
}

/* 3. 카드 그리드 */
.answer-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 15px;
}

.answer-card {
	background: #fff;
	border: 2px solid #eee;
	border-radius: 15px;
	padding: 25px 10px;
	text-align: center;
	cursor: pointer;
	transition: 0.2s;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.answer-card:hover {
	border-color: #007bff;
	background: #fcfdfe;
}

/* 선택되었을 때 스타일 */
.answer-card.selected {
	border-color: #007bff;
	background: #e7f3ff;
	outline: 1px solid #007bff;
}

.answer-card .icon {
	font-size: 40px;
	margin-bottom: 10px;
}

.answer-card .label {
	font-size: 16px;
	font-weight: 600;
	color: #444;
}

/* 4. 하단 버튼 */
.test-footer {
	margin-top: 40px;
	display: flex;
	gap: 10px;
}

.nav-btn {
	flex: 1;
	padding: 16px;
	border-radius: 12px;
	border: none;
	cursor: pointer;
	font-weight: bold;
	font-size: 16px;
	transition: 0.2s;
}

#prevBtn {
	background: #eee;
	color: #777;
}

#nextBtn {
	background: #007bff;
	color: #fff;
}

.nav-btn:disabled {
	opacity: 0.5;
	cursor: not-allowed;
}

/* 5. 로딩 화면 */
#loading-screen {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(255, 255, 255, 0.98);
	z-index: 1000;
	display: none;
	flex-direction: column;
	justify-content: center;
	align-items: center;
}

.loading-icon {
	font-size: 60px;
	animation: bounce 1s infinite;
	margin-bottom: 20px;
}

@
keyframes bounce { 0%, 100% {
	transform: translateY(0);
}

50




%
{
transform




:




translateY


(




-20px




)


;
}
}
.loading-bar {
	width: 200px;
	height: 6px;
	background: #eee;
	border-radius: 10px;
	overflow: hidden;
	margin-top: 20px;
}

.loading-fill {
	width: 0%;
	height: 100%;
	background: #007bff;
	animation: fillProgress 3s forwards;
}

@
keyframes fillProgress {from { width:0%;
	
}

to {
	width: 100%;
}
}
</style>
</head>
<body>

	<!-- header -->
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<!-- header -->

	<div class="test-wrapper">
		<div class="progress-info">
			<span>진행률</span> <span><span id="percentNum">0</span>%</span>
		</div>
		<div class="progress-track">
			<div class="progress-bar" id="progressBar"></div>
		</div>

		<div class="question-header">
			<span id="qNumber">Q1.</span>
			<h2 id="qTitle">질문을 불러오는 중...</h2>
			<p class="q-guide">(중복 선택이 가능합니다)</p>
		</div>

		<div id="answerGrid" class="answer-grid"></div>

		<div class="test-footer">
			<button class="nav-btn" id="prevBtn" onclick="prevStep()" disabled>이전</button>
			<button class="nav-btn" id="nextBtn" onclick="nextStep()">다음
				단계로</button>
		</div>
	</div>

	<div id="loading-screen">
		<div class="loading-icon" id="loadingIcon">✈️</div>
		<h2 style="font-weight: 700; text-align: center;">
			당신의 여행 스타일을<br>분석하고 있습니다...
		</h2>

		<div class="loading-bar">
			<div class="loading-fill" id="loadingBarFill"></div>
		</div>

		<div
			style="margin-top: 15px; font-size: 24px; font-weight: bold; color: #007bff;">
			<span id="loading-percent">0</span>%
		</div>
	</div>

	<script>
    const questions = [
        { title: "지금 당장 떠난다면 보고 싶은 풍경은?", options: [{text:"산·자연", icon:"⛰️"}, {text:"바다·해변", icon:"🌊"}, {text:"도시·문화", icon:"🏙️"}, {text:"시골·온천", icon:"🏡"}] },
        { title: "여행 계획 스타일은 어떤가요?", options: [{text:"분 단위 계획형", icon:"📅"}, {text:"주요 스팟만 선정", icon:"📍"}, {text:"발길 닿는 대로 즉흥", icon:"👣"}, {text:"남이 짜준 대로", icon:"🤝"}] },
        { title: "선호하는 이동 수단은?", options: [{text:"자가용·렌트카", icon:"🚗"}, {text:"대중교통", icon:"🚌"}, {text:"택시", icon:"🚕"}, {text:"도보 여행", icon:"👟"}] },
        { title: "숙소에서 가장 중요한 것은?", options: [{text:"호텔·리조트", icon:"🏨"}, {text:"감성 에어비앤비", icon:"🏡"}, {text:"자연 속 캠핑", icon:"⛺"}, {text:"가성비 게하", icon:"🏠"}] },
        { title: "여행 중 지출 스타일은?", options: [{text:"철저한 예산형", icon:"💰"}, {text:"가성비 중심", icon:"⚖️"}, {text:"쓸 땐 쓰는 스타일", icon:"💎"}, {text:"플렉스 스타일", icon:"💳"}] },
        { title: "함께 가고 싶은 파트너는?", options: [{text:"혼자만의 여행", icon:"🙋"}, {text:"사랑하는 연인", icon:"👩‍❤️‍👨"}, {text:"가족과 함께", icon:"👨‍👩‍👧‍👦"}, {text:"즐거운 친구들", icon:"👯"}] },
        { title: "여행의 꽃, 음식 스타일은?", options: [{text:"유명 맛집 탐방", icon:"🍱"}, {text:"로컬 현지 음식", icon:"🥘"}, {text:"간편식·편의점", icon:"🥪"}, {text:"직접 요리", icon:"🥩"}] },
        { title: "여행의 주된 목적은?", options: [{text:"완전한 휴식", icon:"🧘"}, {text:"액티비티·체험", icon:"🧗"}, {text:"인생샷 남기기", icon:"📸"}, {text:"문화·역사 공부", icon:"🏛️"}] },
        { title: "하루 일정의 강도는?", options: [{text:"숙소에서 힐링", icon:"🛌"}, {text:"여유로운 일정", icon:"🚶"}, {text:"바쁜 관광 일정", icon:"🏃"}, {text:"밤까지 풀코스", icon:"🔥"}] },
        { title: "어떤 분위기를 선호하나요?", options: [{text:"북적이는 핫플", icon:"🔥"}, {text:"조용한 숨은 명소", icon:"🤫"}, {text:"계절감이 있는 곳", icon:"🍂"}, {text:"화려한 야경", icon:"🌃"}] }
    ];

    let currentStep = 0;
    // 중복 선택을 위해 배열 형태로 저장하도록 변경
    let userAnswers = {}; 

    function render() {
        const q = questions[currentStep];
        document.getElementById('qNumber').innerText = "Q" + (currentStep + 1) + ".";
        document.getElementById('qTitle').innerText = q.title;
        
        const grid = document.getElementById('answerGrid');
        grid.innerHTML = ''; 

        // 현재 단계의 답변 데이터가 없으면 빈 배열로 초기화
        if (!userAnswers[currentStep]) userAnswers[currentStep] = [];

        q.options.forEach((opt, idx) => {
            const card = document.createElement('div');
            card.className = 'answer-card';
            
            // 중복 선택 확인: 배열 안에 해당 index가 있는지 체크
            if (userAnswers[currentStep].includes(idx)) {
                card.classList.add('selected');
            }
            
            card.innerHTML = '<span class="icon">' + opt.icon + '</span><span class="label">' + opt.text + '</span>';
            
            card.onclick = function() {
                const answerIdx = userAnswers[currentStep].indexOf(idx);
                if (answerIdx > -1) {
                    // 이미 선택되어 있으면 제거 (토글)
                    userAnswers[currentStep].splice(answerIdx, 1);
                } else {
                    // 새로 선택하면 배열에 추가
                    userAnswers[currentStep].push(idx);
                }
                render(); // 화면 갱신
            };
            grid.appendChild(card);
        });

     // 프로그레스 바 및 퍼센트 수치 업데이트
     // currentStep + 1 대신 currentStep을 사용하여 0번 질문일 때 0%부터 시작하게 합니다.
     const progress = Math.floor((currentStep / questions.length) * 100);
     document.getElementById('progressBar').style.width = progress + "%";
     document.getElementById('percentNum').innerText = progress;

     document.getElementById('prevBtn').disabled = (currentStep === 0);
     document.getElementById('nextBtn').innerText = (currentStep === questions.length - 1) ? "분석 결과 보기" : "다음 단계로";
    }

    function nextStep() {
        // 중복 선택이므로 아무것도 안 골랐을 때 체크
        if (!userAnswers[currentStep] || userAnswers[currentStep].length === 0) {
            alert("최소 하나 이상의 답변을 선택해 주세요!");
            return;
        }
        
        if (currentStep < questions.length - 1) {
            currentStep++;
            render();
            window.scrollTo(0, 0);
        } else {
            showLoading();
        }
    }

    function prevStep() {
        if (currentStep > 0) {
            currentStep--;
            render();
        }
    }

    function showLoading() {
        // 1. 기존 질문지 숨기고 로딩창 띄우기
        document.querySelector('.test-wrapper').style.display = 'none';
        document.getElementById('loading-screen').style.display = 'flex';
        
        const bar = document.getElementById('loadingBarFill'); // 바 요소
        const percentText = document.getElementById('loading-percent'); // 숫자 요소
        const iconBox = document.getElementById('loadingIcon');

        // 2. 아이콘 변경 타이머
        const icons = ["✈️", "🧳", "🏝️", "🏔️", "🗺️"];
        let i = 0;
        const iconInterval = setInterval(() => {
            if(iconBox) iconBox.innerText = icons[i % icons.length];
            i++;
        }, 600);

        // 3. 퍼센트 및 게이지 상승 (0% -> 100%)
        let gauge = 0;
        const gaugeInterval = setInterval(() => {
            gauge++;
            
            // 숫자가 화면에 표시됨
            if (percentText) percentText.innerText = gauge;
            
            // 바가 오른쪽으로 길어짐
            if (bar) bar.style.width = gauge + "%";

            if (gauge >= 100) clearInterval(gaugeInterval);
        }, 30); // 3초 동안 진행

        // 4. 3.5초 뒤 결과 페이지로 이동
        setTimeout(() => {
            clearInterval(iconInterval);
            clearInterval(gaugeInterval);
            location.href = "${pageContext.request.contextPath}/hashTrip/analysisResult"; 
        }, 3500);
    }

    window.onload = render;
</script>
</body>
</html>