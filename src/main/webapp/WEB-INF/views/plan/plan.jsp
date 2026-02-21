<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${editMode}">일정 수정</c:when>
            <c:otherwise>여행 일정 작성</c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/plan/plan.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="plan-page-body">
    <jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />

    <main class="plan-page-shell">
        <c:if test="${not empty planSaveMessage}">
            <div class="plan-alert success">
                <c:out value="${planSaveMessage}" />
            </div>
        </c:if>

        <c:if test="${not empty planSaveError}">
            <div class="plan-alert error">
                <c:out value="${planSaveError}" />
            </div>
        </c:if>

        <c:set var="planFormAction" value="${pageContext.request.contextPath}/plan" />
        <c:if test="${editMode}">
            <c:set var="planFormAction" value="${pageContext.request.contextPath}/plan/${plan.planNo}" />
        </c:if>

        <form id="planForm" action="${planFormAction}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" id="planDetailsJson" name="planDetailsJson" />
            <input type="hidden" id="isEditMode" value="${editMode}" />

            <div class="container">
                <header class="plan-header">
                    <input
                        type="text"
                        class="trip-title-input"
                        id="tripTitle"
                        name="planTitle"
                        placeholder="여행 제목을 입력하세요"
                        value="<c:out value='${plan.planTitle}'/>"
                    >
                    <a class="plan-list-link" href="${pageContext.request.contextPath}/plan">일정 목록</a>
                    <span class="status-badge status-planning" id="statusBadge">계획 중</span>
                    <input
                        type="hidden"
                        id="planStatus"
                        name="planStatus"
                        value="<c:out value='${empty plan.planStatus ? "PLANNING" : plan.planStatus}'/>"
                    >
                </header>

                <section class="trip-period-section">
                    <div class="period-row">
                        <div class="period-item">
                            <label>여행 시작일</label>
                            <input type="date" id="tripStartDate" name="planStartDate" value="<c:out value='${plan.planStartDate}'/>" onchange="updateTripPeriod()">
                        </div>
                        <div class="period-item">
                            <label>여행 종료일</label>
                            <input type="date" id="tripEndDate" name="planEndDate" value="<c:out value='${plan.planEndDate}'/>" onchange="updateTripPeriod()">
                        </div>
                        <div class="period-summary hidden" id="periodSummary"></div>
                    </div>
                </section>

                <section id="addPlaceSection" class="add-place-section">
                    <button type="button" id="addPlaceBtn" class="btn btn-primary">
                        <i class="fas fa-map-marker-alt"></i> 장소 선택
                    </button>
                    <span id="addPlaceHint" class="add-place-hint hidden">여행 중에도 장소를 추가할 수 있습니다.</span>
                </section>

                <section id="progressSection" class="progress-section hidden">
                    <div class="progress-info">
                        <span id="progressText">0 / 0 완료</span>
                        <span id="progressPercent">0%</span>
                    </div>
                    <div class="progress-bar">
                        <div id="progressFill" class="progress-fill" style="width: 0%"></div>
                    </div>
                </section>

                <section class="timeline-container">
                    <div id="placeList" class="place-list"></div>
                </section>

                <section id="reviewSection" class="review-section hidden">
                    <h2>전체 리뷰</h2>
                    <div class="total-rating">
                        <span>전체 만족도</span>
                        <div id="totalStars" class="star-rating" data-rating="0">
                            <i class="far fa-star" data-value="1"></i>
                            <i class="far fa-star" data-value="2"></i>
                            <i class="far fa-star" data-value="3"></i>
                            <i class="far fa-star" data-value="4"></i>
                            <i class="far fa-star" data-value="5"></i>
                        </div>
                    </div>
                    <textarea id="totalReview" class="total-review-input" placeholder="여행 후기를 작성해 주세요"></textarea>
                </section>

                <div class="action-buttons">
                    <div class="visibility-toggle">
                        <label>
                            <input type="checkbox" id="isPublic" name="planIsPublic" value="Y" <c:if test="${plan.planIsPublic eq 'Y'}">checked</c:if>> 공개
                        </label>
                    </div>
                    <a class="btn btn-list" href="${pageContext.request.contextPath}/plan">목록</a>
                    <button type="button" id="startTripBtn" class="btn btn-success">여행 시작</button>
                    <button type="button" id="completeTripBtn" class="btn btn-complete hidden">여행 완료</button>
                    <button type="button" id="saveBtn" class="btn btn-save">저장</button>
                </div>
            </div>

            <div id="mapModal" class="modal hidden">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2>장소 선택</h2>
                        <button type="button" id="closeMapModal" class="close-btn">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="search-box">
                            <input type="text" id="placeSearch" placeholder="장소 검색">
                            <button type="button" id="searchBtn">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>
                        <div id="map" class="map-container"></div>
                        <div id="searchResults" class="search-results"></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="confirmPlace" class="btn btn-primary">선택 완료</button>
                    </div>
                </div>
            </div>
        </form>
    </main>

    <jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />

    <script type="application/json" id="initial-plan-data"><c:out value="${empty planDetailsJson ? '[]' : planDetailsJson}" escapeXml="false"/></script>
    <script>window.appContextPath = '${pageContext.request.contextPath}';</script>
    <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=c0f942806f26f0fe25ab08a3eacbea9d&libraries=services"></script>
    <script src="${pageContext.request.contextPath}/js/plan/plan.js"></script>
</body>
</html>
