/**
 * 여행 상태 정의 (Enum)
 */
const TripStatus = {
    PLANNING: "PLANNING",   // 계획 중
    RECORDING: "RECORDING", // 기록 중 (여행 중)
    COMPLETED: "COMPLETED"  // 완료
};

/**
 * 다국어 및 UI 텍스트 관리
 */
const Texts = {
    UNKNOWN_PLACE: "장소 미지정",
    NEED_ONE_PLACE: "최소 1개 이상 장소를 추가해주세요.",
    DELETE_CONFIRM: "이 장소를 일정에서 삭제하시겠습니까?",
    KAKAO_NOT_READY: "카카오 지도 스크립트가 로드되지 않아 지도를 사용할 수 없습니다.",
    NO_SEARCH_RESULT: "검색 결과가 없습니다.",
    SELECT_PLACE_FIRST: "먼저 지도 검색 결과에서 장소를 하나 선택해주세요.",
    STATUS_PLANNING: "계획 중",
    STATUS_RECORDING: "기록 중",
    STATUS_COMPLETED: "완료",
    PROGRESS_DONE: "완료",
    INVALID_PERIOD: "종료일은 시작일보다 빠를 수 없습니다.",
    SAVE_BLOCKED: "일정 저장 준비 중 문제가 발생했습니다.",
    TITLE_REQUIRED: "일정 제목을 입력해주세요.",
    TRIP_START: "시작",
    TRIP_END: "종료",
    MEMO_LABEL: "메모",
    MEMO_PLACEHOLDER: "장소 메모를 입력해주세요.",
    DRAG_HINT: "끌어서 놓아 순서를 변경할 수 있습니다.",
    PERIOD_SUFFIX_NIGHTS: "박",
    PERIOD_SUFFIX_DAYS: "일"
};

// --- 전역 상태 변수 ---
let currentStatus = TripStatus.PLANNING; // 현재 여행 상태
let places = [];                        // 선택된 장소들의 리스트 (객체 배열)
let selectedPlace = null;               // 지도 검색에서 선택된 임시 장소
let map = null;                         // 카카오 지도 인스턴스
let marker = null;                      // 지도상의 마커
let placeService = null;                // 카카오 장소 검색 서비스
let draggedPlaceId = null;              // 드래그 중인 장소의 ID

let placeIdSeq = 0;                     // 고유 ID 생성을 위한 시퀀스
let isMapInitializing = false;          // 지도 초기화 중 여부
let mapInitPromise = null;              // 지도 초기화 비동기 제어
let isMapScriptErrorShown = false;      // 에러 메시지 중복 노출 방지

// 카카오 지도 준비 대기 설정
const KAKAO_READY_TIMEOUT_MS = 7000;
const KAKAO_READY_POLL_MS = 100;

// 애플리케이션 컨텍스트 경로 (JSP 등 서버 템플릿용)
const contextPath = (typeof window !== "undefined" && window.appContextPath) ? window.appContextPath : "";

// --- DOM 요소 캐싱 ---
const elements = {
    addPlaceBtn: document.getElementById("addPlaceBtn"),
    addPlaceSection: document.getElementById("addPlaceSection"),
    addPlaceHint: document.getElementById("addPlaceHint"),
    mapModal: document.getElementById("mapModal"),
    closeMapModal: document.getElementById("closeMapModal"),
    placeSearch: document.getElementById("placeSearch"),
    searchBtn: document.getElementById("searchBtn"),
    searchResults: document.getElementById("searchResults"),
    confirmPlace: document.getElementById("confirmPlace"),
    placeList: document.getElementById("placeList"),
    startTripBtn: document.getElementById("startTripBtn"),
    completeTripBtn: document.getElementById("completeTripBtn"),
    saveBtn: document.getElementById("saveBtn"),
    statusBadge: document.getElementById("statusBadge"),
    reviewSection: document.getElementById("reviewSection"),
    planForm: document.getElementById("planForm"),
    planDetailsJsonInput: document.getElementById("planDetailsJson"),
    tripTitle: document.getElementById("tripTitle"),
    tripStartDate: document.getElementById("tripStartDate"),
    tripEndDate: document.getElementById("tripEndDate")
};

/**
 * 초기 진입점: DOM 로드 완료 후 실행
 */
