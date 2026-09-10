// ============================================================
// 결제 모듈 (담당: 오윤섭 예정) - 독립적으로 개발/교체 가능하게 분리한 파일.
// reservation.js와의 접점은 딱 하나, window.openPaymentModal(seatLabelText, plan) 뿐.
// 이용방식(1안/2안)은 주차맵 페이지의 필터에서 미리 정하고 넘어오므로, 이 모달은
// 그 값을 그대로 받아서 시간/항공권/결제수단만 처리한다 (이용방식을 다시 고르지 않음).
// 화면 초안 단계 - 전부 가상 데이터/클라이언트 로직만 존재. 서버 연동은 이후 단계.
// ============================================================

var HOURLY_PRICE = 3000;       // 1안 시간당 요금 (원)
var PLAN2_HOURLY_PRICE = 4500; // 2안 페널티 요금 (원, 더 비쌈)
var DEPOSIT_PRICE = 5000;      // 예약금(고정) - 실제 총 이용료는 출차 시 정산

// 메인 화면 PARKING_LOTS 최소 정보(이름/요금) - id로 매칭. 실제로는 서버에서 조회.
var PAY_LOT_INFO = {
	1: { name: "단기주차장 1구역", addr: "인천공항 1터미널 · 단기", price: HOURLY_PRICE },
	2: { name: "단기주차장 2구역", addr: "인천공항 1터미널 · 단기", price: HOURLY_PRICE },
	3: { name: "장기주차장 P1",   addr: "인천공항 1터미널 · 장기", price: PLAN2_HOURLY_PRICE },
	4: { name: "장기주차장 P2",   addr: "인천공항 1터미널 · 장기", price: PLAN2_HOURLY_PRICE }
};

var payState = {
	plan: null,
	timeChosen: false,
	payMethod: null,
	seatLabel: null
};

var paymentModalEl = document.getElementById('paymentModal');

function getPayUrlParams() {
	return new URLSearchParams(window.location.search);
}

function getCurrentLotInfo() {
	var lotId = getPayUrlParams().get('lot') || 1;
	return PAY_LOT_INFO[lotId] || PAY_LOT_INFO[1];
}

// ------- 시간 선택 초기화 -------
function initPaymentTimeInputs() {
	var startSelect = document.getElementById('startTimeInput');
	var endSelect = document.getElementById('endTimeInput');

	// 30분 단위 시간 목록 생성 함수
	function createTimeOptions() {
		var options = [];
		for (var h = 0; h < 24; h++) {
			['00', '30'].forEach(function (m) {
				var label = String(h).padStart(2, '0') + ':' + m;
				options.push(label);
			});
		}
		return options;
	}

	var timeOptions = createTimeOptions();

	// startTimeInput 옵션 생성
	if (startSelect.options.length === 0) {
		timeOptions.forEach(function (time) {
			var opt = document.createElement('option');
			opt.value = time;
			opt.textContent = time;
			startSelect.appendChild(opt);
		});
	}

	// endTimeInput 옵션 생성
	if (endSelect.options.length === 0) {
		timeOptions.forEach(function (time) {
			var opt = document.createElement('option');
			opt.value = time;
			opt.textContent = time;
			endSelect.appendChild(opt);
		});
	}

	// 날짜 설정
	var today = new Date();
	var yyyy = today.getFullYear();
	var mm = String(today.getMonth() + 1).padStart(2, '0');
	var dd = String(today.getDate()).padStart(2, '0');
	var todayStr = yyyy + '-' + mm + '-' + dd;
	var params = getPayUrlParams();

	// 입차 날짜 설정 (startDateInput 사용)
	document.getElementById('startDateInput').value = params.get('date') || todayStr;
	
	// 시작 시각 설정
	var startFromUrl = params.get('start');
	if (startFromUrl) {
		startSelect.value = startFromUrl;
	} else {
		var roundedHour = today.getHours();
		var roundedMin = today.getMinutes() < 30 ? '30' : '00';
		if (roundedMin === '00') roundedHour = (roundedHour + 1) % 24;
		startSelect.value = String(roundedHour).padStart(2, '0') + ':' + roundedMin;
	}

	// 출차 시각 초기값 설정 (URL 파라미터가 있으면 적용, 없으면 시작 시각 + 1시간)
	var endFromUrl = params.get('end');
	if (endFromUrl) {
		endSelect.value = endFromUrl;
	} else {
		updateDefaultEndDateTime();
	}

	// 시작 시각 변경 시 출차 시각도 자동으로 1시간 뒤로 조정되도록 이벤트 바인딩
	startSelect.addEventListener('change', function () {
		updateDefaultEndDateTime();
		if (typeof updatePaymentPrice === 'function') {
			updatePaymentPrice();
		}
	});

	// 출차 시각 변경 시에도 금액 재계산
	endSelect.addEventListener('change', function () {
		if (typeof updatePaymentPrice === 'function') {
			updatePaymentPrice();
		}
	});
}

