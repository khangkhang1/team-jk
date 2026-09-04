// 주차맵 페이지 - 화면 초안, 전부 가상 데이터/클라이언트 로직만 존재. 서버 연동은 이후 단계.
// 결제 관련 로직은 js/payment.js로 분리돼 있음. 이 파일은 좌석 클릭 시
// window.openPaymentModal(seatLabelText) 하나만 호출한다 - 그 외 결제 내부 사정은 몰라도 됨.

// 메인 화면 PARKING_LOTS 최소 정보(이름/요금) - id로 매칭. 실제로는 서버에서 조회.
var LOT_INFO = {
	1: { name: "단기주차장 1구역", addr: "인천공항 1터미널 · 단기", price: 3000 },
	2: { name: "단기주차장 2구역", addr: "인천공항 1터미널 · 단기", price: 3000 },
	3: { name: "장기주차장 P1",   addr: "인천공항 1터미널 · 장기", price: 4500 },
	4: { name: "장기주차장 P2",   addr: "인천공항 1터미널 · 장기", price: 4500 }
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

var resState = {
	floor: 1,
	plan: '1' // 이용 방식 필터 - 기본값은 예약형(1안). 결제창을 열기 전에 여기서 미리 정해둔다.
};

// ------- 이용 방식 필터 (1안/2안) - 예전엔 결제창 안에 있었는데 좌석 고르기 전으로 옮김 -------
document.querySelectorAll('input[name="planType"]').forEach(function (radio) {
	radio.addEventListener('change', function () {
		resState.plan = radio.value;
		document.querySelectorAll('.planCard').forEach(function (card) {
			card.classList.toggle('selected', card.querySelector('input').checked);
		});
	});
});

// ------- URL 파라미터로 주차구역 정보 표시 (메인 화면 확인팝업 → 이 페이지) -------
var urlParams = new URLSearchParams(window.location.search);
(function applyLotFromUrl() {
	var lotId = urlParams.get('lot');
	var info = LOT_INFO[lotId] || LOT_INFO[1];
	document.getElementById('lotName').textContent = info.name;
	document.getElementById('lotAddr').innerHTML = info.addr + ' · 시간당 <span id="lotPrice">' + info.price.toLocaleString() + '</span>원';
})();

// ------- 좌석 선택 -------
function seatLabel(seat) {
	if (seat.type === 'gate') return '';
	if (seat.type === 'disabled') return '♿';
	if (seat.type === 'ev') return '🔌';
	if (seat.type === 'cancelled') return '✈️';
	return (seat.row + 1) + '-' + (seat.col + 1);
}

function seatDisplayText(seat) {
	return resState.floor + '층 ' + (seat.row + 1) + '-' + (seat.col + 1) + '번';
}

function renderSeats() {
	var grid = document.getElementById('seatGrid');
	grid.innerHTML = '';
	var seats = FLOOR_DATA[resState.floor];
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
			cell.textContent = seatLabel(seat);
			cell.addEventListener('click', function () {
				onSeatClick(seat);
			});
		}
		grid.appendChild(cell);
	});

	document.getElementById('floorRemainCount').textContent = remain;
}

// 좌석 클릭 - 결제창(모달)을 여는 게 유일한 역할. 시간/방식/결제는 payment.js가 처리.
function onSeatClick(seat) {
	if (seat.type === 'cancelled') {
		alert('항공편 결항으로 재배정 처리 중인 자리입니다. 잠시 후 다시 확인해주세요.');
		return;
	}
	if (seat.taken) {
		alert('이미 예약된 자리입니다.');
		return;
	}
	window.openPaymentModal(seatDisplayText(seat), resState.plan);
}

function randomAssign() {
	var seats = FLOOR_DATA[resState.floor].filter(function (s) {
		return s.type !== 'gate' && s.type !== 'cancelled' && !s.taken;
	});
	if (seats.length === 0) {
		alert('이 층에는 빈 자리가 없습니다. 다른 층을 선택해주세요.');
		return;
	}
	var picked = seats[Math.floor(Math.random() * seats.length)];
	window.openPaymentModal('랜덤 배정 · ' + seatDisplayText(picked), resState.plan);
}

document.querySelectorAll('.floorTab').forEach(function (btn) {
	btn.addEventListener('click', function () {
		document.querySelectorAll('.floorTab').forEach(function (b) { b.classList.remove('active'); });
		btn.classList.add('active');
		resState.floor = parseInt(btn.dataset.floor, 10);
		renderSeats();
	});
});
document.getElementById('randomAssignBtn').addEventListener('click', randomAssign);

// ------- 초기화 -------
renderSeats();
