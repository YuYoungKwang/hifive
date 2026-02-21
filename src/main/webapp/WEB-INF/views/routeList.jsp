<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>#HiFive — 추천 여행 루트</title>
    <link href="https://fonts.googleapis.com/css2?family=Pretendard:wght@300;400;600;700;800&family=Gmarket+Sans:wght@300;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/routes.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
</head>

<body>

<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />

<div class="page-container">
    <div class="routes-wrap">

        <%-- ✅ 개인화 헤더 (기존 유지) --%>
        <div class="personal-hero" id="personal-hero">
            <div class="personal-badge">PERSONALIZED RECOMMENDATION</div>
            <div class="personal-row">
                <div class="personal-title">
                    <c:set var="displayPersonalName" value="${not empty personalUserName ? personalUserName : (pageContext.request.userPrincipal != null ? pageContext.request.userPrincipal.name : '여행')}"/>
                    <span class="personal-name"><c:out value="${displayPersonalName}"/></span><c:if test="${!fn:endsWith(displayPersonalName, '님')}">님</c:if>과<br/>
                    <span class="personal-strong">유사도 <c:out value="${not empty similarityPct ? similarityPct : 98}"/>% 장소들</span>
                </div>
                <div class="personal-right">
                    <div class="personal-desc">비슷한 성향의 유저들이 최근 7일간 가장 많이 방문한 핫플레이스입니다.</div>
                    <div class="personal-meta">
                        <div class="meta-pill">👥 <b><c:out value="${not empty activeUsers ? activeUsers : '1,234'}"/></b>명 탐색 중</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="routes-header">
            <div class="section-badge">RECOMMENDED ROUTES</div>
            <div class="routes-header-row">
                <h2 class="section-title">🗺️ 추천 여행 루트</h2>
                <button class="btn-cta-outline btn-share" id="btn-share-route">+ 내 루트 공유</button>
            </div>
            <p class="section-subtitle">취향이 맞는 여행자들의 루트를 발견해보세요</p>
        </div>

        <%-- ── 필터 바 ── --%>
        <div class="filter-bar" id="filter-bar">
            <button class="filter-chip ${empty activeFilter ? 'active' : ''}" onclick="filterRoutes('', this)">전체</button>
            <c:forEach var="cat" items="${categories}">
                <button class="filter-chip ${activeFilter == cat.categoryKey ? 'active' : ''}" 
                        onclick="filterRoutes('${cat.categoryKey}', this)">${cat.icon} ${cat.label}</button>
            </c:forEach>
        </div>

        <%-- ── 루트 그리드 ── --%>
        <div class="route-grid" id="route-grid">
            <c:choose>
                <c:when test="${empty routes}">
                    <div class="empty-state"><div class="empty-icon">🗺️</div><p>조건에 맞는 루트가 없어요</p></div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="route" items="${routes}">
                        <c:set var="routeType" value="${null}"/>
                        <c:forEach var="type" items="${travelerTypes}">
                            <c:if test="${type.typeId == route.typeId}"><c:set var="routeType" value="${type}"/></c:if>
                        </c:forEach>

                        <%-- ✅ 상세 페이지 이동 링크 연결 (기존 스타일 유지) --%>
                        <div class="route-card" style="cursor:pointer" 
                             onclick="location.href='${pageContext.request.contextPath}/routes/${route.id}'">
                            
                            <div class="route-head">
                                <div class="traveler-av" style="background:${not empty routeType ? routeType.bgColor : '#f0f0f0'}">${route.emoji}</div>
                                <div class="traveler-info">
                                    <div class="t-name">${route.userName}</div>
                                    <c:if test="${not empty routeType}">
                                        <div class="type-badge" style="background:${routeType.bgColor};color:${routeType.color}">${routeType.emoji} ${routeType.name}</div>
                                    </c:if>
                                </div>
                                <c:if test="${not empty route.matchScore}">
                                    <div class="match-pct">
                                        <div class="match-num" style="color:${route.matchScore >= 80 ? 'var(--green)' : 'var(--primary-blue)'}">${route.matchScore}%</div>
                                        <div class="match-label">취향 매칭</div>
                                    </div>
                                </c:if>
                            </div>

                            <c:if test="${not empty route.matchScore}">
                                <div class="match-bar-wrap">
                                    <div class="match-bar-bg">
                                        <div class="match-bar-fill" style="width:${route.matchScore}%; background:${route.matchScore >= 80 ? 'var(--green)' : 'var(--primary-blue)'}"></div>
                                    </div>
                                </div>
                            </c:if>

                            <div class="route-body">
                                <div class="route-title">${route.title}</div>
                                <div class="route-desc">${route.description}</div>
                                <div class="route-steps">
                                    <c:forEach var="step" items="${route.steps}" varStatus="st">
                                        <span class="route-step">${step}</span><c:if test="${!st.last}"><span class="route-arrow">→</span></c:if>
                                    </c:forEach>
                                </div>
                                <div class="tags">
                                    <c:forEach var="tagEntry" items="${route.tags}">
                                        <span class="tag tag-place">${tagEntry.value}</span>
                                    </c:forEach>
                                </div>
                            </div>

                            <div class="route-foot">
                                <div class="route-stats">
                                    <div class="route-stat">🔖 ${route.savedCount}</div>
                                </div>
                                <button class="btn-save-route" onclick="event.stopPropagation(); saveRoute(${route.id}, this)">저장</button>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="toast" id="toast"></div>

<jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />

<script>
    const csrfHeader = '${_csrf.headerName}';
    const csrfToken = '${_csrf.token}';

    function filterRoutes(category, btn) {
        document.querySelectorAll('.filter-chip').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        fetch('${pageContext.request.contextPath}/routes/filter?category=' + encodeURIComponent(category))
            .then(res => res.json())
            .then(routes => renderRouteGrid(routes))
            .catch(() => showToast('오류가 발생했어요'));
    }

    function renderRouteGrid(routes) {
        const grid = document.getElementById('route-grid');
        if (!routes || routes.length === 0) {
            grid.innerHTML = '<div class="empty-state"><p>조건에 맞는 루트가 없어요</p></div>';
            return;
        }

        const typeMap = {};
        <c:forEach var="type" items="${travelerTypes}">
            typeMap['${type.typeId}'] = { name: '${type.name}', emoji: '${type.emoji}', color: '${type.color}', bgColor: '${type.bgColor}' };
        </c:forEach>

        grid.innerHTML = routes.map(route => {
            const type = typeMap[route.typeId] || {};
            const sim = route.matchScore;
            const barClr = sim >= 80 ? 'var(--green)' : 'var(--primary-blue)';

            return `
            <div class="route-card" style="cursor:pointer" onclick="location.href='${pageContext.request.contextPath}/routes/\${route.id}'">
                <div class="route-head">
                    <div class="traveler-av" style="background:\${type.bgColor || '#f0f0f0'}">\${route.emoji}</div>
                    <div class="traveler-info">
                        <div class="t-name">\${route.userName}</div>
                        <div class="type-badge" style="background:\${type.bgColor};color:\${type.color}">\${type.emoji} \${type.name}</div>
                    </div>
                    \${sim ? `<div class="match-pct"><div class="match-num" style="color:\${barClr}">\${sim}%</div><div class="match-label">취향 매칭</div></div>` : ''}
                </div>
                \${sim ? `<div class="match-bar-wrap"><div class="match-bar-bg"><div class="match-bar-fill" style="width:\${sim}%;background:\${barClr}"></div></div></div>` : ''}
                <div class="route-body">
                    <div class="route-title">\${route.title}</div>
                    <div class="route-desc">\${route.description}</div>
                    <div class="route-steps">\${route.steps.join(' → ')}</div>
                </div>
                <div class="route-foot">
                    <div class="route-stats">
                        <div class="route-stat">💾 \${route.savedCount}</div>
                    </div>
                    <button class="btn-save-route" onclick="event.stopPropagation(); saveRoute(\${route.id}, this)">저장</button>
                </div>
            </div>`;
        }).join('');
    }

    async function saveRoute(routeId, btn) {
        const headers = {'Content-Type': 'application/x-www-form-urlencoded'};
        if (csrfHeader && csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        try {
            const response = await fetch('${pageContext.request.contextPath}/routes/save', {
                method: 'POST',
                headers,
                body: 'routeId=' + encodeURIComponent(routeId)
            });

            const text = await response.text();
            let data = null;
            try {
                data = text ? JSON.parse(text) : {};
            } catch (e) {
                data = {success: false, message: '서버 응답을 처리하지 못했습니다.'};
            }

            if (data && data.loginRequired) {
                showToast(data.message || '로그인이 필요합니다.');
                setTimeout(() => {
                    const redirectUrl = data.redirectUrl || '/auth/login';
                    if (redirectUrl.startsWith('http')) {
                        location.href = redirectUrl;
                    } else {
                        location.href = '${pageContext.request.contextPath}' + redirectUrl;
                    }
                }, 500);
                return;
            }

            if (response.ok && data && data.success) {
                showToast(data.message || '저장되었습니다.');
                btn.textContent = '저장됨';
                btn.disabled = true;
                return;
            }

            showToast(data && data.message ? data.message : '저장 중 오류가 발생했습니다.');
        } catch (e) {
            showToast('저장 중 오류가 발생했습니다.');
        }
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