//출차 시각 설정
function updateDefaultEndDateTime() {
	var startDateVal = document.getElementById('startDateInput').value;
	var startTimeVal = document.getElementById('startTimeInput').value;

	if (!startDateVal || !startTimeVal) return;

	var startDt = new Date(startDateVal + 'T' + startTimeVal);
	var endDt = new Date(startDt.getTime() + 60 * 60 * 1000); // 1시간 추가

	var endYyyy = endDt.getFullYear();
	var endMm = String(endDt.getMonth() + 1).padStart(2, '0');
	var endDd = String(endDt.getDate()).padStart(2, '0');

	var endHh = String(endDt.getHours()).padStart(2, '0');
	var endMi = String(endDt.getMinutes()).padStart(2, '0');

	document.getElementById('endDateInput').value = endYyyy + '-' + endMm + '-' + endDd;
	document.getElementById('endTimeInput').value = endHh + ':' + endMi;
}

// ------- 예상 금액 -------
function updatePaymentPrice() {
	var startDate = document.getElementById('startDateInput').value;
	var startTime = document.getElementById('startTimeInput').value;
	var endDate = document.getElementById('endDateInput').value;
	var endTime = document.getElementById('endTimeInput').value;
	var info = getCurrentLotInfo();

	if (payState.plan === '2') {
		document.getElementById('estimatedPrice').textContent =
			'출차 시 정산 (시간당 ' + PLAN2_HOURLY_PRICE.toLocaleString() + '원, 페널티 요금)';
	} else if (payState.plan === '1') {
		// 모든 날짜 및 시간 정보가 갖춰진 경우
		if (startDate && startTime && endDate && endTime) {
			// "YYYY-MM-DD" + "THH:mm" 포맷으로 Date 객체 생성
			var startDateTime = new Date(startDate + 'T' + startTime);
			var endDateTime = new Date(endDate + 'T' + endTime);

			// 출차 일시가 입차 일시보다 빠른 경우 예외 처리
			if (endDateTime <= startDateTime) {
				document.getElementById('estimatedPrice').textContent = '날짜/시각 확인 필요';
				document.getElementById('estimatedPriceInput').value = 0;
				payState.timeChosen = false;
				refreshPaymentFooter();
				return;
			}

			// Milliseconds 차이를 시간 단위(소수점)로 변환
			var diffMs = endDateTime - startDateTime;
			var durationHours = diffMs / (1000 * 60 * 60);

			// 총 금액 계산 (시간 * 시간당 금액)
			var totalPrice = Math.round(durationHours * info.price);

			document.getElementById('estimatedPrice').textContent = totalPrice.toLocaleString() + '원';
			document.getElementById('estimatedPriceInput').value = totalPrice;
		} else {
			document.getElementById('estimatedPrice').textContent = '-';
			document.getElementById('estimatedPriceInput').value = '';
		}
	} else {
		document.getElementById('estimatedPrice').textContent = '-';
	}

	payState.timeChosen = !!(startDate && startTime && endDate && endTime);
	refreshPaymentFooter();
}

// ------- 하단 결제 바 -------
function refreshPaymentFooter() {
	document.getElementById('payBarAmount').textContent = DEPOSIT_PRICE.toLocaleString();

	var flightOk = true;
	if (payState.plan === '1') {
		flightOk = document.getElementById('flightNoInput').value.trim() !== ''
			&& document.getElementById('flightRoundtripInput').value === 'round';
	}
	var ready = payState.plan && payState.timeChosen && payState.payMethod && flightOk;
	document.getElementById('payBtn').disabled = !ready;
}