document.addEventListener("DOMContentLoaded", function () {
    bindEvents();         // 이벤트 연결
    loadInitialData();    // 기존 데이터(서버 제공) 로드
    initReviewStars();    // 별점 기능 초기화
    updateUI();           // 상태에 따른 화면 갱신
    updateTripPeriod();   // 여행 기간 계산
});

/**
 * 이벤트 바인딩 함수
 */
function bindEvents() {
    if (elements.addPlaceBtn) elements.addPlaceBtn.addEventListener("click", openMapModal);
    if (elements.closeMapModal) elements.closeMapModal.addEventListener("click", closeModal);
    if (elements.searchBtn) elements.searchBtn.addEventListener("click", searchPlace);
    if (elements.confirmPlace) elements.confirmPlace.addEventListener("click", addSelectedPlace);
    if (elements.startTripBtn) elements.startTripBtn.addEventListener("click", startTrip);
    if (elements.completeTripBtn) elements.completeTripBtn.addEventListener("click", completeTrip);
    if (elements.saveBtn) elements.saveBtn.addEventListener("click", savePlan);

    // 검색창 엔터키 지원
    if (elements.placeSearch) {
        elements.placeSearch.addEventListener("keypress", function (event) {
            if (event.key === "Enter") {
                event.preventDefault();
                searchPlace();
            }
        });
    }

    // 날짜 변경 시 기간 업데이트
    if (elements.tripStartDate) elements.tripStartDate.addEventListener("change", updateTripPeriod);
    if (elements.tripEndDate) elements.tripEndDate.addEventListener("change", updateTripPeriod);
}

/**
 * 장소 객체에 부여할 고유 ID 생성 (UUID 또는 Timestamp 조합)
 */
function createPlaceId() {
    if (typeof crypto !== "undefined" && typeof crypto.randomUUID === "function") {
        return crypto.randomUUID();
    }
    placeIdSeq += 1;
    return "p_" + Date.now().toString(36) + "_" + placeIdSeq.toString(36);
}

/**
 * 서버에서 전달받은 초기 데이터를 클라이언트 변수에 할당
 */
function loadInitialData() {
    const statusInput = document.getElementById("planStatus");
    if (statusInput && statusInput.value) {
        const normalized = String(statusInput.value).trim().toUpperCase();
        if (normalized === TripStatus.PLANNING || normalized === TripStatus.RECORDING || normalized === TripStatus.COMPLETED) {
            currentStatus = normalized;
        }
    }

    const initialDataElement = document.getElementById("initial-plan-data");
    if (!initialDataElement) {
        places = [];
        return;
    }

    let parsed = [];
    try {
        const raw = (initialDataElement.textContent || "").trim();
        parsed = raw ? JSON.parse(raw) : [];
    } catch (error) {
        parsed = [];
    }

    // 데이터 정형화
    places = (Array.isArray(parsed) ? parsed : []).map(function (item) {
        const placeNo = item && item.placeNo ? Number(item.placeNo) : null;
        return {
            id: item && item.id ? String(item.id) : createPlaceId(),
            placeNo: Number.isFinite(placeNo) && placeNo > 0 ? placeNo : null,
            name: item && item.placeName ? String(item.placeName) : Texts.UNKNOWN_PLACE,
            address: item && item.placeAddress ? String(item.placeAddress) : "",
            lat: item ? toNullableNumber(item.placeLatitude) : null,
            lng: item ? toNullableNumber(item.placeLongitude) : null,
            date: item && item.date ? String(item.date) : "",
            time: item && item.time ? String(item.time) : "",
            endDate: item && item.endDate ? String(item.endDate) : "",
            endTime: item && item.endTime ? String(item.endTime) : "",
            memo: item && item.memo ? String(item.memo) : ""
        };
    });
}

/**
 * '여행 시작' 버튼 클릭 시 상태 변경
 */
function startTrip() {
    if (!Array.isArray(places) || places.length === 0) {
        alert(Texts.NEED_ONE_PLACE);
        return;
    }
    currentStatus = TripStatus.RECORDING;
    const statusInput = document.getElementById("planStatus");
    if (statusInput) statusInput.value = TripStatus.RECORDING;
    updateUI();
}

/**
 * '여행 완료' 버튼 클릭 시 상태 변경
 */
function completeTrip() {
    currentStatus = TripStatus.COMPLETED;
    const statusInput = document.getElementById("planStatus");
    if (statusInput) statusInput.value = TripStatus.COMPLETED;
    updateUI();
}

