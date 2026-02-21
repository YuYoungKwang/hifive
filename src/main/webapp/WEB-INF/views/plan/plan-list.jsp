<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>여행 일정 목록</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/plan/plan-list.css">
</head>
<body class="plan-list-body">
<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />

<main class="plan-list-shell">
    <div class="plan-list-wrap">
        <div class="top">
            <h1 class="title">여행 일정 목록</h1>
            <a class="btn-new" href="${pageContext.request.contextPath}/plan/new">새 일정 작성</a>
        </div>

        <c:if test="${not empty planSaveMessage}">
            <div class="msg ok"><c:out value="${planSaveMessage}"/></div>
        </c:if>
        <c:if test="${not empty planSaveError}">
            <div class="msg err"><c:out value="${planSaveError}"/></div>
        </c:if>

        <c:choose>
            <c:when test="${empty myPlans}">
                <div class="empty">저장된 일정이 없습니다. 새 일정을 작성해 주세요.</div>
            </c:when>
            <c:otherwise>
                <div class="list">
                    <c:forEach var="plan" items="${myPlans}">
                        <article class="item">
                            <a class="item-main" href="${pageContext.request.contextPath}/plan/${plan.planNo}/edit">
                                <div class="name"><c:out value="${plan.planTitle}"/></div>
                                <c:choose>
                                    <c:when test="${plan.planStatus eq 'PLANNING'}">
                                        <span class="status st-planning">계획 중</span>
                                    </c:when>
                                    <c:when test="${plan.planStatus eq 'RECORDING'}">
                                        <span class="status st-recording">기록 중</span>
                                    </c:when>
                                    <c:when test="${plan.planStatus eq 'COMPLETED'}">
                                        <span class="status st-completed">완료</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status st-default"><c:out value="${plan.planStatus}"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </a>

                            <form class="delete-form" action="${pageContext.request.contextPath}/plan/${plan.planNo}/delete" method="post">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <button type="submit" class="btn-delete" onclick="return confirm('이 일정을 삭제하시겠습니까?');">삭제</button>
                            </form>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />
</body>
</html>
