// 주차맵+결제 통합 페이지 - 화면 초안, 전부 가상 데이터/클라이언트 로직만 존재. 서버 연동은 이후 단계.
// 3차 회의(2026-08-28) 반영: ① 이용방식(1안 예약형 / 2안 자유출차형) 선택이 첫 단계로 들어옴.

var HOURLY_PRICE = 3000;      // 1안 시간당 요금 (원)
var PLAN2_HOURLY_PRICE = 4500; // 2안 페널티 요금 (원, 더 비쌈)
var DEPOSIT_PRICE = 5000;      // 예약금(고정) - 실제 총 이용료는 출차 시 정산

// 메인 화면 PARKING_LOTS 최소 정보(이름/요금) - id로 매칭. 실제로는 서버에서 조회.
var LOT_INFO = {
	1: { name: "단기주차장 1구역", addr: "인천공항 1터미널 · 단기", price: HOURLY_PRICE },
	2: { name: "단기주차장 2구역", addr: "인천공항 1터미널 · 단기", price: HOURLY_PRICE },
	3: { name: "장기주차장 P1",   addr: "인천공항 1터미널 · 장기", price: PLAN2_HOURLY_PRICE },
	4: { name: "장기주차장 P2",   addr: "인천공항 1터미널 · 장기", price: PLAN2_HOURLY_PRICE }
};

// 층별 좌석 데이터 (5행 x 8열). type: normal / disabled / ev / gate / cancelled(결항 재배정중)
function buildFloorSeats(floor) {
	var rows = 5, cols = 8;
	var seats = [];
	for (var r = 0; r < rows; r++) {
		for (var c = 0; c < cols; c++) {
			var id = floor + '-' + r + '-' + c;
			var type = 'normal';
			if (r === 0 && c === 0) type = 'gate';
			else if (r === 0 && (c === 3 || c === 4)) type = 'disabled';
			else if (r === rows - 1 && (c === 0 || c === 1)) type = 'ev';
			else if (r === 2 && c === 6 && floor === 1) type = 'cancelled'; // 결항 대응 로직 데모용 - 재배정 중 좌석

			// 가상 예약 데이터: 일정 비율 랜덤으로 예약됨 처리 (층마다 다르게 시드)
			var seedVal = (r * 8 + c + floor * 13) % 7;
			var taken = (type === 'normal' || type === 'disabled' || type === 'ev') && (seedVal === 0 || seedVal === 3);

			seats.push({ id: id, row: r, col: c, type: type, taken: taken });
		}
	}
	return seats;
}

var FLOOR_DATA = {
	1: buildFloorSeats(1),
	2: buildFloorSeats(2)
};

var state = {
	plan: null,          // '1' or '2'
	floor: 1,
	selectedSeat: null,
	timeChosen: false,
	payMethod: null
};

// ------- URL 파라미터로 주차구역/시간/모드 전달받기 (메인 화면 확인팝업 → 이 페이지) -------
var urlParams = new URLSearchParams(window.location.search);
(function applyLotFromUrl() {
	var lotId = urlParams.get('lot');
	var info = LOT_INFO[lotId] || LOT_INFO[1];
	document.getElementById('lotName').textContent = info.name;
	document.getElementById('lotAddr').innerHTML = info.addr + ' · 시간당 <span id="lotPrice">' + info.price.toLocaleString() + '</span>원';
})();

// ------- ① 이용 방식 선택 (1안/2안) -------
document.querySelectorAll('input[name="planType"]').forEach(function (radio) {
	radio.addEventListener('change', function () {
		state.plan = radio.value;
		document.querySelectorAll('.planCard').forEach(function (card) {
			card.classList.toggle('selected', card.querySelector('input').checked);
		});

		var isPlan1 = state.plan === '1';
		document.querySelectorAll('.plan1Only').forEach(function (el) { el.classList.toggle('hidden', !isPlan1); });
		document.querySelectorAll('.plan2Only').forEach(function (el) { el.classList.toggle('hidden', isPlan1); });

		document.getElementById('planSummary').textContent =
			isPlan1 ? '1안 (예약형)' : '2안 (자유출차형)';

		updateEstimatedPrice();
	});
});

