<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>인천공항 주차예약 - 결제</title>

<!-- 1. jQuery 및 포트원 v1 SDK 로드 -->
<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
<script src="https://cdn.iamport.kr/v1/iamport.js"></script>

<!-- 헤더/푸터용 공통 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/index1.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/c.css">

<style>
  html, body {
    width: 100% !important;
    height: auto !important;
    min-height: 100% !important;
    margin: 0 !important;
    padding: 0 !important;
    background-color: #f8f9fa !important;
    overflow-x: hidden !important;
    overflow-y: auto !important; /* 브라우저 기본 스크롤 활성화 */
  }

  .wrap {
    width: 100% !important;
    height: auto !important;
    min-height: 100vh !important;
    display: flex !important;
    flex-direction: column !important;
    justify-content: space-between !important;
    box-sizing: border-box !important;
    position: relative !important;
  }

  /* 메인 영역: 위아래 여백 확보 */
  .pay_page_container {
    flex: 1 0 auto !important;
    width: 35% !important;
    min-width: 420px !important;
    margin: 0 auto !important;
    padding-top: 100px !important;  /* 헤더 아래 공간 */
    padding-bottom: 60px !important; /* 푸터 위 공간 */
    box-sizing: border-box !important;
  }

  form[name="pay"] {
    width: 100% !important;
    height: auto !important;
  }

  /* 카드 영역 */
  .pay_card {
    background: #ffffff !important;
    border: 1px solid #e1e4e8 !important;
    border-radius: 12px !important;
    padding: 28px !important;
    box-shadow: 0 4px 16px rgba(0,0,0,0.06) !important;
    width: 100% !important;
    height: auto !important;
    overflow: visible !important;
    box-sizing: border-box !important;
  }

  /* 푸터 영역 */
  .footer {
    flex-shrink: 0 !important;
    position: relative !important;
    bottom: auto !important;
    width: 100% !important;
    padding: 30px 0 !important;
    background-color: #2b2e33 !important;
    z-index: 10 !important;
  }

  /* 폼 타이틀 및 안내 */
  .pay_title {
    font-size: 20px !important;
    font-weight: 700 !important;
    color: #222 !important;
    margin-bottom: 4px !important;
  }

  .pay_sub_info {
    font-size: 13px !important;
    color: #666 !important;
    margin-bottom: 16px !important;
    padding-bottom: 12px !important;
    border-bottom: 2px solid #222 !important;
  }

  /* 폼 레이아웃 정렬 */
  .formRow {
    width: 100% !important;
    margin-bottom: 14px !important;
    display: flex !important;
    flex-direction: column !important;
  }
  
  .formRow label {
    font-size: 13px !important;
    font-weight: 600 !important;
    color: #333 !important;
    margin-bottom: 4px !important;
  }

  .formRow input, .formRow select {
    width: 100% !important;
    height: 40px !important;
    padding: 0 10px !important;
    border: 1px solid #ccc !important;
    border-radius: 6px !important;
    font-size: 13px !important;
    box-sizing: border-box !important;
    background-color: #fff !important;
  }

  /* 항공권 필드셋 */
  #flightFieldset {
    width: 100% !important;
    box-sizing: border-box !important;
    border: 1px solid #e1e4e8 !important;
    background-color: #fcfcfc !important;
    border-radius: 8px !important;
    padding: 14px !important;
    margin: 16px 0 !important;
  }

  #flightFieldset legend {
    padding: 0 6px !important;
    font-weight: bold !important;
    font-size: 13px !important;
    color: #0056b3 !important;
  }

  /* 결제 수단 영역 (세로 정렬) */
  .pay_method_title {
    font-size: 13px !important;
    font-weight: 600 !important;
    margin-top: 16px !important;
    margin-bottom: 8px !important;
    display: block !important;
  }

  #payMethodArea {
    display: flex !important;
    flex-direction: column !important; /* 세로 정렬 변경 */
    gap: 8px !important;
    margin-bottom: 16px !important;
    width: 100% !important;
  }

  .payOption {
    display: flex !important;
    align-items: center !important;
    padding: 12px 14px !important;
    border: 1px solid #dcdfe6 !important;
    border-radius: 6px !important;
    cursor: pointer !important;
    font-size: 14px !important;
    font-weight: 500 !important;
    background: #fff !important;
    transition: all 0.2s ease;
  }

  .payOption:hover {
    border-color: #0056b3 !important;
    background-color: #f0f7ff !important;
  }

  .payOption input[type="radio"] {
    margin-right: 10px !important;
    width: 16px !important;
    height: 16px !important;
    cursor: pointer !important;
  }

  /* 결제 하단 및 버튼 */
  #paymentFooter {
    margin-top: 16px !important;
    padding-top: 14px !important;
    border-top: 1px solid #e1e4e8 !important;
  }

  #payBtn {
    width: 100% !important;
    height: 46px !important;
    background-color: #0056b3 !important;
    color: #fff !important;
    font-size: 16px !important;
    font-weight: bold !important;
    border: none !important;
    border-radius: 6px !important;
    cursor: pointer !important;
    margin-top: 10px !important;
  }

  #payBtn:hover {
    background-color: #004085 !important;
  }