/**
 * 상태(PLANNING/RECORDING/COMPLETED)에 따라 UI 요소의 노출 여부 제어
 */
function updateUI() {
    if (!elements.statusBadge) return;

    if (currentStatus === TripStatus.PLANNING) {
        elements.statusBadge.textContent = Texts.STATUS_PLANNING;
        elements.statusBadge.className = "status-badge status-planning";
        // 계획 중일 때: 장소 추가 가능, 리뷰 섹션 숨김
        if (elements.addPlaceSection) elements.addPlaceSection.classList.remove("hidden");
        if (elements.addPlaceHint) elements.addPlaceHint.classList.add("hidden");
        if (elements.startTripBtn) elements.startTripBtn.classList.remove("hidden");
        if (elements.completeTripBtn) elements.completeTripBtn.classList.add("hidden");
        if (elements.reviewSection) elements.reviewSection.classList.add("hidden");
    } else if (currentStatus === TripStatus.RECORDING) {
        elements.statusBadge.textContent = Texts.STATUS_RECORDING;
        elements.statusBadge.className = "status-badge status-recording";
        // 여행 중일 때: 장소 편집 가능, 별점 섹션 보임
        if (elements.addPlaceSection) elements.addPlaceSection.classList.remove("hidden");
        if (elements.addPlaceHint) elements.addPlaceHint.classList.remove("hidden");
        if (elements.startTripBtn) elements.startTripBtn.classList.add("hidden");
        if (elements.completeTripBtn) elements.completeTripBtn.classList.remove("hidden");
        if (elements.reviewSection) elements.reviewSection.classList.remove("hidden");
    } else {
        elements.statusBadge.textContent = Texts.STATUS_COMPLETED;
        elements.statusBadge.className = "status-badge status-complete";
        // 완료 시: 장소 추가 불가
        if (elements.addPlaceSection) elements.addPlaceSection.classList.add("hidden");
        if (elements.addPlaceHint) elements.addPlaceHint.classList.add("hidden");
        if (elements.startTripBtn) elements.startTripBtn.classList.add("hidden");
        if (elements.completeTripBtn) elements.completeTripBtn.classList.add("hidden");
        if (elements.reviewSection) elements.reviewSection.classList.remove("hidden");
    }

    renderPlaces(); // 리스트 다시 그리기
    updateProgress(); // 진행률 바 업데이트
}

/**
 * 현재 places 배열을 바탕으로 화면에 장소 카드 리스트를 생성
 */
function renderPlaces(options) {
    if (!elements.placeList) return;

    // 리스트를 다시 그릴 때 현재 입력 중인 포커스 상태 저장
    const preserveFocus = Boolean(options && options.preserveFocus);
    const focusState = preserveFocus ? captureFocusState() : null;

    elements.placeList.innerHTML = "";

    places.forEach(function (place) {
        const card = document.createElement("div");
        card.className = "place-card";
        card.draggable = currentStatus === TripStatus.PLANNING; // 계획 중일 때만 드래그 가능
        card.dataset.placeId = String(place.id);

        const placeName = escapeHtml(place.name || Texts.UNKNOWN_PLACE);
        const address = place.address
            ? '<div style="font-size:12px;color:#64748b;margin-bottom:8px;">' + escapeHtml(place.address) + "</div>"
            : "";

        const nameHtml = place.placeNo
            ? '<button type="button" class="place-name place-link" data-place-no="' + Number(place.placeNo) + '">' + placeName + "</button>"
            : '<div class="place-name">' + placeName + "</div>";

        const dragHint = currentStatus === TripStatus.PLANNING
            ? '<span class="drag-hint">' + Texts.DRAG_HINT + "</span>"
            : "";

        // HTML 생성
        card.innerHTML =
            dragHint +
            nameHtml +
            address +
            '<div class="place-datetime">' +
            '  <div class="datetime-row">' +
            '    <label>' + Texts.TRIP_START + "</label>" +
            '    <input type="date" data-field="date" value="' + escapeHtml(place.date || "") + '">' +
            '    <input type="time" data-field="time" value="' + escapeHtml(place.time || "") + '">' +
            "  </div>" +
            '  <div class="datetime-row">' +
            '    <label>' + Texts.TRIP_END + "</label>" +
            '    <input type="date" data-field="endDate" value="' + escapeHtml(place.endDate || "") + '">' +
            '    <input type="time" data-field="endTime" value="' + escapeHtml(place.endTime || "") + '">' +
            "  </div>" +
            "</div>" +
            '<div class="place-memo">' +
            "  <label>" + Texts.MEMO_LABEL + "</label>" +
            '  <textarea data-field="memo" placeholder="' + escapeHtml(Texts.MEMO_PLACEHOLDER) + '">' + escapeHtml(place.memo || "") + "</textarea>" +
            "</div>" +
            '<button type="button" class="delete-place">&times;</button>';

        // 입력 값 변경 시 실시간 데이터 동기화
        card.querySelectorAll("[data-field]").forEach(function (fieldEl) {
            fieldEl.addEventListener("input", function () {
                updatePlaceById(place.id, fieldEl.dataset.field, fieldEl.value);
            });
        });

        // 삭제 버튼 처리
        const deleteBtn = card.querySelector(".delete-place");
        if (deleteBtn) {
            deleteBtn.addEventListener("click", function () {
                deletePlaceById(place.id);
            });
        }

        // 장소 이름 클릭 시 상세정보 이동
        const placeLink = card.querySelector(".place-link");
        if (placeLink) {
            placeLink.addEventListener("click", function (event) {
                openPlaceDetail(event, Number(placeLink.dataset.placeNo));
            });
        }

        // 드래그 앤 드롭 이벤트 리스너
        card.addEventListener("dragstart", onDragStart);
        card.addEventListener("dragover", onDragOver);
        card.addEventListener("drop", onDrop);

        elements.placeList.appendChild(card);
    });

    // 포커스 복원
    if (focusState) {
        restoreFocusState(focusState);
    }

    updateProgress();
}

