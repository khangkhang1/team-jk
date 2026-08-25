// 화면 초안 단계 - 전부 가상 데이터 / 클라이언트 로직만 존재. 서버 연동은 이후 단계에서 처리.

var NAGOYA_CENTER = [35.1706, 136.8816]; // 나고야역 부근

// 가상 주차장 데이터 (실제 API 연동 없이 우리 회사가 직접 운영한다는 설정)
var PARKING_LOTS = [
	{ id: 1, name: "나고야역 코인파킹",   lat: 35.1706, lng: 136.8816, total: 40, remain: 12, users: 231, ev: true,  disabled: true  },
	{ id: 2, name: "사카에 센트럴 파킹",   lat: 35.1682, lng: 136.9088, total: 25, remain: 0,  users: 402, ev: false, disabled: false },
	{ id: 3, name: "오스 상점가 파킹",     lat: 35.1565, lng: 136.9019, total: 18, remain: 5,  users: 150, ev: false, disabled: true  },
	{ id: 4, name: "나고야성 인근 파킹",   lat: 35.1856, lng: 136.8998, total: 30, remain: 20, users: 98,  ev: true,  disabled: false }
];

var map = L.map('map').setView(NAGOYA_CENTER, 14);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	attribution: '&copy; OpenStreetMap contributors',
	maxZoom: 19
}).addTo(map);

var markers = {};

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

// 상세 모달
var detailModal = document.getElementById('detailModal');

function openDetail(lot) {
	document.getElementById('detailName').textContent = lot.name;
	document.getElementById('detailDistance').textContent = '약 ' + (Math.random() * 2 + 0.3).toFixed(1) + 'km';
	document.getElementById('detailRemain').textContent = lot.remain;
	document.getElementById('detailTotal').textContent = lot.total;
	document.getElementById('detailUsers').textContent = lot.users + '명';
	detailModal.classList.remove('hidden');
}

document.getElementById('detailCloseBtn').addEventListener('click', function () {
	detailModal.classList.add('hidden');
});

document.getElementById('quickReserveBtn').addEventListener('click', function () {
	alert('바로 예약 화면은 다음 단계에서 연결됩니다.');
});
document.getElementById('selectSeatBtn').addEventListener('click', function () {
	alert('좌석 맵 화면은 다음 단계에서 연결됩니다.');
});

// 새로고침 버튼 (실제 재조회는 서버 연동 후)
document.getElementById('refreshBtn').addEventListener('click', function () {
	renderMarkers(PARKING_LOTS);
});

// 필터 (장애인 / 전기차)
function applyFilters() {
	var wantDisabled = document.getElementById('filterDisabled').checked;
	var wantEv = document.getElementById('filterEv').checked;

	var filtered = PARKING_LOTS.filter(function (lot) {
		if (wantDisabled && !lot.disabled) return false;
		if (wantEv && !lot.ev) return false;
		return true;
	});
	renderMarkers(filtered);
}
document.getElementById('filterDisabled').addEventListener('change', applyFilters);
document.getElementById('filterEv').addEventListener('change', applyFilters);

// 사이드바 메뉴 - 지역 선택만 토글로 예시 구현, 나머지는 자리만 표시
document.querySelectorAll('.menuItem[data-menu]').forEach(function (item) {
	item.addEventListener('click', function (e) {
		e.preventDefault();
		var menu = item.getAttribute('data-menu');
		if (menu === 'region') {
			document.getElementById('regionArea').classList.toggle('hidden');
		} else {
			alert(item.textContent.trim() + ' 화면은 다음 단계에서 연결됩니다.');
		}
	});
});

// 내 위치 (GPS) - HTTPS/localhost 제약 있음, 실패 시 지역 선택으로 안내
document.getElementById('myLocationBtn').addEventListener('click', function (e) {
	e.preventDefault();
	if (!navigator.geolocation) {
		alert('이 브라우저는 위치 기능을 지원하지 않습니다. 지역을 선택해주세요.');
		document.getElementById('regionArea').classList.remove('hidden');
		return;
	}
	navigator.geolocation.getCurrentPosition(
		function (pos) {
			map.setView([pos.coords.latitude, pos.coords.longitude], 15);
		},
		function () {
			alert('위치 정보를 가져올 수 없습니다 (HTTPS/localhost 환경에서만 동작). 지역을 선택해주세요.');
			document.getElementById('regionArea').classList.remove('hidden');
		}
	);
});

document.getElementById('loginBtn').addEventListener('click', function () {
	alert('로그인 화면은 다음 단계에서 연결됩니다.');
});
document.getElementById('joinBtn').addEventListener('click', function () {
	alert('회원가입 화면은 다음 단계에서 연결됩니다.');
});
document.getElementById('searchBtn').addEventListener('click', function () {
	alert('자연어 검색(AI)은 2군 기능 - 다음 단계에서 연결됩니다.');
});