</style>

<script>
// 1. 식별코드 초기화
$(document).ready(function() {
    var IMP = window.IMP;
    IMP.init("imp43028000");
});

// 2. 결제 버튼 클릭 시 실행
function goPayment() {
    var method = document.pay.t_reservation_pay_method.value;

    if (!method) {
        alert("결제 수단을 선택해 주세요.");
        return;
    }

    if (confirm("예약 및 결제를 진행하시겠습니까?")) {
        payment(method);
    }
}

// 3. 포트원 결제 요청 (수단별 분기)
function payment(method) {
    var IMP = window.IMP;

    var plan = document.getElementById("reservationPlan").value;
    var plan1_price = document.getElementById("estimatedPriceInput").value;
    var price = plan === '1' ? plan1_price : 5000;

    var redirectUrl = window.location.origin + "${pageContext.request.contextPath}/Reservation";

    var payData = {
        name: "인천공항 주차장 예약",
        amount: price,
        buyer_name: "홍길동",
        buyer_email: "test@example.com",
        m_redirect_url: redirectUrl
    };

    if (method === "kakaoPay") {
        payData.pg = "kakaopay";
        payData.pay_method = "card";
        payData.merchant_uid = "ORD_KAKAO_" + new Date().getTime();
    } else if (method === "naverPay") {
        payData.pg = "naverpay";
        payData.pay_method = "card";
        payData.merchant_uid = "ORD_NAVER_" + new Date().getTime();
    } else if (method === "creditCard") {
        payData.pg = "html5_inicis.INIpayTest";
        payData.pay_method = "card";
        payData.merchant_uid = "ORD_CARD_" + new Date().getTime();
    } else if (method === "bankTransfer") {
        payData.pg = "html5_inicis.INIpayTest";
        payData.pay_method = "trans"; // 실시간 계좌이체
        payData.merchant_uid = "ORD_BANK_" + new Date().getTime();
    } else {
        alert("선택하신 결제 수단은 현재 미지원입니다.");
        return;
    }

    IMP.request_pay(payData, handleResponse);
}

// 4. 콜백 처리
function handleResponse(rsp) {
    if (rsp.success) {
        document.getElementById("impUidInput").value = rsp.imp_uid;
        document.getElementById("merchantUidInput").value = rsp.merchant_uid;

        var form = document.pay;
        form.method = "post";
        form.action = "Reservation";
        form.submit();
    } else {
        alert("결제에 실패했거나 취소되었습니다.\n사유: " + rsp.error_msg);
    }
}
</script>
</head>