/**
 * ID로 장소 배열 인덱스 찾기
 */
function findPlaceIndexById(placeId) {
    return places.findIndex(function (place) {
        return String(place.id) === String(placeId);
    });
}

/**
 * 특정 장소의 필드 값(날짜, 메모 등) 수정
 */
function updatePlaceById(placeId, key, value) {
    const index = findPlaceIndexById(placeId);
    if (index < 0) return;
    places[index][key] = value;
    updateProgress(); // 진행률 갱신
}

/**
 * 장소 삭제
 */
function deletePlaceById(placeId) {
    const index = findPlaceIndexById(placeId);
    if (index < 0) return;
    if (!confirm(Texts.DELETE_CONFIRM)) return;

    places.splice(index, 1);
    renderPlaces({ preserveFocus: true });
}

// --- 드래그 앤 드롭 로직 ---
function onDragStart(event) {
    if (currentStatus !== TripStatus.PLANNING) return;
    draggedPlaceId = event.currentTarget.dataset.placeId || null;
    event.currentTarget.classList.add("dragging");
}

function onDragOver(event) {
    if (currentStatus !== TripStatus.PLANNING) return;
    event.preventDefault(); // 드롭을 허용하기 위해 필요
}

function onDrop(event) {
    if (currentStatus !== TripStatus.PLANNING) return;
    event.preventDefault();

    const targetPlaceId = event.currentTarget.dataset.placeId || null;
    document.querySelectorAll(".place-card.dragging").forEach(function (element) {
        element.classList.remove("dragging");
    });

    if (!draggedPlaceId || !targetPlaceId || draggedPlaceId === targetPlaceId) {
        draggedPlaceId = null;
        return;
    }

    const fromIndex = findPlaceIndexById(draggedPlaceId);
    const toIndex = findPlaceIndexById(targetPlaceId);
    if (fromIndex < 0 || toIndex < 0 || fromIndex === toIndex) {
        draggedPlaceId = null;
        return;
    }

    // 배열 순서 변경
    const moved = places.splice(fromIndex, 1)[0];
    places.splice(toIndex, 0, moved);
    draggedPlaceId = null;
    renderPlaces({ preserveFocus: true });
}

/**
 * 입력 중인 필드의 포커스와 커서 위치 정보를 캡처
 */
function captureFocusState() {
    if (!elements.placeList) return null;
    const active = document.activeElement;
    if (!active || !elements.placeList.contains(active)) return null;

    const card = active.closest(".place-card");
    if (!card) return null;

    const state = { 
        placeId: card.dataset.placeId, 
        field: active.dataset.field, 
        selectionStart: null, 
        selectionEnd: null 
    };

    // 텍스트 계열 입력창인 경우 커서 위치 저장
    if (active.tagName === "TEXTAREA" || (active.tagName === "INPUT" && /text|search|tel|url/.test(active.type))) {
        try {
            state.selectionStart = active.selectionStart;
            state.selectionEnd = active.selectionEnd;
        } catch (e) { }
    }
    return state;
}

