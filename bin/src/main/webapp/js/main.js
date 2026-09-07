// 화면 초안 단계 - 전부 가상 데이터 / 클라이언트 로직만 존재. 서버 연동은 이후 단계에서 처리.
// 3차 회의(2026-08-28) 전환: 나고야 가상 회사 → 인천공항 1터미널 실제 도메인.

var INCHEON_T1_CENTER = [37.4602, 126.4407]; // 인천공항 1터미널 부근

// 가상 주차구역 데이터 (실제 API 연동 전 단계 - 좌표/잔여대수는 임의값)
// zone: short(단기, 1안 위주) / long(장기, 2안 전용 구역)
var PARKING_LOTS = [
	{ id: 1, name: "단기주차장 1구역",  lat: 37.4614, lng: 126.4392, total: 40, remain: 12, users: 231, ev: true,  disabled: true,  zone: "short1" },
	{ id: 2, name: "단기주차장 2구역",  lat: 37.4595, lng: 126.4418, total: 25, remain: 0,  users: 402, ev: false, disabled: false, zone: "short2" },
	{ id: 3, name: "장기주차장 P1",     lat: 37.4585, lng: 126.4380, total: 60, remain: 25, users: 150, ev: false, disabled: true,  zone: "long1" },
	{ id: 4, name: "장기주차장 P2",     lat: 37.4625, lng: 126.4430, total: 60, remain: 40, users: 98,  ev: true,  disabled: false, zone: "long2" }
];

var map = L.map('map').setView(INCHEON_T1_CENTER, 15);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	attribution: '&copy; OpenStreetMap contributors',
	maxZoom: 19
}).addTo(map);

var markers = {};
var selectedLot = null; // 확인 팝업에서 선택된 주차구역

function countIcon(remain) {
	var cls = remain > 0 ? 'markerCount' : 'markerCount full';
	return L.divIcon({
		className: '',
		html: '<div class="' + cls + '">' + remain + '</div>',
		iconSize: [32, 32]
	});
}

function renderMarkers(list) {
	Object.keys(markers).forEach(function (id) {
		map.removeLayer(markers[id]);
	});
	markers = {};

	list.forEach(function (lot) {
		var marker = L.marker([lot.lat, lot.lng], { icon: countIcon(lot.remain) }).addTo(map);
		marker.on('click', function () {
			openDetail(lot);
		});
		markers[lot.id] = marker;
	});
}

renderMarkers(PARKING_LOTS);

// 상세 정보 + 확인 팝업 (화면설계서: 주차장 클릭 → "이용하시겠습니까?" 확인 팝업)
var detailModal = document.getElementById('detailModal');

function openDetail(lot) {
	selectedLot = lot;
	document.getElementById('detailName').textContent = lot.name;
	document.getElementById('detailDistance').textContent = '약 ' + Math.floor(Math.random() * 10 + 3) + '분';
	document.getElementById('detailRemain').textContent = lot.remain;
	document.getElementById('detailTotal').textContent = lot.total;
	document.getElementById('detailUsers').textContent = lot.users;
	detailModal.classList.remove('hidden');
}

document.getElementById('detailCloseBtn').addEventListener('click', function () {
	detailModal.classList.add('hidden');
});

// 확인 팝업에서 [확인] 클릭 시, 사이드바에서 고른 시간대를 같이 넘겨서
// 주차맵 페이지에서 그 시간 기준으로 좌석 상태를 보여준다.
// 랜덤배정 진입은 없앰 - 항상 주차맵에서 직접 좌석을 골라 결제창을 여는 흐름 하나로 통일.
function goToReservation() {
	var date = document.getElementById('mainDateInput').value;
	var start = document.getElementById('mainStartTimeInput').value;
	var qs = 'lot=' + selectedLot.id + '&date=' + encodeURIComponent(date) + '&start=' + encodeURIComponent(start);
	window.location.href = 'reservation.html?' + qs;
}
document.getElementById('selectSeatBtn').addEventListener('click', function () {
	goToReservation();
});

