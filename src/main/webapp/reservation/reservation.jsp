<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>주차맵 - 인천공항 주차관리시스템</title>
<link rel="stylesheet" href="css/c.css">
<link rel="stylesheet" href="css/reservation.css">
<link rel="stylesheet" href="css/payment.css">
</head>
<body class="resPage">

	<!-- 상단바 -->
	<header id="topSearch">
		<a href="Index.html" id="backBtn" title="메인으로" data-i18n="common_back">← 메인으로</a>
		<div id="logo" data-i18n="res_pageTitle">주차맵</div>
		<nav id="topNav">
			<button id="langToggleBtn" class="langToggle">🇯🇵 日本語</button>
		</nav>
	</header>

	<div id="resWrap">

		<!-- 주차장 요약 정보 -->
		<div id="lotSummary">
			<div id="lotSummaryPhoto">사진</div>
			<div id="lotSummaryText">
				<h2 id="lotName">단기주차장 1구역</h2>
				<p id="lotAddr">인천공항 1터미널 · 시간당 <span id="lotPrice">3,000</span>원</p>
			</div>
		</div>
<form name="rec">
		<!-- 이용 방식 필터 - 예전엔 결제창 안에 있었는데, 좌석 고르기 전에 먼저 정하는 게 자연스러워서 여기로 옮김 -->
		<div id="planFilter">
			<div class="filterTitle" data-i18n="res_planFilterTitle">이용 방식</div>
			<div id="planCards">
				<label class="planCard selected">
					<input type="radio" name="planType" value="1" checked>
					<div class="planCardTitle" data-i18n="res_plan1Name">예약형 (1안)</div>
					<div class="planCardDesc" data-i18n="res_plan1Desc">시작·종료 시각을 미리 정합니다. 왕복 항공권 정보 입력 필수. 기본 요금.</div>
				</label>
				<label class="planCard">
					<input type="radio" name="planType" value="2">
					<div class="planCardTitle" data-i18n="res_plan2Name">자유출차형 (2안)</div>
					<div class="planCardDesc" data-i18n="res_plan2Desc">시작 시각만 정하고 종료는 자유입니다. 장기주차구역, 페널티 요금(더 비쌈) 적용.</div>
				</label>
			</div>
		</div>

		<!-- 좌석 선택 - 여기서 자리를 클릭하면 결제창(모달)이 뜬다. 시간/결제는 그 모달 안에서 처리 -->
		<div id="seatToolbar">
			<div id="floorTabs">
				<button class="floorTab active" data-floor="1">1층</button>
				<button class="floorTab" data-floor="2">2층</button>
			</div>
			<div id="floorRemain"><span data-i18n="res_floorRemain">이 층 잔여</span>: <strong id="floorRemainCount">-</strong>석</div>
			<button id="randomAssignBtn" data-i18n="res_random">🎲 랜덤 배정</button>
		</div>

		<div id="seatLegend">
			<span><i class="legendBox legendFree"></i> <span data-i18n="res_legendFree">선택 가능</span></span>
			<span><i class="legendBox legendSelected"></i> <span data-i18n="res_legendSelected">선택됨</span></span>
			<span><i class="legendBox legendTaken"></i> <span data-i18n="res_legendTaken">예약됨</span></span>
			<span><i class="legendBox legendDisabled"></i> <span data-i18n="res_legendDisabled">♿ 장애인</span></span>
			<span><i class="legendBox legendEv"></i> <span data-i18n="res_legendEv">🔌 전기차</span></span>
			<span><i class="legendBox legendCancelled"></i> <span data-i18n="res_legendCancelled">✈️ 결항 재배정중</span></span>
		</div>

		<div id="seatGrid"></div>
</form>
	</div>

	<!-- ============================================================ -->
	<!-- 결제 모듈 (담당: 오윤섭 예정) — 이 모달 블록은 여기서부터 END 주석까지 독립적으로 개발하고, -->
	<!-- 완성되면 이 자리에 통째로 교체/병합하면 됨. css/payment.css, js/payment.js도 같이 분리돼 있음. -->
	<!-- ============================================================ -->
	<div id="paymentModal" class="hidden">
		<div id="paymentModalInner">
			<button id="paymentCloseBtn">&times;</button>

			<h3 id="paymentSeatTitle">-</h3>
			<p id="paymentLotInfo">-</p>
			<p id="paymentPlanInfo">-</p>

			<div class="formRow">
				<label data-i18n="res_dateLabel">날짜</label>
				<input type="date" id="dateInput">
			</div>
			<div class="formRow">
				<label data-i18n="res_startTimeLabel">시작 시각</label>
				<select id="startTimeInput"></select>
			</div>

			<div class="formRow plan1Only hidden" id="durationRow">
				<label data-i18n="res_durationLabel">이용 시간</label>
				<select id="durationInput">
					<option value="1">1시간</option>
					<option value="2" selected>2시간</option>
					<option value="3">3시간</option>
					<option value="4">4시간</option>
					<option value="6">6시간</option>
				</select>
			</div>

			<div class="plan2Only hidden" id="endFreeNotice">
				<p data-i18n="res_endFreeNotice">종료 시각은 정하지 않습니다 (자유출차, 페널티 요금 적용)</p>
			</div>

			<fieldset class="plan1Only hidden" id="flightFieldset">
				<legend data-i18n="res_flightSectionTitle">✈️ 항공권 정보 (필수)</legend>
				<div class="formRow">
					<label data-i18n="res_flightNo">항공편명</label>
					<input type="text" id="flightNoInput" placeholder="예: KE001">
				</div>
				<div class="formRow">
					<label data-i18n="res_flightRoundtrip">왕복 여부</label>
					<select id="flightRoundtripInput">
						<option value="round">왕복</option>
						<option value="oneway">편도 (이용 불가)</option>
					</select>
				</div>
				<div class="formRow">
					<label data-i18n="res_flightArriveTime">귀국 도착 예정</label>
					<input type="time" id="flightArriveInput">
				</div>
			</fieldset>

			<div id="estimatedPriceBox"><span data-i18n="res_estimated">예상 금액</span>: <strong id="estimatedPrice">-</strong></div>

			<div id="payMethodArea">
				<label class="payOption"><input type="radio" name="payMethod" value="kakao"> 카카오페이</label>
				<label class="payOption"><input type="radio" name="payMethod" value="naver"> 네이버페이</label>
				<label class="payOption"><input type="radio" name="payMethod" value="card"> 카드</label>
				<label class="payOption"><input type="radio" name="payMethod" value="account"> 계좌이체</label>
			</div>

			<div id="paymentFooter">
				<div id="payBarPrice"><span data-i18n="res_depositLabel">예약금</span> <strong id="payBarAmount">-</strong>원</div>
				<button id="payBtn" data-i18n="res_payBtn" disabled>결제하기</button>
			</div>
		</div>
	</div>
	<!-- ============================================================ -->
	<!-- 결제 모듈 END -->
	<!-- ============================================================ -->

	<script src="js/jquery-1.8.1.min.js"></script>
	<script src="js/i18n.js"></script>
	<script src="js/payment.js"></script>
	<script src="js/reservation.js"></script>
</body>
</html>