/**
 * 저장된 포커스 상태를 리스트 갱신 후 복원
 */
function restoreFocusState(state) {
    if (!state || !elements.placeList) return;
    const card = Array.from(elements.placeList.querySelectorAll(".place-card")).find(function (el) {
        return el.dataset.placeId === state.placeId;
    });
    if (!card) return;

    const target = card.querySelector('[data-field="' + state.field + '"]');
    if (!target) return;

    target.focus();
    if (state.selectionStart !== null && state.selectionEnd !== null && typeof target.setSelectionRange === "function") {
        try {
            target.setSelectionRange(state.selectionStart, state.selectionEnd);
        } catch (error) { }
    }
}

/**
 * 방문 기록이 있는 장소의 개수를 세어 진행률 바 업데이트
 */
function updateProgress() {
    const progressSection = document.getElementById("progressSection");
    const progressText = document.getElementById("progressText");
    const progressPercent = document.getElementById("progressPercent");
    const progressFill = document.getElementById("progressFill");

    if (!progressSection || !progressText || !progressPercent || !progressFill) return;

    // 장소가 없거나 계획 중이면 진행 바 숨김
    if (!Array.isArray(places) || places.length === 0 || (currentStatus !== TripStatus.RECORDING && currentStatus !== TripStatus.COMPLETED)) {
        progressSection.classList.add("hidden");
        return;
    }

    progressSection.classList.remove("hidden");

    // 완료 상태면 100%, 여행 중이면 기록 여부 체크
    const doneCount = currentStatus === TripStatus.COMPLETED
        ? places.length
        : places.filter(hasAnyVisitRecord).length;
    const percent = Math.round((doneCount / places.length) * 100);

    progressText.textContent = doneCount + " / " + places.length + " " + Texts.PROGRESS_DONE;
    progressPercent.textContent = percent + "%";
    progressFill.style.width = percent + "%";
}

/**
 * 장소 카드에 날짜나 메모 등 사용자가 기록한 내용이 있는지 확인
 */
function hasAnyVisitRecord(place) {
    if (!place) return false;
    return Boolean(
        (place.date && String(place.date).trim()) ||
        (place.time && String(place.time).trim()) ||
        (place.endDate && String(place.endDate).trim()) ||
        (place.endTime && String(place.endTime).trim()) ||
        (place.memo && String(place.memo).trim())
    );
}

/**
 * 최종 일정 데이터를 JSON으로 직렬화하여 서버로 전송(Submit)
 */
function savePlan(event) {
    if (event) event.preventDefault();

    if (!elements.planForm || !elements.planDetailsJsonInput) {
        alert(Texts.SAVE_BLOCKED);
        return;
    }

    if (!elements.tripTitle || !elements.tripTitle.value.trim()) {
        alert(Texts.TITLE_REQUIRED);
        if (elements.tripTitle) elements.tripTitle.focus();
        return;
    }

    if (!Array.isArray(places) || places.length === 0) {
        alert(Texts.NEED_ONE_PLACE);
        return;
    }

    // 서버 DB 구조에 맞춰 데이터 매핑
    const payload = places.map(function (place) {
        return {
            placeNo: place.placeNo || null,
            placeName: place.name || "",
            placeAddress: place.address || "",
            placeLatitude: toNullableNumber(place.lat),
            placeLongitude: toNullableNumber(place.lng),
            date: place.date || "",
            time: place.time || "",
            endDate: place.endDate || "",
            endTime: place.endTime || "",
            memo: place.memo || ""
        };
    });

    elements.planDetailsJsonInput.value = JSON.stringify(payload);
    elements.planForm.submit();
}

// --- 카카오 지도 API 관련 로직 ---

function openMapModal() {
    if (!elements.mapModal) return;
    elements.mapModal.classList.remove("hidden");
    ensureMapReady().catch(function () { });
}

function closeModal() {
    if (!elements.mapModal) return;
    elements.mapModal.classList.add("hidden");
    selectedPlace = null;
    if (elements.searchResults) elements.searchResults.innerHTML = "";
}

function showMapScriptErrorOnce() {
    if (isMapScriptErrorShown) return;
    alert(Texts.KAKAO_NOT_READY);
    isMapScriptErrorShown = true;
}

