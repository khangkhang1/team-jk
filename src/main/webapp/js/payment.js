// ============================================================
// 결제 모듈 (담당: 오윤섭 예정)
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

	// 출차 시각 초기값 설정
	var endFromUrl = params.get('end');
	if (endFromUrl) {
		endSelect.value = endFromUrl;
	} else {
		updateDefaultEndDateTime();
	}

	// 이벤트 바인딩
	startSelect.addEventListener('change', function () {
		updateDefaultEndDateTime();
		if (typeof updatePaymentPrice === 'function') {
			updatePaymentPrice();
		}
	});

	endSelect.addEventListener('change', function () {
		if (typeof updatePaymentPrice === 'function') {
			updatePaymentPrice();
		}
	});
}

// 출차 시각 설정
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

// ------- 예상 금액 계산 -------
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
		if (startDate && startTime && endDate && endTime) {
			var startDateTime = new Date(startDate + 'T' + startTime);
			var endDateTime = new Date(endDate + 'T' + endTime);

			if (endDateTime <= startDateTime) {
				document.getElementById('estimatedPrice').textContent = '날짜/시각 확인 필요';
				document.getElementById('estimatedPriceInput').value = 0;
				payState.timeChosen = false;
				refreshPaymentFooter();
				return;
			}

			var diffMs = endDateTime - startDateTime;
			var durationHours = diffMs / (1000 * 60 * 60);
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

// ------- 하단 결제 바 갱신 및 결제 버튼 활성화 제어 -------
function refreshPaymentFooter() {
    var planEl = document.getElementById("reservationPlan");
    var plan = planEl ? planEl.value : payState.plan;
    var payBarPrice = document.getElementById("payBarPrice");
    var estimatedPriceInput = document.getElementById("estimatedPriceInput");
    var depositAmountInput = document.getElementById("depositAmount");
    var payBtn = document.getElementById("payBtn");

    var currentPrice = 0;

    if (plan === "1") {
        // [1안: 예약형] - 최종 예상 결제 금액 표시
        currentPrice = parseInt(estimatedPriceInput ? estimatedPriceInput.value : "0", 10) || 0;
        
        if (payBarPrice) {
            payBarPrice.innerHTML = '<span>최종 결제 금액</span> <strong id="payBarAmount">' + currentPrice.toLocaleString() + '</strong>원';
        }
        if (depositAmountInput) {
            depositAmountInput.value = currentPrice;
        }
    } else {
        // [2안: 자유출차형] - 예약금 5,000원 표시
        currentPrice = 5000;
        
        if (payBarPrice) {
            payBarPrice.innerHTML = '<span>예약금</span> <strong id="payBarAmount">' + currentPrice.toLocaleString() + '</strong>원';
        }
        if (depositAmountInput) {
            depositAmountInput.value = currentPrice;
        }
    }

    // ★ [핵심] 결제 버튼 활성화 조건 체크
    var payMethodSelected = document.querySelector('input[name="t_reservation_pay_method"]:checked');
    
    if (payBtn) {
        if (plan === "1") {
            // 1안: 결제 수단이 선택되었고, 결제 금액이 0보다 큰 경우 활성화
            payBtn.disabled = !(payMethodSelected && currentPrice > 0);
        } else {
            // 2안: 결제 수단만 선택되었으면 바로 활성화
            payBtn.disabled = !payMethodSelected;
        }
    }
}

// ------- 모달 상태 초기화 -------
function resetPaymentState(plan) {
	payState.plan = plan;
	payState.payMethod = null;

	// DOM Hidden input에도 plan 값 저장 동기화
	var resPlanInput = document.getElementById('reservationPlan');
	if (resPlanInput) {
		resPlanInput.value = plan;
	}

	document.querySelectorAll('input[name="t_reservation_pay_method"]').forEach(function (r) { r.checked = false; });

	var isPlan1 = plan === '1';
	document.querySelectorAll('.plan1Only').forEach(function (el) { el.classList.toggle('hidden', !isPlan1); });
	document.querySelectorAll('.plan2Only').forEach(function (el) { el.classList.toggle('hidden', isPlan1); });
	
	var flightNoEl = document.getElementById('flightNoInput');
	if (flightNoEl) flightNoEl.value = '';
	
	var flightRoundEl = document.getElementById('flightRoundtripInput');
	if (flightRoundEl) flightRoundEl.value = 'round';

	initPaymentTimeInputs();
	updatePaymentPrice();
}

// ------- 외부 진입점 -------
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

// 오버레이 클릭 시 닫기
paymentModalEl.addEventListener('click', function (e) {
	if (e.target === paymentModalEl) closePaymentModal();
});

// 입차 날짜/시각 변경 시
['startDateInput', 'startTimeInput'].forEach(function (id) {
	var el = document.getElementById(id);
	if (el) {
		el.addEventListener('change', function () {
			updateDefaultEndDateTime();
			updatePaymentPrice();
		});
	}
});

// 출차 날짜/시각 변경 시
['endDateInput', 'endTimeInput'].forEach(function (id) {
	var el = document.getElementById(id);
	if (el) {
		el.addEventListener('change', updatePaymentPrice);
	}
});

var flightRoundtripEl = document.getElementById('flightRoundtripInput');
if (flightRoundtripEl) {
	flightRoundtripEl.addEventListener('change', function () {
		if (this.value === 'oneway') {
			alert('편도 항공권은 이 시스템을 이용하실 수 없습니다 (현장 이용을 안내해드립니다).');
		}
		refreshPaymentFooter();
	});
}

var flightNoEl = document.getElementById('flightNoInput');
if (flightNoEl) {
	flightNoEl.addEventListener('input', refreshPaymentFooter);
}

// ------- 결제 수단 선택 시 즉시 결제바 및 버튼 활성화 갱신 -------
document.querySelectorAll('input[name="t_reservation_pay_method"]').forEach(function (radio) {
	radio.addEventListener('change', function () {
		payState.payMethod = radio.value;
		refreshPaymentFooter();
	});
});

// 도착 예정 시간 입력 시
var flightArriveInputEl = document.getElementById('flightArriveInput');
if (flightArriveInputEl) {
	flightArriveInputEl.addEventListener('input', refreshPaymentFooter);
	flightArriveInputEl.addEventListener('change', refreshPaymentFooter);
}