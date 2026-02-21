<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>마이페이지</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css">
</head>
<body>
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />
	<div id="mypage" class="page">
		<div class="mypage-container">
			<c:set var="displayName" value="${empty usersDTO.userNickName ? usersDTO.userName : usersDTO.userNickName}" />
			<c:if test="${empty displayName}">
				<c:set var="displayName" value="${currentAuthId}" />
			</c:if>

			<section class="profile-card">
				<div id="my-av" class="profile-avatar">${fn:substring(displayName, 0, 1)}</div>
				<h2 class="profile-name"><c:out value="${displayName}" /></h2>
				<div id="my-badge" class="profile-badge">🧭 나의 여행 성향</div>
				<p id="my-desc" class="profile-desc">태그를 추가/삭제하면 여행 매칭 추천에 반영됩니다.</p>
				<p class="profile-sub"><c:out value="${currentAuthId}" /></p>
				<div class="profile-actions">
					<a class="profile-action-btn" href="${pageContext.request.contextPath}/mypage/edit">회원정보 수정</a>
					<button class="profile-action-btn danger" type="button">회원 탈퇴</button>
				</div>
			</section>

			<section class="dashboard-card summary-card">
				<div class="summary-item">
					<p class="summary-label">작성한 리뷰</p>
					<p class="summary-value">${reviewCount}</p>
				</div>
				<div class="summary-item">
					<p class="summary-label">찜한 장소</p>
					<p class="summary-value">${wishCount}</p>
				</div>
			</section>

			<section class="dashboard-card">
				<details class="tag-manage-details">
					<summary>성향 태그 관리</summary>
					<div class="tag-manage-body">
						<div id="tag-managers"></div>
					</div>
				</details>
			</section>

			<section class="dashboard-card">
				<div class="review-section-head">
					<h3>내 여행지 리뷰 (${placeReviewCount})</h3>
					<form class="review-sort-form" method="get" action="${pageContext.request.contextPath}/mypage">
						<input type="hidden" name="placePage" value="1">
						<input type="hidden" name="communityPage" value="${communityCurrentPage}">
						<input type="hidden" name="communitySort" value="${communitySort}">
						<input type="hidden" name="placeExpanded" value="${placeExpanded ? 'Y' : 'N'}">
						<input type="hidden" name="communityExpanded" value="${communityExpanded ? 'Y' : 'N'}">
						<select name="placeSort" onchange="this.form.submit()">
							<option value="latest" ${placeSort == 'latest' ? 'selected' : ''}>최신순</option>
							<option value="oldest" ${placeSort == 'oldest' ? 'selected' : ''}>오래된순</option>
							<option value="rating" ${placeSort == 'rating' ? 'selected' : ''}>별점순</option>
						</select>
					</form>
				</div>
				<c:choose>
					<c:when test="${empty placeReviewList}">
						<p class="empty-text">작성한 여행지 리뷰가 없습니다.</p>
					</c:when>
					<c:otherwise>
						<div class="review-list">
							<c:forEach var="review" items="${placeReviewList}" varStatus="status">
								<c:if test="${placeExpanded or status.count <= reviewPreviewSize}">
									<article class="review-item">
										<div class="review-top">
											<c:choose>
												<c:when test="${not empty review.placeNo}">
													<a class="review-place-link" href="${pageContext.request.contextPath}/place/detail?place_no=${review.placeNo}">
														<c:choose>
															<c:when test="${not empty review.placeName}">
																<c:out value="${review.placeName}" />
															</c:when>
															<c:otherwise>
																장소 #${review.placeNo}
															</c:otherwise>
														</c:choose>
													</a>
												</c:when>
												<c:otherwise>
													<span class="review-place-link">
														<c:out value="${empty review.placeName ? '장소 정보 없음' : review.placeName}" />
													</span>
												</c:otherwise>
											</c:choose>
											<span class="review-rating">
												<c:forEach var="i" begin="1" end="5">
													<c:choose>
														<c:when test="${i <= review.rating}">★</c:when>
														<c:otherwise>☆</c:otherwise>
													</c:choose>
												</c:forEach>
											</span>
										</div>
										<p class="review-content"><c:out value="${review.commentContent}" /></p>
										<p class="review-date">
											<fmt:formatDate value="${review.createdAt}" pattern="yyyy-MM-dd HH:mm" />
										</p>
									</article>
								</c:if>
							</c:forEach>
						</div>

						<c:if test="${!placeExpanded and (fn:length(placeReviewList) > reviewPreviewSize or placeTotalPages > 1)}">
							<a class="review-more-toggle"
								href="${pageContext.request.contextPath}/mypage?placePage=${placeCurrentPage}&communityPage=${communityCurrentPage}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=Y&communityExpanded=${communityExpanded ? 'Y' : 'N'}">
								펼쳐서 더보기
							</a>
						</c:if>

						<c:if test="${placeExpanded and placeTotalPages > 1}">
							<nav class="pagination">
								<c:if test="${placeCurrentPage > 1}">
									<a class="page-link"
										href="${pageContext.request.contextPath}/mypage?placePage=${placeCurrentPage - 1}&communityPage=${communityCurrentPage}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=Y&communityExpanded=${communityExpanded ? 'Y' : 'N'}">이전</a>
								</c:if>
								<c:forEach var="pageNo" begin="1" end="${placeTotalPages}">
									<c:choose>
										<c:when test="${pageNo == placeCurrentPage}">
											<span class="page-link is-active">${pageNo}</span>
										</c:when>
										<c:otherwise>
											<a class="page-link"
												href="${pageContext.request.contextPath}/mypage?placePage=${pageNo}&communityPage=${communityCurrentPage}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=Y&communityExpanded=${communityExpanded ? 'Y' : 'N'}">${pageNo}</a>
										</c:otherwise>
									</c:choose>
								</c:forEach>
								<c:if test="${placeCurrentPage < placeTotalPages}">
									<a class="page-link"
										href="${pageContext.request.contextPath}/mypage?placePage=${placeCurrentPage + 1}&communityPage=${communityCurrentPage}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=Y&communityExpanded=${communityExpanded ? 'Y' : 'N'}">다음</a>
								</c:if>
							</nav>
						</c:if>

						<c:if test="${placeExpanded}">
							<a class="review-more-toggle is-collapse"
								href="${pageContext.request.contextPath}/mypage?placePage=1&communityPage=${communityCurrentPage}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=N&communityExpanded=${communityExpanded ? 'Y' : 'N'}">
								접기
							</a>
						</c:if>
					</c:otherwise>
				</c:choose>
			</section>

			<section class="dashboard-card">
				<div class="review-section-head">
					<h3>내 일정 리뷰 (${communityReviewCount})</h3>
					<form class="review-sort-form" method="get" action="${pageContext.request.contextPath}/mypage">
						<input type="hidden" name="placePage" value="${placeCurrentPage}">
						<input type="hidden" name="communityPage" value="1">
						<input type="hidden" name="placeSort" value="${placeSort}">
						<input type="hidden" name="placeExpanded" value="${placeExpanded ? 'Y' : 'N'}">
						<input type="hidden" name="communityExpanded" value="${communityExpanded ? 'Y' : 'N'}">
						<select name="communitySort" onchange="this.form.submit()">
							<option value="latest" ${communitySort == 'latest' ? 'selected' : ''}>최신순</option>
							<option value="oldest" ${communitySort == 'oldest' ? 'selected' : ''}>오래된순</option>
							<option value="rating" ${communitySort == 'rating' ? 'selected' : ''}>별점순</option>
						</select>
					</form>
				</div>

				<c:choose>
					<c:when test="${empty communityReviewList}">
						<p class="empty-text">작성한 일정 리뷰가 없습니다.</p>
					</c:when>
					<c:otherwise>
						<div class="review-list">
							<c:forEach var="review" items="${communityReviewList}" varStatus="status">
								<c:if test="${communityExpanded or status.count <= reviewPreviewSize}">
									<article class="review-item">
										<div class="review-top">
											<c:choose>
												<c:when test="${not empty review.planNo}">
													<a class="review-place-link" href="${pageContext.request.contextPath}/routes/${review.planNo}">
														<c:choose>
															<c:when test="${not empty review.planTitle}">
																<c:out value="${review.planTitle}" />
															</c:when>
															<c:otherwise>
																일정 #${review.planNo}
															</c:otherwise>
														</c:choose>
													</a>
												</c:when>
												<c:otherwise>
													<span class="review-place-link">
														<c:out value="${empty review.planTitle ? '일정 정보 없음' : review.planTitle}" />
													</span>
												</c:otherwise>
											</c:choose>
											<span class="review-rating">
												<c:forEach var="i" begin="1" end="5">
													<c:choose>
														<c:when test="${i <= review.rating}">★</c:when>
														<c:otherwise>☆</c:otherwise>
													</c:choose>
												</c:forEach>
											</span>
										</div>
										<p class="review-content"><c:out value="${review.reviewContent}" /></p>
										<p class="review-date">
											<fmt:formatDate value="${review.createdAt}" pattern="yyyy-MM-dd HH:mm" />
										</p>
									</article>
								</c:if>
							</c:forEach>
						</div>

						<c:if test="${!communityExpanded and (fn:length(communityReviewList) > reviewPreviewSize or communityTotalPages > 1)}">
							<a class="review-more-toggle"
								href="${pageContext.request.contextPath}/mypage?placePage=${placeCurrentPage}&communityPage=${communityCurrentPage}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=${placeExpanded ? 'Y' : 'N'}&communityExpanded=Y">
								펼쳐서 더보기
							</a>
						</c:if>

						<c:if test="${communityExpanded and communityTotalPages > 1}">
							<nav class="pagination">
								<c:if test="${communityCurrentPage > 1}">
									<a class="page-link"
										href="${pageContext.request.contextPath}/mypage?placePage=${placeCurrentPage}&communityPage=${communityCurrentPage - 1}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=${placeExpanded ? 'Y' : 'N'}&communityExpanded=Y">이전</a>
								</c:if>
								<c:forEach var="pageNo" begin="1" end="${communityTotalPages}">
									<c:choose>
										<c:when test="${pageNo == communityCurrentPage}">
											<span class="page-link is-active">${pageNo}</span>
										</c:when>
										<c:otherwise>
											<a class="page-link"
												href="${pageContext.request.contextPath}/mypage?placePage=${placeCurrentPage}&communityPage=${pageNo}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=${placeExpanded ? 'Y' : 'N'}&communityExpanded=Y">${pageNo}</a>
										</c:otherwise>
									</c:choose>
								</c:forEach>
								<c:if test="${communityCurrentPage < communityTotalPages}">
									<a class="page-link"
										href="${pageContext.request.contextPath}/mypage?placePage=${placeCurrentPage}&communityPage=${communityCurrentPage + 1}&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=${placeExpanded ? 'Y' : 'N'}&communityExpanded=Y">다음</a>
								</c:if>
							</nav>
						</c:if>

						<c:if test="${communityExpanded}">
							<a class="review-more-toggle is-collapse"
								href="${pageContext.request.contextPath}/mypage?placePage=${placeCurrentPage}&communityPage=1&placeSort=${placeSort}&communitySort=${communitySort}&placeExpanded=${placeExpanded ? 'Y' : 'N'}&communityExpanded=N">
								접기
							</a>
						</c:if>
					</c:otherwise>
				</c:choose>
			</section>

			<section class="dashboard-card">
				<h3>찜한 장소 지도</h3>
				<c:choose>
					<c:when test="${empty wishEntryList}">
						<p class="empty-text">찜한 장소가 없습니다.</p>
					</c:when>
					<c:otherwise>
						<div class="wish-filter-row">
							<label class="wish-filter-label" for="wish-category-filter">카테고리 선택</label>
							<select id="wish-category-filter" class="wish-filter-select">
								<option value="ALL">전체</option>
							</select>
						</div>
						<div id="wish-category-legend" class="wish-category-legend"></div>
						<p id="map-status" class="map-status">지도를 불러오는 중입니다.</p>
						<div id="wish-map" class="wish-map"></div>
						<ul id="wish-place-list" class="wish-place-list">
							<c:forEach var="wish" items="${wishEntryList}">
								<li data-category="${fn:escapeXml(empty wish.categoryType ? '기타' : wish.categoryType)}">
									<div class="wish-place-top">
										<a href="${pageContext.request.contextPath}/place/detail?place_no=${wish.placeNo}">
											<c:choose>
												<c:when test="${not empty wish.placeName}">
													<c:out value="${wish.placeName}" />
												</c:when>
												<c:otherwise>
													장소 #${wish.placeNo}
												</c:otherwise>
											</c:choose>
										</a>
										<span class="wish-category-badge">
											<c:out value="${empty wish.categoryType ? '기타' : wish.categoryType}" />
										</span>
									</div>
									<p><c:out value="${wish.placeAddress}" /></p>
								</li>
							</c:forEach>
						</ul>
					</c:otherwise>
				</c:choose>
			</section>
		</div>
	</div>

	<jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />

	<div id="mypage-toast" class="mypage-toast"></div>

	<script>
		(function() {
			const contextPath = "${pageContext.request.contextPath}";
			const csrfParamName = "${_csrf.parameterName}";
			const csrfToken = "${_csrf.token}";

			const TAG_MASTER_ROWS = [
				<c:forEach var="tag" items="${tagMasterList}" varStatus="status">
				{
					tagCode: "${tag.tagCode}",
					tagName: "${tag.tagName}",
					tagCategory: "${empty tag.tagCategory ? '기타' : tag.tagCategory}"
				}<c:if test="${!status.last}">,</c:if>
				</c:forEach>
			];

			const USER_TAG_ROWS = [
				<c:forEach var="tag" items="${userTagList}" varStatus="status">
				{
					tagCode: "${tag.tagCode}",
					tagName: "${empty tag.tagName ? tag.tagCode : tag.tagName}",
					tagCategory: "${empty tag.tagCategory ? '기타' : tag.tagCategory}"
				}<c:if test="${!status.last}">,</c:if>
				</c:forEach>
			];

			const categoryPreset = {
				"LOCATION": { label: "장소", icon: "📍", color: "#5B8DEE", cls: "cat-location" },
				"PLANNING": { label: "계획", icon: "📋", color: "#22C55E", cls: "cat-planning" },
				"MOVE": { label: "이동", icon: "🚗", color: "#2DD4BF", cls: "cat-move" },
				"STAY": { label: "숙소", icon: "🏨", color: "#F5A623", cls: "cat-stay" },
				"BUDGET": { label: "예산", icon: "💰", color: "#10B981", cls: "cat-budget" },
				"COMPANION": { label: "동행", icon: "👥", color: "#F87171", cls: "cat-companion" },
				"FOOD_STYLE": { label: "음식", icon: "🍽️", color: "#D97706", cls: "cat-food" },
				"PURPOSE": { label: "목적", icon: "🎯", color: "#818CF8", cls: "cat-purpose" },
				"INTENSITY": { label: "강도", icon: "⚡", color: "#7C3AED", cls: "cat-intensity" },
				"MOOD": { label: "무드", icon: "🌙", color: "#0EA5E9", cls: "cat-mood" }
			};

			const fallbackColors = ["#3182f6", "#22c55e", "#f59e0b", "#ef4444", "#8b5cf6", "#14b8a6"];
			const categoryKeys = [];
			TAG_MASTER_ROWS.forEach(function(tag) {
				const key = tag.tagCategory || "기타";
				if (categoryKeys.indexOf(key) < 0) {
					categoryKeys.push(key);
				}
			});
			USER_TAG_ROWS.forEach(function(tag) {
				const key = tag.tagCategory || "기타";
				if (categoryKeys.indexOf(key) < 0) {
					categoryKeys.push(key);
				}
			});

			const CATS = {};
			categoryKeys.forEach(function(category, index) {
				const preset = categoryPreset[(category || "").toUpperCase()];
				if (preset) {
					CATS[category] = preset;
					return;
				}
				CATS[category] = {
					label: category || "기타",
					icon: "🏷️",
					color: fallbackColors[index % fallbackColors.length],
					cls: "cat-etc"
				};
			});

			const ALL_TAGS = {};
			categoryKeys.forEach(function(category) {
				ALL_TAGS[category] = [];
			});
			TAG_MASTER_ROWS.forEach(function(tag) {
				const category = tag.tagCategory || "기타";
				if (!ALL_TAGS[category]) {
					ALL_TAGS[category] = [];
				}
				ALL_TAGS[category].push({ code: tag.tagCode, name: tag.tagName || tag.tagCode });
			});

			const myTags = {};
			categoryKeys.forEach(function(category) {
				myTags[category] = [];
			});
			USER_TAG_ROWS.forEach(function(tag) {
				const category = tag.tagCategory || "기타";
				if (!myTags[category]) {
					myTags[category] = [];
				}
				if (!ALL_TAGS[category]) {
					ALL_TAGS[category] = [];
				}
				if (!ALL_TAGS[category].some(function(item) { return item.code === tag.tagCode; })) {
					ALL_TAGS[category].push({ code: tag.tagCode, name: tag.tagName || tag.tagCode });
				}
				if (!myTags[category].some(function(item) { return item.code === tag.tagCode; })) {
					myTags[category].push({ code: tag.tagCode, name: tag.tagName || tag.tagCode });
				}
			});

			const myType = USER_TAG_ROWS.length > 0
				? {
					emoji: "🧭",
					name: "태그 " + USER_TAG_ROWS.length + "개 선택",
					desc: "선택된 성향 태그가 여행 매칭에 반영됩니다."
				}
				: null;

			function escapeHtml(value) {
				if (value === null || value === undefined) {
					return "";
				}
				return String(value)
					.replace(/&/g, "&amp;")
					.replace(/</g, "&lt;")
					.replace(/>/g, "&gt;")
					.replace(/"/g, "&quot;")
					.replace(/'/g, "&#39;");
			}

			function showToast(message) {
				const toast = document.getElementById("mypage-toast");
				if (!toast) {
					return;
				}
				toast.textContent = message;
				toast.classList.add("is-show");
				window.clearTimeout(window.__mypageToastTimer);
				window.__mypageToastTimer = window.setTimeout(function() {
					toast.classList.remove("is-show");
				}, 1800);
			}

			async function callTagApi(url, tagCode) {
				const body = new URLSearchParams();
				body.set("tagCode", tagCode);
				if (csrfParamName && csrfToken) {
					body.set(csrfParamName, csrfToken);
				}
				try {
					const response = await fetch(contextPath + url, {
						method: "POST",
						headers: {
							"Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
						},
						body: body.toString()
					});
					const data = await response.json();
					if (!response.ok || !data.success) {
						showToast(data && data.message ? data.message : "요청 처리에 실패했습니다.");
						return false;
					}
					return true;
				} catch (e) {
					showToast("요청 중 오류가 발생했습니다.");
					return false;
				}
			}

			function renderMyPage() {
				if (myType) {
					const avatar = document.getElementById("my-av");
					const badge = document.getElementById("my-badge");
					const desc = document.getElementById("my-desc");
					if (avatar) {
						avatar.textContent = myType.emoji;
						avatar.style.background = "rgba(255,255,255,0.2)";
					}
					if (badge) {
						badge.textContent = myType.emoji + " " + myType.name;
					}
					if (desc) {
						desc.textContent = myType.desc;
					}
				}

				const container = document.getElementById("tag-managers");
				if (!container) {
					return;
				}

				const catEntries = Object.entries(CATS);
				if (catEntries.length === 0) {
					container.innerHTML = "<p class=\"empty-text\">태그 마스터 데이터가 없습니다.</p>";
					return;
				}

				container.innerHTML = catEntries.map(function(entry) {
					const cat = entry[0];
					const info = entry[1];
					const mine = myTags[cat] || [];
					const available = (ALL_TAGS[cat] || []).filter(function(tag) {
						return !mine.some(function(myTag) { return myTag.code === tag.code; });
					});

					const items = mine.map(function(tag) {
						return "<span class=\"tag tag-item " + info.cls + "\">"
							+ escapeHtml(tag.name)
							+ "<button class=\"remove-btn\" data-action=\"remove\" data-cat=\"" + encodeURIComponent(cat)
							+ "\" data-code=\"" + encodeURIComponent(tag.code)
							+ "\" data-name=\"" + escapeHtml(tag.name)
							+ "\">✕</button></span>";
					}).join("");

					const adds = available.map(function(tag) {
						return "<button class=\"add-tag-btn\" data-action=\"add\" data-cat=\"" + encodeURIComponent(cat)
							+ "\" data-code=\"" + encodeURIComponent(tag.code)
							+ "\" data-name=\"" + escapeHtml(tag.name)
							+ "\">+ " + escapeHtml(tag.name) + "</button>";
					}).join("");

					return "<div class=\"tag-mgr-card\">"
						+ "<div class=\"tmg-head\">"
						+ "<span class=\"tmg-dot\" style=\"background:" + info.color + "\"></span>"
						+ "<span class=\"tmg-title\">" + escapeHtml(info.icon + " " + info.label) + "</span>"
						+ "</div>"
						+ "<div class=\"tmg-sub\">이 카테고리 태그가 여행 매칭에 반영돼요</div>"
						+ "<div class=\"tags\" style=\"min-height:26px\">"
						+ (items || "<span class=\"tags-empty\">태그를 추가해보세요</span>")
						+ "</div>"
						+ (available.length ? "<div class=\"add-tag-row\">" + adds + "</div>" : "")
						+ "</div>";
				}).join("");
			}

			async function addTag(cat, tagCode, tagName) {
				const mine = myTags[cat] || [];
				if (mine.some(function(tag) { return tag.code === tagCode; })) {
					return;
				}
				const ok = await callTagApi("/mypage/tags/add", tagCode);
				if (!ok) {
					return;
				}
				const all = ALL_TAGS[cat] || [];
				const found = all.find(function(tag) { return tag.code === tagCode; });
				const selected = found || { code: tagCode, name: tagName || tagCode };
				mine.push(selected);
				myTags[cat] = mine;
				renderMyPage();
				if (typeof window.renderRoutes === "function") {
					window.renderRoutes();
				}
				showToast((selected.name || tagCode) + " 태그 추가됐어요 ✓");
			}

			async function removeTag(cat, tagCode, tagName) {
				const ok = await callTagApi("/mypage/tags/remove", tagCode);
				if (!ok) {
					return;
				}
				myTags[cat] = (myTags[cat] || []).filter(function(tag) { return tag.code !== tagCode; });
				renderMyPage();
				if (typeof window.renderRoutes === "function") {
					window.renderRoutes();
				}
				showToast((tagName || tagCode) + " 태그 제거됐어요");
			}

			const container = document.getElementById("tag-managers");
			if (container) {
				container.addEventListener("click", function(event) {
					const actionButton = event.target.closest("button[data-action]");
					if (!actionButton) {
						return;
					}
					const action = actionButton.dataset.action;
					const cat = decodeURIComponent(actionButton.dataset.cat || "");
					const code = decodeURIComponent(actionButton.dataset.code || "");
					const name = actionButton.dataset.name || code;
					if (!cat || !code) {
						return;
					}
					if (action === "add") {
						addTag(cat, code, name);
						return;
					}
					if (action === "remove") {
						removeTag(cat, code, name);
					}
				});
			}

			renderMyPage();
			window.renderMyPage = renderMyPage;
		})();
	</script>

	<c:if test="${not empty wishEntryList}">
		<script>
			(function() {
				const mapElement = document.getElementById("wish-map");
				const mapStatusElement = document.getElementById("map-status");
				const categoryFilter = document.getElementById("wish-category-filter");
				const legendElement = document.getElementById("wish-category-legend");
				const placeListElement = document.getElementById("wish-place-list");
				if (!mapElement || !categoryFilter) {
					return;
				}

				const appKey = "${fn:escapeXml(kakaoMapAppKey)}";
				const places = [
					<c:forEach var="wish" items="${wishEntryList}" varStatus="status">
					{
						wishNo: ${wish.wishNo},
						placeNo: ${wish.placeNo},
						placeName: '${fn:escapeXml(empty wish.placeName ? '' : wish.placeName)}',
						categoryType: '${fn:escapeXml(empty wish.categoryType ? '기타' : wish.categoryType)}',
						lat:
						<c:choose>
							<c:when test="${wish.placeLatitude != null}">${wish.placeLatitude}</c:when>
							<c:otherwise>null</c:otherwise>
						</c:choose>,
						lng:
						<c:choose>
							<c:when test="${wish.placeLongitude != null}">${wish.placeLongitude}</c:when>
							<c:otherwise>null</c:otherwise>
						</c:choose>
					}<c:if test="${!status.last}">,</c:if>
					</c:forEach>
				];

				const validPlaces = places.filter(function(place) {
					return Number.isFinite(Number(place.lat)) && Number.isFinite(Number(place.lng));
				});
				const allCategories = [];
				places.forEach(function(place) {
					if (allCategories.indexOf(place.categoryType) < 0) {
						allCategories.push(place.categoryType);
					}
				});

				const palette = ["#2563eb", "#dc2626", "#059669", "#d97706", "#7c3aed", "#0284c7", "#be185d", "#334155"];
				const categoryColorMap = {};
				allCategories.forEach(function(category, index) {
					categoryColorMap[category] = palette[index % palette.length];
				});

				function renderCategoryFilter() {
					allCategories.forEach(function(category) {
						const option = document.createElement("option");
						option.value = category;
						option.textContent = category;
						categoryFilter.appendChild(option);
					});
				}

				function renderCategoryLegend() {
					if (!legendElement) {
						return;
					}
					legendElement.innerHTML = "";
					allCategories.forEach(function(category) {
						const item = document.createElement("span");
						item.className = "wish-category-legend-item";
						const colorDot = document.createElement("span");
						colorDot.className = "wish-category-legend-dot";
						colorDot.style.backgroundColor = categoryColorMap[category];
						const text = document.createElement("span");
						text.textContent = category;
						item.appendChild(colorDot);
						item.appendChild(text);
						legendElement.appendChild(item);
					});
				}

				function applyCategoryBadgeColors() {
					if (!placeListElement) {
						return;
					}
					const items = placeListElement.querySelectorAll("li[data-category]");
					items.forEach(function(item) {
						const category = item.dataset.category || "기타";
						const badge = item.querySelector(".wish-category-badge");
						if (!badge) {
							return;
						}
						const color = categoryColorMap[category] || "#2563eb";
						badge.style.color = color;
						badge.style.borderColor = color;
					});
				}

				function applyListFilter(category) {
					if (!placeListElement) {
						return;
					}
					const items = placeListElement.querySelectorAll("li[data-category]");
					items.forEach(function(item) {
						const itemCategory = item.dataset.category || "기타";
						const show = category === "ALL" || itemCategory === category;
						item.style.display = show ? "" : "none";
					});
				}

				renderCategoryFilter();
				renderCategoryLegend();
				applyCategoryBadgeColors();
				applyListFilter("ALL");

				let mapFilterHandler = null;
				categoryFilter.addEventListener("change", function() {
					const selected = categoryFilter.value;
					applyListFilter(selected);
					if (mapFilterHandler) {
						mapFilterHandler(selected);
					}
				});

				if (!appKey) {
					if (mapStatusElement) {
						mapStatusElement.textContent = "KAKAO_MAP_APP_KEY 설정 후 지도를 사용할 수 있습니다.";
					}
					return;
				}
				if (validPlaces.length === 0) {
					if (mapStatusElement) {
						mapStatusElement.textContent = "좌표가 있는 찜 장소가 없어 지도를 표시할 수 없습니다.";
					}
					return;
				}

				const script = document.createElement("script");
				script.src = "https://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=" + encodeURIComponent(appKey);
				script.async = true;
				script.onload = function() {
					if (!window.kakao || !window.kakao.maps) {
						if (mapStatusElement) {
							mapStatusElement.textContent = "카카오맵 로딩에 실패했습니다.";
						}
						return;
					}

					window.kakao.maps.load(function() {
						const initial = validPlaces[0];
						const map = new window.kakao.maps.Map(mapElement, {
							center: new window.kakao.maps.LatLng(Number(initial.lat), Number(initial.lng)),
							level: 7
						});

						function createMarkerImage(color) {
							const svg = "<svg xmlns='http://www.w3.org/2000/svg' width='30' height='40' viewBox='0 0 30 40'>"
								+ "<path d='M15 0C7 0 1 6 1 14c0 11 14 26 14 26s14-15 14-26C29 6 23 0 15 0z' fill='" + color + "'/>"
								+ "<circle cx='15' cy='14' r='6' fill='#ffffff'/>"
								+ "</svg>";
							return new window.kakao.maps.MarkerImage(
								"data:image/svg+xml;charset=UTF-8," + encodeURIComponent(svg),
								new window.kakao.maps.Size(30, 40),
								{ offset: new window.kakao.maps.Point(15, 40) }
							);
						}

						const markerEntries = validPlaces.map(function(place) {
							const category = place.categoryType || "기타";
							const color = categoryColorMap[category] || "#2563eb";
							const position = new window.kakao.maps.LatLng(Number(place.lat), Number(place.lng));
							const marker = new window.kakao.maps.Marker({
								position: position,
								image: createMarkerImage(color),
								title: place.placeName
							});
							return {
								category: category,
								position: position,
								marker: marker
							};
						});

						function applyMapFilter(category) {
							const bounds = new window.kakao.maps.LatLngBounds();
							let visibleCount = 0;
							let lastVisiblePosition = null;

							markerEntries.forEach(function(entry) {
								const show = category === "ALL" || entry.category === category;
								entry.marker.setMap(show ? map : null);
								if (show) {
									visibleCount++;
									lastVisiblePosition = entry.position;
									bounds.extend(entry.position);
								}
							});

							if (visibleCount > 1) {
								map.setBounds(bounds);
							} else if (visibleCount === 1 && lastVisiblePosition) {
								map.setCenter(lastVisiblePosition);
								map.setLevel(5);
							}

							if (mapStatusElement) {
								const label = category === "ALL" ? "전체" : category;
								mapStatusElement.textContent = label + " 카테고리 " + visibleCount + "건 표시 중";
							}
						}

						mapFilterHandler = applyMapFilter;
						mapFilterHandler(categoryFilter.value);
					});
				};
				script.onerror = function() {
					if (mapStatusElement) {
						mapStatusElement.textContent = "카카오맵 스크립트를 불러오지 못했습니다.";
					}
				};
				document.head.appendChild(script);
			})();
		</script>
	</c:if>
</body>
</html>