// ------- 시간 선택 -------
function initTimeInputs() {
	var today = new Date();
	var yyyy = today.getFullYear();
	var mm = String(today.getMonth() + 1).padStart(2, '0');
	var dd = String(today.getDate()).padStart(2, '0');
	var dateFromUrl = urlParams.get('date');
	document.getElementById('dateInput').value = dateFromUrl || (yyyy + '-' + mm + '-' + dd);

	var select = document.getElementById('startTimeInput');
	for (var h = 0; h < 24; h++) {
		['00', '30'].forEach(function (m) {
			var label = String(h).padStart(2, '0') + ':' + m;
			var opt = document.createElement('option');
			opt.value = label;
			opt.textContent = label;
			select.appendChild(opt);
		});
	}
	var startFromUrl = urlParams.get('start');
	if (startFromUrl) {
		select.value = startFromUrl;
	} else {
		var roundedHour = today.getHours();
		var roundedMin = today.getMinutes() < 30 ? '30' : '00';
		if (roundedMin === '00') roundedHour = (roundedHour + 1) % 24;
		select.value = String(roundedHour).padStart(2, '0') + ':' + roundedMin;
	}
}

function updateEstimatedPrice() {
	var date = document.getElementById('dateInput').value;
	var start = document.getElementById('startTimeInput').value;
	var lotId = urlParams.get('lot') || 1;
	var info = LOT_INFO[lotId] || LOT_INFO[1];

	if (state.plan === '2') {
		document.getElementById('estimatedPrice').textContent =
			'출차 시 정산 (시간당 ' + PLAN2_HOURLY_PRICE.toLocaleString() + '원, 페널티 요금)';
	} else {
		var duration = parseInt(document.getElementById('durationInput').value, 10);
		var total = duration * info.price;
		document.getElementById('estimatedPrice').textContent = total.toLocaleString() + '원';
	}

	state.timeChosen = !!(date && start);
	refreshPayBar();
}

// ------- 좌석 선택 -------
function seatLabel(seat) {
	if (seat.type === 'gate') return '';
	if (seat.type === 'disabled') return '♿';
	if (seat.type === 'ev') return '🔌';
	if (seat.type === 'cancelled') return '✈️';
	return (seat.row + 1) + '-' + (seat.col + 1);
}

function renderSeats() {
	var grid = document.getElementById('seatGrid');
	grid.innerHTML = '';
	var seats = FLOOR_DATA[state.floor];
	var remain = 0;

	seats.forEach(function (seat) {
		var cell = document.createElement('div');
		cell.className = 'seatCell';
		if (seat.type === 'gate') {
			cell.classList.add('gate');
			cell.textContent = '입구';
		} else {
			if (seat.type === 'disabled') cell.classList.add('disabled-seat');
			if (seat.type === 'ev') cell.classList.add('ev-seat');
			if (seat.type === 'cancelled') cell.classList.add('cancelled-seat');
			if (seat.taken) {
				cell.classList.add('taken');
			} else if (seat.type !== 'cancelled') {
				remain++;
			}
			if (state.selectedSeat && state.selectedSeat.id === seat.id) {
				cell.classList.add('selected');
			}
			cell.textContent = seatLabel(seat);
			cell.addEventListener('click', function () {
				onSeatClick(seat);
			});
		}
		grid.appendChild(cell);
	});

	document.getElementById('floorRemainCount').textContent = remain;
}

function onSeatClick(seat) {
	if (seat.type === 'cancelled') {
		alert('항공편 결항으로 재배정 처리 중인 자리입니다. 잠시 후 다시 확인해주세요.');
		return;
	}
	if (seat.taken) {
		alert('이미 예약된 자리입니다.');
		return;
	}
	if (!confirm((seat.row + 1) + '층 ' + (seat.col + 1) + '번 자리를 선택하시겠습니까?')) return;

	state.selectedSeat = { id: seat.id, floor: state.floor, row: seat.row, col: seat.col, type: seat.type };
	document.getElementById('seatSummary').textContent =
		state.floor + '층 ' + (seat.row + 1) + '-' + (seat.col + 1) + '번';
	renderSeats();
	refreshPayBar();
}