/**
 * 카카오 지도 스크립트가 브라우저에 로드될 때까지 대기
 */
function waitForKakaoMapsReady() {
    return new Promise(function (resolve, reject) {
        if (window.kakao && window.kakao.maps) {
            resolve();
            return;
        }

        const startedAt = Date.now();
        const timer = setInterval(function () {
            if (window.kakao && window.kakao.maps) {
                clearInterval(timer);
                resolve();
                return;
            }

            if (Date.now() - startedAt >= KAKAO_READY_TIMEOUT_MS) {
                clearInterval(timer);
                reject(new Error("Kakao maps script timeout"));
            }
        }, KAKAO_READY_POLL_MS);
    });
}

/**
 * 지도 객체 및 검색 서비스 인스턴스 생성
 */
function initializeMapInstance() {
    const container = document.getElementById("map");
    if (!container) throw new Error("Map container not found");
    if (!window.kakao || !window.kakao.maps || !window.kakao.maps.services) {
        throw new Error("Kakao maps not ready");
    }

    map = new kakao.maps.Map(container, {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 5
    });
    marker = new kakao.maps.Marker();
    placeService = new kakao.maps.services.Places();
}

/**
 * 지도가 준비되었음을 보장하는 비동기 함수
 */
function ensureMapReady() {
    if (map && placeService) return Promise.resolve();
    if (isMapInitializing) return mapInitPromise;

    isMapInitializing = true;
    mapInitPromise = waitForKakaoMapsReady()
        .then(function() {
            return new Promise(function(resolve, reject) {
                // 비동기 로딩(autoload=false) 환경 대응
                window.kakao.maps.load(function() {
                    try {
                        initializeMapInstance();
                        resolve();
                    } catch (e) { reject(e); }
                });
            });
        })
        .catch(function(err) {
            showMapScriptErrorOnce();
            throw err;
        })
        .finally(function() {
            isMapInitializing = false;
        });

    return mapInitPromise;
}

/**
 * 키워드로 장소 검색
 */
function searchPlace() {
    ensureMapReady().then(function () {
        const keyword = elements.placeSearch ? elements.placeSearch.value.trim() : "";
        if (!keyword) return;

        placeService.keywordSearch(keyword, function (data, status) {
            if (status !== kakao.maps.services.Status.OK || !Array.isArray(data) || data.length === 0) {
                if (elements.searchResults) {
                    elements.searchResults.innerHTML = '<div class="search-result-item">' + Texts.NO_SEARCH_RESULT + "</div>";
                }
                return;
            }
            renderSearchResults(data); // 검색 결과 리스트 표시

            // 검색된 장소들이 모두 보이도록 지도 영역 확장
            const bounds = new kakao.maps.LatLngBounds();
            data.forEach(function (item) {
                const y = Number(item.y);
                const x = Number(item.x);
                if (Number.isFinite(y) && Number.isFinite(x)) {
                    bounds.extend(new kakao.maps.LatLng(y, x));
                }
            });
            map.setBounds(bounds);
        });
    }).catch(function () { });
}

/**
 * 검색 결과 목록을 모달창 내에 렌더링
 */
function renderSearchResults(results) {
    if (!elements.searchResults) return;

    elements.searchResults.innerHTML = results.map(function (item) {
        const name = escapeHtml(item.place_name || Texts.UNKNOWN_PLACE);
        const address = escapeHtml(item.road_address_name || item.address_name || "");
        const lat = escapeHtml(String(item.y || ""));
        const lng = escapeHtml(String(item.x || ""));

        return (
            '<div class="search-result-item" data-name="' + name + '" data-address="' + address + '" data-lat="' + lat + '" data-lng="' + lng + '">' +
            "<strong>" + name + "</strong>" +
            '<div style="font-size:12px;color:#64748b;margin-top:4px;">' + address + "</div>" +
            "</div>"
        );
    }).join("");

    // 각 결과 항목 클릭 이벤트
    elements.searchResults.querySelectorAll(".search-result-item").forEach(function (el) {
        el.addEventListener("click", function () {
            selectSearchResult(el);
        });
    });
}

/**
 * 검색 목록 중 하나를 클릭했을 때 지도를 해당 위치로 이동하고 선택 상태로 저장
 */
