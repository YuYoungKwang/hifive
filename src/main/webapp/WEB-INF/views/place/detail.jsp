<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>여행지 상세</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fragments/main-layout.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/place/detail.css">
</head>
<body>
	<jsp:include page="/WEB-INF/views/fragments/mainPage-Header.jsp" />

	<div class="page-shell">
		<header class="appbar">
			<button class="ghost-btn" type="button" onclick="history.back()">뒤로</button>
			<h1>여행지 상세</h1>
			<button class="ghost-btn" type="button">공유</button>
		</header>

		<c:choose>
			<c:when test="${empty place}">
				<main class="layout">
					<section class="panel not-found-panel">
						<h4>장소를 찾을 수 없습니다</h4>
						<p>place_no: ${placeNo} 데이터가 없습니다.</p>
					</section>
				</main>
			</c:when>
			<c:otherwise>
				<c:set var="heroImageUrl" value="${place.placeThumbnailUrl}" />
				<c:if test="${empty heroImageUrl and not empty photoUrlList}">
					<c:set var="heroImageUrl" value="${photoUrlList[0]}" />
				</c:if>
				<c:set var="photoHeroClass" value="photo-hero" />
				<c:if test="${empty heroImageUrl}">
					<c:set var="photoHeroClass" value="photo-hero photo-hero-fallback" />
				</c:if>

				<main class="layout">
					<section class="photo-section">
						<div class="${photoHeroClass}"
							 <c:if test="${not empty heroImageUrl}">
								style="background-image:
									radial-gradient(circle at 10% 18%, rgba(255, 255, 255, 0.25), transparent 40%),
									radial-gradient(circle at 88% 84%, rgba(255, 255, 255, 0.2), transparent 30%),
									linear-gradient(145deg, rgba(23, 47, 79, 0.5), rgba(36, 95, 167, 0.35)),
									url('${heroImageUrl}');"
							</c:if>>
							<div class="hero-label">대표 사진</div>
							<h2><c:out value="${place.placeName}" /></h2>
						</div>

						<div class="thumb-row">
							<c:choose>
								<c:when test="${not empty photoUrlList}">
									<c:forEach var="photoUrl" items="${photoUrlList}" varStatus="status">
										<c:if test="${status.count <= 8}">
											<div class="thumb-card thumb-card-image">
												<img src="${photoUrl}" alt="여행지 사진 ${status.count}">
											</div>
										</c:if>
									</c:forEach>
								</c:when>
								<c:when test="${not empty place.placeThumbnailUrl}">
									<div class="thumb-card thumb-card-image">
										<img src="${place.placeThumbnailUrl}" alt="대표 썸네일">
									</div>
								</c:when>
								<c:otherwise>
									<div class="thumb-card">사진 없음</div>
								</c:otherwise>
							</c:choose>
						</div>
					</section>

					<section class="place-head">
						<div>
							<p class="place-kind">
								<c:choose>
									<c:when test="${not empty tagNameList}">
										<c:out value="${tagNameList[0]}" />
									</c:when>
									<c:when test="${not empty place.placeCategory}">
										<c:out value="${place.placeCategory}" />
									</c:when>
									<c:otherwise>여행지</c:otherwise>
								</c:choose>
							</p>
							<h3><c:out value="${place.placeName}" /></h3>
							<p class="place-meta"><c:out value="${place.placeAddress}" /></p>
						</div>
						<c:set var="isWishedByMe" value="${not empty currentAuthId and not empty wishlistList}" />
						<div class="place-head-actions">
							<c:choose>
								<c:when test="${not empty currentAuthId}">
									<button type="button" class="wish-trigger-btn" id="wishlist-open-btn" aria-label="찜하기">
										<span class="wish-label">찜</span>
										<span class="wish-icon ${isWishedByMe ? 'is-active' : ''}">${isWishedByMe ? '♥' : '♡'}</span>
									</button>
								</c:when>
								<c:otherwise>
									<a class="wish-trigger-btn wish-login-link" href="${pageContext.request.contextPath}/auth/login" aria-label="로그인 후 찜하기">
										<span class="wish-label">찜</span>
										<span class="wish-icon">♡</span>
									</a>
								</c:otherwise>
							</c:choose>
							<div class="place-score">
								<p class="score-title">평점</p>
								<p class="score-value">
									<c:choose>
										<c:when test="${not empty place.placeRating}">
											<fmt:formatNumber value="${place.placeRating}" pattern="0.0" />
										</c:when>
										<c:otherwise>-</c:otherwise>
									</c:choose>
								</p>
							</div>
						</div>
					</section>

					<nav class="place-nav">
						<a href="#section-overview">기본정보</a>
						<a href="#section-map">지도</a>
						<a href="#section-review">리뷰</a>
					</nav>

					<section class="panel" id="section-overview">
						<div class="panel-head">
							<h4>기본 정보</h4>
							<span class="panel-head-sub">place_no: ${place.placeNo}</span>
						</div>
						<div class="detail-grid">
							<div class="detail-item">
								<p class="detail-label">리뷰 수</p>
								<p class="detail-value">${fn:length(reviewList)}개</p>
							</div>
							<div class="detail-item">
								<p class="detail-label">찜 수</p>
								<p class="detail-value">${wishCount}명</p>
							</div>
							<div class="detail-item">
								<p class="detail-label">전화번호</p>
								<p class="detail-value">
									<c:choose>
										<c:when test="${not empty place.placeNumber}">
											<c:out value="${place.placeNumber}" />
										</c:when>
										<c:otherwise>-</c:otherwise>
									</c:choose>
								</p>
							</div>
							<div class="detail-item">
								<p class="detail-label">주소</p>
								<p class="detail-value">
									<c:choose>
										<c:when test="${not empty place.placeAddress}">
											<c:out value="${place.placeAddress}" />
										</c:when>
										<c:otherwise>-</c:otherwise>
									</c:choose>
								</p>
							</div>
						</div>

					</section>

					<c:if test="${not empty hoursList}">
						<section class="panel" id="section-hours">
							<div class="panel-head">
								<h4>운영시간</h4>
							</div>
							<div class="hours-list">
								<c:forEach var="hour" items="${hoursList}">
									<div class="hours-item">
										<p class="hours-day">
											<c:choose>
												<c:when test="${hour.dayOfWeek == 1}">월</c:when>
												<c:when test="${hour.dayOfWeek == 2}">화</c:when>
												<c:when test="${hour.dayOfWeek == 3}">수</c:when>
												<c:when test="${hour.dayOfWeek == 4}">목</c:when>
												<c:when test="${hour.dayOfWeek == 5}">금</c:when>
												<c:when test="${hour.dayOfWeek == 6}">토</c:when>
												<c:when test="${hour.dayOfWeek == 7}">일</c:when>
												<c:otherwise>-</c:otherwise>
											</c:choose>
										</p>
										<p class="hours-time">
											<c:choose>
												<c:when test="${hour.isClosed eq 'Y'}">휴무</c:when>
												<c:when test="${not empty hour.openTime and not empty hour.closeTime}">
													<c:out value="${hour.openTime}" /> ~ <c:out value="${hour.closeTime}" />
												</c:when>
												<c:otherwise>미등록</c:otherwise>
											</c:choose>
										</p>
									</div>
								</c:forEach>
							</div>
						</section>
					</c:if>

					<section class="panel map-panel" id="section-map">
						<div class="panel-head">
							<h4>지도 위치</h4>
							<c:choose>
								<c:when test="${not empty place.placeLatitude and not empty place.placeLongitude}">
									<button class="text-btn" type="button" id="kakao-navi-btn">길찾기</button>
								</c:when>
								<c:otherwise>
									<button class="text-btn" type="button" disabled>길찾기</button>
								</c:otherwise>
							</c:choose>
						</div>
						<div id="kakao-map" class="map-canvas"
							 data-lat="${place.placeLatitude}"
							 data-lng="${place.placeLongitude}"
							 data-name="${fn:escapeXml(place.placeName)}">
						</div>
						<p class="map-status" id="map-status">
							<c:choose>
								<c:when test="${not empty place.placeAddress}">
									<c:out value="${place.placeAddress}" />
								</c:when>
								<c:otherwise>주소 정보가 없습니다.</c:otherwise>
							</c:choose>
						</p>
					</section>

					<section class="panel" id="section-review">
						<div class="panel-head">
							<h4>리뷰</h4>
							<span class="panel-head-sub">place_no: ${placeNo} | ${fn:length(reviewList)}개</span>
						</div>

						<c:if test="${not empty reviewActionMessage}">
							<p class="review-alert review-alert-success"><c:out value="${reviewActionMessage}" /></p>
						</c:if>
						<c:if test="${not empty reviewActionError}">
							<p class="review-alert review-alert-error"><c:out value="${reviewActionError}" /></p>
						</c:if>

						<form class="review-write-form" method="post" action="${pageContext.request.contextPath}/place/${placeNo}/reviews">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="hidden" id="review-rating-value" name="rating" value="5" />
							<div class="rating-row">
								<label>별점</label>
								<div class="star-rating" data-target-input="review-rating-value">
									<c:forEach var="star" begin="1" end="5">
										<button type="button" class="star-btn is-active" data-value="${star}" aria-label="${star}점">&#9733;</button>
									</c:forEach>
								</div>
							</div>
							<textarea name="commentContent" class="review-write-input" maxlength="2000" placeholder="이 여행지에 대한 리뷰를 남겨주세요." required></textarea>
							<button type="submit" class="review-submit-btn">리뷰 등록</button>
						</form>

						<c:choose>
							<c:when test="${not empty reviewList}">
									<div class="review-list">
										<c:forEach var="review" items="${reviewList}">
											<article class="review-card">
												<div class="review-top">
													<div>
														<p class="review-author">
															<c:out value="${review.createdBy}" />
														</p>
															<p class="review-rating">
																<c:forEach var="star" begin="1" end="5">
																	<c:choose>
																		<c:when test="${star <= (empty review.rating ? 0 : review.rating)}">
																			<span class="review-star is-filled">★</span>
																		</c:when>
																		<c:otherwise>
																			<span class="review-star is-empty">☆</span>
																		</c:otherwise>
																	</c:choose>
																</c:forEach>
															</p>
													</div>
													<p class="review-date">
														<c:choose>
															<c:when test="${not empty review.createdAt}">
																<fmt:formatDate value="${review.createdAt}" pattern="yyyy-MM-dd HH:mm" />
															</c:when>
															<c:otherwise>-</c:otherwise>
														</c:choose>
													</p>
												</div>
												<p class="review-content">
													<c:choose>
														<c:when test="${not empty review.commentContent}">
															<c:out value="${review.commentContent}" />
														</c:when>
													<c:otherwise>(내용 없음)</c:otherwise>
												</c:choose>
											</p>

											<c:if test="${not empty currentAuthId and review.createdByAuthId eq currentAuthId}">
												<div class="review-owner-actions">
													<div class="review-owner-buttons">
														<button type="button"
																class="review-action-btn review-edit-btn review-edit-toggle-btn"
																data-target="review-edit-form-${review.commentNo}">수정</button>
														<form class="review-delete-form" method="post" action="${pageContext.request.contextPath}/place/${placeNo}/reviews/${review.commentNo}/delete" onsubmit="return confirm('리뷰를 삭제하시겠습니까?');">
															<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
															<button type="submit" class="review-action-btn review-delete-btn">삭제</button>
														</form>
													</div>
													<form id="review-edit-form-${review.commentNo}"
														  class="review-edit-form review-edit-form-hidden"
														  method="post"
														  action="${pageContext.request.contextPath}/place/${placeNo}/reviews/${review.commentNo}/update">
														<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
														<c:set var="editRatingValue" value="${empty review.rating ? 5 : review.rating}" />
														<input type="hidden" id="review-edit-rating-${review.commentNo}" name="rating" value="${editRatingValue}" />
														<div class="rating-row">
															<label>별점</label>
															<div class="star-rating" data-target-input="review-edit-rating-${review.commentNo}">
																<c:forEach var="star" begin="1" end="5">
																	<button type="button"
																			class="star-btn ${star <= editRatingValue ? 'is-active' : ''}"
																			data-value="${star}"
																			aria-label="${star}점">&#9733;</button>
																</c:forEach>
															</div>
														</div>
														<textarea name="commentContent" class="review-edit-input" maxlength="2000" required><c:out value="${review.commentContent}" /></textarea>
														<div class="review-edit-buttons">
															<button type="submit" class="review-action-btn review-edit-btn">저장</button>
															<button type="button"
																	class="review-action-btn review-cancel-btn review-edit-cancel-btn"
																	data-target="review-edit-form-${review.commentNo}">취소</button>
														</div>
													</form>
												</div>
											</c:if>
										</article>
									</c:forEach>
								</div>
							</c:when>
							<c:otherwise>
								<p class="place-meta">등록된 리뷰가 없습니다.</p>
							</c:otherwise>
						</c:choose>
					</section>
				</main>
			</c:otherwise>
		</c:choose>
	</div>

	<c:if test="${not empty currentAuthId}">
		<div class="wishlist-modal-overlay" id="wishlist-modal-overlay">
			<div class="wishlist-modal-card" role="dialog" aria-modal="true" aria-labelledby="wishlist-modal-title">
				<div class="wishlist-modal-head">
					<h4 id="wishlist-modal-title">찜 저장</h4>
					<button type="button" class="wishlist-close-btn" id="wishlist-close-btn">닫기</button>
				</div>

				<c:if test="${not empty wishlistActionMessage}">
					<p class="review-alert review-alert-success"><c:out value="${wishlistActionMessage}" /></p>
				</c:if>
				<c:if test="${not empty wishlistActionError}">
					<p class="review-alert review-alert-error"><c:out value="${wishlistActionError}" /></p>
				</c:if>

				<div class="wishlist-modal-section">
					<p class="wishlist-modal-title">카테고리 선택 후 장소 찜</p>
					<form class="wishlist-inline-form" method="post" action="${pageContext.request.contextPath}/place/${placeNo}/wishlist">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<select name="categoryNo" class="wishlist-select" required>
							<c:set var="hasActiveCategory" value="false" />
							<c:forEach var="category" items="${wishlistCategoryList}">
								<c:if test="${category.categoryIsUsed eq 'Y'}">
									<c:set var="hasActiveCategory" value="true" />
									<option value="${category.categoryNo}"><c:out value="${category.categoryType}" /></option>
								</c:if>
							</c:forEach>
							<c:if test="${not hasActiveCategory}">
								<option value="">사용중인 카테고리가 없습니다.</option>
							</c:if>
						</select>
						<button type="submit" class="review-submit-btn" <c:if test="${not hasActiveCategory}">disabled</c:if>>저장</button>
					</form>
				</div>

				<div class="wishlist-modal-section">
					<p class="wishlist-modal-title">카테고리 만들기</p>
					<form class="wishlist-inline-form" method="post" action="${pageContext.request.contextPath}/place/${placeNo}/wishlist/categories">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<input type="text" name="categoryType" class="wishlist-input" maxlength="100" placeholder="카테고리 이름" required />
						<select name="categoryIsUsed" class="wishlist-select">
							<option value="Y" selected>사용</option>
							<option value="N">미사용</option>
						</select>
						<button type="submit" class="review-action-btn review-edit-btn">생성</button>
					</form>
				</div>

				<div class="wishlist-modal-section">
					<p class="wishlist-modal-title">내 카테고리</p>
					<c:choose>
						<c:when test="${not empty wishlistCategoryList}">
							<div class="wishlist-grid">
								<c:forEach var="category" items="${wishlistCategoryList}">
									<div class="wishlist-card">
										<p class="wishlist-name"><c:out value="${category.categoryType}" /></p>
										<p class="wishlist-sub">상태: ${category.categoryIsUsed}</p>
										<form method="post" action="${pageContext.request.contextPath}/place/${placeNo}/wishlist/categories/${category.categoryNo}/usage">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<input type="hidden" name="categoryIsUsed" value="${category.categoryIsUsed eq 'Y' ? 'N' : 'Y'}" />
											<button type="submit" class="review-action-btn review-edit-btn">
												<c:choose>
													<c:when test="${category.categoryIsUsed eq 'Y'}">미사용</c:when>
													<c:otherwise>사용</c:otherwise>
												</c:choose>
											</button>
										</form>
									</div>
								</c:forEach>
							</div>
						</c:when>
						<c:otherwise>
							<p class="place-meta">카테고리가 없습니다.</p>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="wishlist-modal-section">
					<p class="wishlist-modal-title">이 장소의 내 찜</p>
					<c:choose>
						<c:when test="${not empty wishlistList}">
							<div class="wishlist-grid">
								<c:forEach var="wish" items="${wishlistList}">
									<div class="wishlist-card">
										<p class="wishlist-name"><c:out value="${wish.categoryType}" /></p>
										<form method="post" action="${pageContext.request.contextPath}/place/${placeNo}/wishlist/${wish.wishNo}/delete" onsubmit="return confirm('찜을 삭제하시겠습니까?');">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<button type="submit" class="review-action-btn review-delete-btn">삭제</button>
										</form>
									</div>
								</c:forEach>
							</div>
						</c:when>
						<c:otherwise>
							<p class="place-meta">이 장소에 대한 찜 내역이 없습니다.</p>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</c:if>

	<jsp:include page="/WEB-INF/views/fragments/mainPage-Footer.jsp" />

	<script>
		(function() {
			const mapElement = document.getElementById("kakao-map");
			const mapStatusElement = document.getElementById("map-status");
			const naviButton = document.getElementById("kakao-navi-btn");

			if (!mapElement) {
				return;
			}

			const appKey = "${kakaoMapAppKey}";
			const lat = Number(mapElement.dataset.lat);
			const lng = Number(mapElement.dataset.lng);
			const placeName = mapElement.dataset.name || "destination";

			if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
				if (mapStatusElement) {
					mapStatusElement.textContent = "좌표 정보가 없어 지도를 표시할 수 없습니다.";
				}
				return;
			}

			if (naviButton) {
				naviButton.addEventListener("click", function() {
					const encodedName = encodeURIComponent(placeName);
					const url = "https://map.kakao.com/link/to/" + encodedName + "," + lat + "," + lng;
					window.open(url, "_blank");
				});
			}

			if (!appKey) {
				if (mapStatusElement) {
					mapStatusElement.textContent = "KAKAO_MAP_APP_KEY 설정 후 카카오맵을 사용할 수 있습니다.";
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
					const position = new window.kakao.maps.LatLng(lat, lng);
					const map = new window.kakao.maps.Map(mapElement, {
						center: position,
						level: 3
					});

					const marker = new window.kakao.maps.Marker({
						position: position
					});
					marker.setMap(map);
				});
			};
			script.onerror = function() {
				if (mapStatusElement) {
					mapStatusElement.textContent = "카카오맵 스크립트를 불러오지 못했습니다.";
				}
			};
			document.head.appendChild(script);
		})();

		(function() {
			const ratingGroups = document.querySelectorAll(".star-rating");
			ratingGroups.forEach(function(group) {
				const inputId = group.dataset.targetInput;
				const ratingInput = document.getElementById(inputId);
				if (!ratingInput) {
					return;
				}

				const starButtons = group.querySelectorAll(".star-btn");

				function paintStars(value) {
					starButtons.forEach(function(button) {
						const starValue = Number(button.dataset.value);
						if (starValue <= value) {
							button.classList.add("is-active");
						} else {
							button.classList.remove("is-active");
						}
					});
				}

				const initialValue = Number(ratingInput.value) || 5;
				paintStars(initialValue);

				starButtons.forEach(function(button) {
					button.addEventListener("click", function() {
						const selectedValue = Number(button.dataset.value) || 5;
						ratingInput.value = String(selectedValue);
						paintStars(selectedValue);
					});
				});
			});

			const openButtons = document.querySelectorAll(".review-edit-toggle-btn");
			const closeButtons = document.querySelectorAll(".review-edit-cancel-btn");

			function showEditForm(targetId) {
				const form = document.getElementById(targetId);
				if (!form) {
					return;
				}
				form.classList.remove("review-edit-form-hidden");
			}

			function hideEditForm(targetId) {
				const form = document.getElementById(targetId);
				if (!form) {
					return;
				}
				form.classList.add("review-edit-form-hidden");
			}

			openButtons.forEach(function(button) {
				button.addEventListener("click", function() {
					showEditForm(button.dataset.target);
				});
			});

			closeButtons.forEach(function(button) {
				button.addEventListener("click", function() {
					hideEditForm(button.dataset.target);
				});
			});

			const modalOverlay = document.getElementById("wishlist-modal-overlay");
			const modalOpenButton = document.getElementById("wishlist-open-btn");
			const modalCloseButton = document.getElementById("wishlist-close-btn");
			const openWishlistOnLoad = ${openWishlist or not empty wishlistActionMessage or not empty wishlistActionError};

			function openWishlistModal() {
				if (!modalOverlay) {
					return;
				}
				modalOverlay.classList.add("is-open");
				document.body.classList.add("modal-open");
			}

			function closeWishlistModal() {
				if (!modalOverlay) {
					return;
				}
				modalOverlay.classList.remove("is-open");
				document.body.classList.remove("modal-open");
			}

			if (modalOpenButton) {
				modalOpenButton.addEventListener("click", openWishlistModal);
			}

			if (modalCloseButton) {
				modalCloseButton.addEventListener("click", closeWishlistModal);
			}

			if (modalOverlay) {
				modalOverlay.addEventListener("click", function(event) {
					if (event.target === modalOverlay) {
						closeWishlistModal();
					}
				});
			}

			if (openWishlistOnLoad) {
				openWishlistModal();
			}
		})();
	</script>
</body>
</html>
