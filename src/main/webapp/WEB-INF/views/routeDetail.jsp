<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>${route.title} 상세 일정</title>
    <link href="https://fonts.googleapis.com/css2?family=Pretendard:wght@400;600;700;800&family=Gmarket+Sans:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/routes.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/routeDetail.css">
</head>
<body style="background-color: var(--bg-gray-50);">

<nav class="navbar scrolled">
    <div class="logo" onclick="location.href='${pageContext.request.contextPath}/routes'">#HiFive</div>
</nav>

<main class="detail-container">
    <header class="detail-header">
        <div class="type-badge" style="background:none; padding-left:0; color:var(--text-tertiary);">COURSE DETAIL</div>
        <div class="title-row">
            <h1 class="route-title">${route.title}</h1>
        </div>
        <p class="personal-desc" style="margin-top:15px;">
            <strong>${route.userName}</strong>의 추천 코스: ${route.description}
        </p>
    </header>

    <section>
        <c:forEach var="step" items="${route.steps}" varStatus="status">
            <div class="step-card">
                <div style="display:flex; align-items:center; gap:20px;">
                    <div class="step-number-box" style="width:40px; height:40px; background:var(--bg-gray-100); border-radius:12px; display:flex; align-items:center; justify-content:center; font-weight:800; color:var(--primary-blue);">
                        ${status.count}
                    </div>
                    <div style="font-size:1.2rem; font-weight:700;">${step}</div>
                </div>
                <button class="btn-place-save" onclick="showToast('장소를 저장했습니다.')">장소 저장</button>
            </div>
        </c:forEach>
    </section>

    <section class="review-container">
        <h3 style="font-family:'Gmarket Sans'; margin-bottom:24px;">여행자 리뷰 <span style="color:var(--primary-blue);" id="review-count">${fn:length(reviews)}</span></h3>

        <form id="review-form" class="review-form" onsubmit="submitReview(event, ${route.id})">
            <textarea id="review-content" class="review-input" placeholder="코스 경험을 남겨주세요." maxlength="2000" required></textarea>
            <div class="review-form-row">
                <div>
                    <label for="review-rating">평점</label>
                    <select id="review-rating" name="rating">
                        <option value="5">5점</option>
                        <option value="4">4점</option>
                        <option value="3">3점</option>
                        <option value="2">2점</option>
                        <option value="1">1점</option>
                    </select>
                </div>
                <button type="submit" class="btn-detail-action btn-review-main">리뷰 등록</button>
            </div>
        </form>

        <div id="review-list">
            <c:choose>
                <c:when test="${empty reviews}">
                    <div class="review-empty">첫 리뷰를 작성해보세요.</div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="review" items="${reviews}">
                        <div class="review-item">
                            <div class="review-head">
                                <strong><c:out value="${not empty review.createdBy ? review.createdBy : '익명'}"/></strong>
                                <span class="review-rating">⭐ <c:out value="${not empty review.rating ? review.rating : 0}"/></span>
                            </div>
                            <div class="review-content"><c:out value="${review.reviewContent}"/></div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</main>

<div class="toast" id="toast"></div>

<script>
    const contextPath = '${pageContext.request.contextPath}';
    const csrfToken = '${_csrf.token}';
    const csrfHeader = '${_csrf.headerName}';
    const csrfParam = '${_csrf.parameterName}';

    async function submitReview(event, routeId) {
        event.preventDefault();

        const contentInput = document.getElementById('review-content');
        const ratingInput = document.getElementById('review-rating');
        const content = contentInput.value.trim();
        const rating = ratingInput.value;

        if (!content) {
            showToast('리뷰 내용을 입력해 주세요.');
            return;
        }

        try {
            const body = 'reviewContent=' + encodeURIComponent(content)
                + '&rating=' + encodeURIComponent(rating)
                + '&' + encodeURIComponent(csrfParam) + '=' + encodeURIComponent(csrfToken);

            const response = await fetch(contextPath + '/routes/' + routeId + '/reviews', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    [csrfHeader]: csrfToken
                },
                body
            });

            let data = null;
            try {
                data = await response.json();
            } catch (jsonError) {
                showToast('리뷰 저장 실패 (' + response.status + ')');
                return;
            }
            if (!response.ok || !data.success) {
                showToast(data && data.message ? data.message : '리뷰 저장 중 오류가 발생했습니다.');
                return;
            }

            prependReview(data.review);
            document.getElementById('review-count').textContent = data.reviewCount;
            contentInput.value = '';
            ratingInput.value = '5';
            showToast('리뷰가 등록되었습니다.');
        } catch (e) {
            showToast('리뷰 저장 중 오류가 발생했습니다.');
        }
    }

    function prependReview(review) {
        const list = document.getElementById('review-list');
        const empty = list.querySelector('.review-empty');
        if (empty) {
            empty.remove();
        }

        const name = review && review.createdBy ? review.createdBy : '익명';
        const rating = review && review.rating ? review.rating : 0;
        const content = review && review.reviewContent ? review.reviewContent : '';

        const item = document.createElement('div');
        item.className = 'review-item';
        item.innerHTML = ''
            + '<div class="review-head">'
            + '<strong>' + escapeHtml(name) + '</strong>'
            + '<span class="review-rating">⭐ ' + escapeHtml(String(rating)) + '</span>'
            + '</div>'
            + '<div class="review-content">' + escapeHtml(content) + '</div>';

        list.prepend(item);
    }

    function escapeHtml(value) {
        return value
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function showToast(msg) {
        const t = document.getElementById('toast');
        t.textContent = msg;
        t.classList.add('show');
        setTimeout(() => t.classList.remove('show'), 2200);
    }
</script>
</body>
</html>