function randomAssign() {
	var seats = FLOOR_DATA[state.floor].filter(function (s) {
		return s.type !== 'gate' && s.type !== 'cancelled' && !s.taken;
	});
	if (seats.length === 0) {
		alert('이 층에는 빈 자리가 없습니다. 다른 층을 선택해주세요.');
		return;
	}
	var picked = seats[Math.floor(Math.random() * seats.length)];
	state.selectedSeat = { id: picked.id, floor: state.floor, row: picked.row, col: picked.col, type: picked.type };
	document.getElementById('seatSummary').textContent =
		'랜덤 배정 · ' + state.floor + '층 ' + (picked.row + 1) + '-' + (picked.col + 1) + '번';
	renderSeats();
	refreshPayBar();
}

document.querySelectorAll('.floorTab').forEach(function (btn) {
	btn.addEventListener('click', function () {
		document.querySelectorAll('.floorTab').forEach(function (b) { b.classList.remove('active'); });
		btn.classList.add('active');
		state.floor = parseInt(btn.dataset.floor, 10);
		renderSeats();
	});
});
document.getElementById('randomAssignBtn').addEventListener('click', randomAssign);

// ------- 결제 수단 -------
document.querySelectorAll('input[name="payMethod"]').forEach(function (radio) {
	radio.addEventListener('change', function () {
		state.payMethod = radio.value;
		var labelMap = { kakao: '카카오페이', naver: '네이버페이', card: '카드', account: '계좌이체' };
		document.getElementById('paySummary').textContent = labelMap[radio.value];
		refreshPayBar();
	});
});

// ------- 하단 결제 바 -------
function refreshPayBar() {
	document.getElementById('payBarAmount').textContent = DEPOSIT_PRICE.toLocaleString();
	var flightOk = true;
	if (state.plan === '1') {
		flightOk = document.getElementById('flightNoInput').value.trim() !== ''
			&& document.getElementById('flightRoundtripInput').value === 'round';
	}
	var ready = state.plan && state.timeChosen && state.selectedSeat && state.payMethod && flightOk;
	document.getElementById('payBtn').disabled = !ready;
}

document.getElementById('flightRoundtripInput').addEventListener('change', function () {
	if (this.value === 'oneway') {
		alert('편도 항공권은 이 시스템을 이용하실 수 없습니다 (현장 이용을 안내해드립니다).');
	}
	refreshPayBar();
});
document.getElementById('flightNoInput').addEventListener('input', refreshPayBar);

document.getElementById('payBtn').addEventListener('click', function () {
	alert(
		'예약이 완료되었습니다.\n\n' +
		'이용방식: ' + (state.plan === '1' ? '1안 (예약형)' : '2안 (자유출차형)') + '\n' +
		'좌석: ' + document.getElementById('seatSummary').textContent + '\n' +
		'예약금: ' + DEPOSIT_PRICE.toLocaleString() + '원 결제\n\n' +
		'(실제 결제/서버 저장 및 항공편 결항 감지 API 연동은 다음 단계에서 연결됩니다)'
	);
});

// ------- 초기화 -------
document.getElementById('durationInput').addEventListener('change', updateEstimatedPrice);
document.getElementById('dateInput').addEventListener('change', updateEstimatedPrice);
document.getElementById('startTimeInput').addEventListener('change', updateEstimatedPrice);

initTimeInputs();
renderSeats();
refreshPayBar();

// URL 파라미터로 바로예약(랜덤) 진입한 경우, 좌석 아코디언 열고 자동 랜덤 배정
if (urlParams.get('mode') === 'random') {
	document.getElementById('stepSeat').setAttribute('open', '');
	randomAssign();
}