// ------- 모달 상태 초기화 (이용방식은 주차맵 필터에서 이미 정해져서 들어옴 - 여기서 안 건드림) -------
function resetPaymentState(plan) {
	payState.plan = plan;
	payState.payMethod = null;

	document.querySelectorAll('input[name="t_reservation_pay_method"]').forEach(function (r) { r.checked = false; });

	var isPlan1 = plan === '1';
	document.querySelectorAll('.plan1Only').forEach(function (el) { el.classList.toggle('hidden', !isPlan1); });
	document.querySelectorAll('.plan2Only').forEach(function (el) { el.classList.toggle('hidden', isPlan1); });
	document.getElementById('flightNoInput').value = '';
	document.getElementById('flightRoundtripInput').value = 'round';

	initPaymentTimeInputs();
	updatePaymentPrice();
}
// ------- 외부(reservation.js)에서 호출하는 진입점 -------
// plan: 주차맵 페이지의 "이용 방식" 필터에서 이미 선택된 값('1' 또는 '2')을 그대로 넘겨받음.
window.openPaymentModal = function (seatLabelText, plan) {
	payState.seatLabel = seatLabelText;
	var info = getCurrentLotInfo();

	document.getElementById('paymentSeatTitle').textContent = seatLabelText;
	document.getElementById('paymentLotInfo').textContent = info.name + ' · 시간당 ' + info.price.toLocaleString() + '원';
	document.getElementById('paymentPlanInfo').textContent =
		'이용방식: ' + (plan === '1' ? '예약형 (1안)' : '자유출차형 (2안)');

	resetPaymentState(plan);
	paymentModalEl.classList.remove('hidden');
};

function closePaymentModal() {
	paymentModalEl.classList.add('hidden');
}

document.getElementById('paymentCloseBtn').addEventListener('click', closePaymentModal);

// 오버레이(모달 바깥) 클릭 시 닫기 - Index 화면 상세팝업과 동일한 UX
paymentModalEl.addEventListener('click', function (e) {
	if (e.target === paymentModalEl) closePaymentModal();
});

// 입차 날짜/시각 변경 시: 출차 일시 +1시간 자동 세팅 및 금액 계산
['startDateInput', 'startTimeInput'].forEach(function (id) {
	var el = document.getElementById(id);
	if (el) {
		el.addEventListener('change', function () {
			updateDefaultEndDateTime();
			updatePaymentPrice();
		});
	}
});

// 출차 날짜/시각 변경 시: 금액 계산
['endDateInput', 'endTimeInput'].forEach(function (id) {
	var el = document.getElementById(id);
	if (el) {
		el.addEventListener('change', updatePaymentPrice);
	}
});

document.getElementById('flightRoundtripInput').addEventListener('change', function () {
	if (this.value === 'oneway') {
		alert('편도 항공권은 이 시스템을 이용하실 수 없습니다 (현장 이용을 안내해드립니다).');
	}
	refreshPaymentFooter();
});
document.getElementById('flightNoInput').addEventListener('input', refreshPaymentFooter);

// ------- 결제 수단 -------
document.querySelectorAll('input[name="t_reservation_pay_method"]').forEach(function (radio) {
	radio.addEventListener('change', function () {
		payState.payMethod = radio.value;
		refreshPaymentFooter();
	});
});

// ------- 결제하기 -------
document.getElementById('payBtn').addEventListener('click', function () {
	alert(
		'예약이 완료되었습니다.\n\n' +
		'좌석: ' + payState.seatLabel + '\n' +
		'이용방식: ' + (payState.plan === '1' ? '1안 (예약형)' : '2안 (자유출차형)') + '\n' +
		'예약금: ' + DEPOSIT_PRICE.toLocaleString() + '원 결제\n\n' +
		'(실제 결제/서버 저장 및 항공편 결항 감지 API 연동은 다음 단계에서 연결됩니다)'
	);
	closePaymentModal();
});