function selectSearchResult(element) {
    if (!element || !elements.searchResults) return;

    elements.searchResults.querySelectorAll(".search-result-item").forEach(function (el) {
        el.style.background = "";
    });
    element.style.background = "#e8f4fd";

    const lat = Number(element.dataset.lat);
    const lng = Number(element.dataset.lng);
    const pos = new kakao.maps.LatLng(lat, lng);
    
    map.setCenter(pos);
    marker.setPosition(pos);
    marker.setMap(map);

    selectedPlace = {
        id: createPlaceId(),
        placeNo: null,
        name: element.dataset.name || Texts.UNKNOWN_PLACE,
        address: element.dataset.address || "",
        lat: Number.isFinite(lat) ? lat : null,
        lng: Number.isFinite(lng) ? lng : null,
        date: "", time: "", endDate: "", endTime: "", memo: ""
    };
}

/**
 * '장소 추가' 버튼 클릭 시 선택된 장소를 실제 여행 일정(places)에 추가
 */
function addSelectedPlace() {
    if (!selectedPlace) {
        alert(Texts.SELECT_PLACE_FIRST);
        return;
    }
    places.push(selectedPlace);
    renderPlaces({ preserveFocus: true });
    closeModal();
}

/**
 * 전체 여행 시작일/종료일로부터 며칠간의 여행인지 계산 (N박 M일)
 */
function updateTripPeriod() {
    const start = elements.tripStartDate ? elements.tripStartDate.value : "";
    const end = elements.tripEndDate ? elements.tripEndDate.value : "";
    const summary = document.getElementById("periodSummary");

    if (!summary) return;
    if (!start || !end) {
        summary.classList.add("hidden");
        return;
    }
    if (start > end) {
        alert(Texts.INVALID_PERIOD);
        if (elements.tripEndDate) elements.tripEndDate.value = "";
        summary.classList.add("hidden");
        return;
    }

    const diffDays = Math.floor((new Date(end) - new Date(start)) / (1000 * 60 * 60 * 24)) + 1;
    const nights = Math.max(diffDays - 1, 0);
    summary.innerHTML =
        '<span class="period-days">' + nights + Texts.PERIOD_SUFFIX_NIGHTS + " " + diffDays + Texts.PERIOD_SUFFIX_DAYS + "</span>" +
        '<span class="period-text">' + formatDate(start) + " ~ " + formatDate(end) + "</span>";
    summary.classList.remove("hidden");
}

/**
 * YYYY-MM-DD 포맷을 'M월 D일' 형태로 변환
 */
function formatDate(dateValue) {
    if (!dateValue) return "";
    const date = new Date(dateValue);
    if (Number.isNaN(date.getTime())) return dateValue;
    return (date.getMonth() + 1) + "월 " + date.getDate() + "일";
}

/**
 * 장소 상세 페이지로 이동
 */
function openPlaceDetail(event, placeNo) {
    if (event && typeof event.stopPropagation === "function") {
        event.stopPropagation();
    }
    if (!placeNo) return;
    location.href = contextPath + "/place/detail?place_no=" + encodeURIComponent(placeNo);
}

/**
 * 별점 인터랙션 초기화
 */
function initReviewStars() {
    const wrap = document.getElementById("totalStars");
    if (!wrap) return;

    const stars = Array.from(wrap.querySelectorAll("i[data-value]"));
    if (stars.length === 0) return;

    const apply = function (rating) {
        stars.forEach(function (star) {
            const value = Number(star.dataset.value);
            if (value <= rating) {
                star.classList.remove("far");
                star.classList.add("fas"); // 칠해진 별
            } else {
                star.classList.remove("fas");
                star.classList.add("far"); // 빈 별
            }
        });
        wrap.dataset.rating = String(rating);
    };

    stars.forEach(function (star) {
        star.addEventListener("click", function () {
            const value = Number(star.dataset.value);
            apply(Number.isFinite(value) ? value : 0);
        });
    });

    const initial = Number(wrap.dataset.rating || "0");
    apply(Number.isFinite(initial) ? initial : 0);
}

/**
 * 빈 문자열 등을 null이나 숫자로 안전하게 변환
 */
function toNullableNumber(value) {
    if (value === null || value === undefined || value === "") return null;
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
}

/**
 * HTML 특수문자 이스케이프 (보안 처리)
 */
function escapeHtml(value) {
    return String(value || "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

// 외부(HTML)에서 호출 가능하도록 글로벌 바인딩
window.updateTripPeriod = updateTripPeriod;
window.openPlaceDetail = openPlaceDetail;