// 새로고침 버튼 (실제 재조회는 서버/API 연동 후)
document.getElementById('refreshBtn').addEventListener('click', function () {
	renderMarkers(PARKING_LOTS);
});

// 필터 (장애인 / 전기차)
function applyFilters() {
	var wantDisabled = document.getElementById('filterDisabled').checked;
	var wantEv = document.getElementById('filterEv').checked;
	var wantZone = document.getElementById('regionSelect').value;

	var filtered = PARKING_LOTS.filter(function (lot) {
		if (wantDisabled && !lot.disabled) return false;
		if (wantEv && !lot.ev) return false;
		if (wantZone && lot.zone !== wantZone) return false;
		return true;
	});
	renderMarkers(filtered);
}
document.getElementById('filterDisabled').addEventListener('change', applyFilters);
document.getElementById('filterEv').addEventListener('change', applyFilters);
document.getElementById('regionSelect').addEventListener('change', applyFilters);

// 사이드바 메뉴 - 구역 선택만 토글로 구현, 나머지 미구현 항목은 안내만
document.querySelectorAll('.menuItem[data-menu]').forEach(function (item) {
	item.addEventListener('click', function (e) {
		e.preventDefault();
		var menu = item.getAttribute('data-menu');
		if (menu === 'zone') {
			document.getElementById('regionArea').classList.toggle('hidden');
		} else {
			alert(item.textContent.trim() + ' 화면은 다음 단계에서 연결됩니다.');
		}
	});
});

// 내 위치 (GPS) - HTTPS/localhost 제약 있음, 실패 시 구역 선택으로 안내
document.getElementById('myLocationBtn').addEventListener('click', function (e) {
	e.preventDefault();
	if (!navigator.geolocation) {
		alert('이 브라우저는 위치 기능을 지원하지 않습니다. 구역을 선택해주세요.');
		document.getElementById('regionArea').classList.remove('hidden');
		return;
	}
	navigator.geolocation.getCurrentPosition(
		function (pos) {
			map.setView([pos.coords.latitude, pos.coords.longitude], 16);
		},
		function () {
			alert('위치 정보를 가져올 수 없습니다 (HTTPS/localhost 환경에서만 동작). 구역을 선택해주세요.');
			document.getElementById('regionArea').classList.remove('hidden');
		}
	);
});

document.getElementById('loginBtn').addEventListener('click', function () {
	window.location.href = 'login.html?tab=login';
});
document.getElementById('joinBtn').addEventListener('click', function () {
	window.location.href = 'login.html?tab=join';
});
document.getElementById('searchBtn').addEventListener('click', function () {
	alert('자연어 검색(AI)은 2군 기능 - 다음 단계에서 연결됩니다.');
});

// ------- 사이드바 시간 선택 초기화 (좌석 상태는 시간 기준으로만 판단 - 2차 회의 결론) -------
(function initMainTimeInputs() {
	var today = new Date();
	var yyyy = today.getFullYear();
	var mm = String(today.getMonth() + 1).padStart(2, '0');
	var dd = String(today.getDate()).padStart(2, '0');
	document.getElementById('mainDateInput').value = yyyy + '-' + mm + '-' + dd;

	var select = document.getElementById('mainStartTimeInput');
	for (var h = 0; h < 24; h++) {
		['00', '30'].forEach(function (m) {
			var label = String(h).padStart(2, '0') + ':' + m;
			var opt = document.createElement('option');
			opt.value = label;
			opt.textContent = label;
			select.appendChild(opt);
		});
	}
	var roundedHour = today.getHours();
	var roundedMin = today.getMinutes() < 30 ? '30' : '00';
	if (roundedMin === '00') roundedHour = (roundedHour + 1) % 24;
	select.value = String(roundedHour).padStart(2, '0') + ':' + roundedMin;
})();