<body>
<div class="wrap">

	<!-- 헤더 -->
	<header class="header scrolled">
		<div class="header_inner">
			<a href="${pageContext.request.contextPath}/index.jsp" class="logo">
				인천공항 주차예약
				<small>INCHEON AIRPORT PARKING</small>
			</a>
			<div class="header_right">
				<a href="${pageContext.request.contextPath}/member/member_login.jsp">로그인</a>
				<span>|</span>
				<a href="${pageContext.request.contextPath}/member/member_join.jsp">회원가입</a>
			</div>
		</div>
	</header>

	<!-- 메인 영역 -->
	<main class="pay_page_container">
		<form name="pay">
			<input type="hidden" name="t_gubun" value="payment">
			
			<div class="pay_card">
				<h1 id="paymentSeatTitle" class="pay_title">P1 구역 - 선택 좌석</h1>
				<p id="paymentLotInfo" class="pay_sub_info">단기주차장 · 시간당 3,000원</p>

				<!-- 예약 데이터 히든 파라미터 -->
				<input type="hidden" id="reservationPlan" name="t_reservation_plan" value="1">
				<input type="hidden" id="reservationSeat" name="t_reservation_seat" value="P1-01">

				<div class="formRow">
					<label>주차 날짜</label>
					<input type="date" id="startDateInput" name="t_reservation_start_date">
				</div>
				<div class="formRow">
					<label>주차 시각</label>
					<select id="startTimeInput" name="t_reservation_start_time"></select>
				</div>
				<div class="formRow">
					<label>예상 출차 날짜</label>
					<input type="date" id="endDateInput" name="t_reservation_end_date">
				</div>
				<div class="formRow" id="durationRow">
					<label>이용 시간</label>
					<select id="endTimeInput" name="t_reservation_end_time"></select>
				</div>

				<fieldset id="flightFieldset">
					<legend>✈️ 항공권 정보 (필수)</legend>
					<div class="formRow">
						<label>항공편명</label>
						<input type="text" id="flightNoInput" name="t_reservation_flight_no" placeholder="항공편명 입력">
					</div>
					<div class="formRow">
						<label>왕복 여부</label>
						<select id="flightRoundtripInput">
							<option value="round">왕복</option>
							<option value="oneway">편도 (이용 불가)</option>
						</select>
					</div>
					<div class="formRow" style="margin-bottom: 0 !important;">
						<label>귀국 도착 예정</label>
						<input type="time" id="flightArriveInput">
					</div>
				</fieldset>

				<div id="estimatedPriceBox" style="font-size: 15px; text-align: right; margin-bottom: 12px;">
					<span>예상 금액</span>: <strong id="estimatedPrice" style="color: #0056b3; font-size: 19px;">-</strong>
				</div>
				<input type="hidden" name="t_reservation_estimate_amount" id="estimatedPriceInput" value="12000">

				<!-- 결제 수단 선택 (세로형 추가 및 변경) -->
				<span class="pay_method_title">결제 수단 선택</span>
				<div id="payMethodArea">
					<label class="payOption">
						<input type="radio" name="t_reservation_pay_method" value="kakaoPay"> 카카오페이
					</label>
					<label class="payOption">
						<input type="radio" name="t_reservation_pay_method" value="naverPay"> 네이버페이
					</label>
					<label class="payOption">
						<input type="radio" name="t_reservation_pay_method" value="creditCard"> 신용카드
					</label>
					<label class="payOption">
						<input type="radio" name="t_reservation_pay_method" value="bankTransfer"> 계좌이체
					</label>
				</div>

				<div id="paymentFooter">
					<div id="payBarPrice" style="font-size: 15px; text-align: right;">
						<span>결제액:</span> <strong id="payBarAmount" style="font-size: 20px; color: #e53935;">????</strong> 원
					</div>
					
					<input type="hidden" id="depositAmount" name="t_reservation_deposit_amount" value="5000">
					<input type="hidden" name="t_imp_uid" id="impUidInput">
					<input type="hidden" name="t_merchant_uid" id="merchantUidInput">
					
					<button id="payBtn" type="button" onclick="goPayment()">결제하기</button>
				</div>
			</div>
		</form>
	</main>

	<!-- 푸터 -->
	<footer class="footer">
		<div class="footer_inner">
			<div class="footer_info">
				<p>제1여객터미널 주차예약 서비스</p>
				<p class="copyright">Copyright © Parking Reservation Project. All rights reserved.</p>
			</div>
		</div>
	</footer>

</div>

<script src="${pageContext.request.contextPath}/js/payment.js"></script>
</body>
</html>