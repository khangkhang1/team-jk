<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
 주차맵 + 결제 합본 (안병찬 reservation2.jsp + 오윤섭 reservationOys.jsp)
   좌석·결항·회원타입 제한 : 안병찬  (DB seatList / memberType)
   결제 폼·포트원·항공편 검색 : 오윤섭 (form name="pay" / js/payment.js)
 Reservation?t_gubun=ReservationMap&zone=P1 로 들어온다 (ReservationMap 커맨드가 seatList 를 채운다)
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>주차맵 - 인천공항 주차예약</title>
<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
<script src="https://cdn.iamport.kr/v1/iamport.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/index1.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/c.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reservation.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/payment.css">

<script>
// ---------- 항공편 검색 팝업 (오윤섭) ----------
function goFlightSearch() {
    var ctx = "${pageContext.request.contextPath}";
    window.open(ctx + '/flight/flight_search.jsp', 'flightSearchWin', 'width=1000,height=760,scrollbars=yes');
}

function onFlightSelected(f) {
    var flightNoInput = document.getElementById("flightNoInput");
    if (flightNoInput) flightNoInput.value = f.flightNo;

    if (f.scheduleDateTime && f.scheduleDateTime.length >= 12) {
        var yyyy = f.scheduleDateTime.substr(0, 4);
        var MM   = f.scheduleDateTime.substr(4, 2);
        var dd   = f.scheduleDateTime.substr(6, 2);
        var hh   = f.scheduleDateTime.substr(8, 2);
        var mm   = f.scheduleDateTime.substr(10, 2);
        var formattedDate = yyyy + "-" + MM + "-" + dd;
        var formattedTime = hh + ":" + mm;

        var arriveDateInput = document.getElementById("flightArriveDateInput");
        if (arriveDateInput) arriveDateInput.value = formattedDate;
        var endDateInput = document.getElementById("endDateInput");
        if (endDateInput) endDateInput.value = formattedDate;
        var arriveInput = document.getElementById("flightArriveInput");
        if (arriveInput) arriveInput.value = formattedTime;
    }
    if (typeof refreshPaymentFooter === "function") refreshPaymentFooter();
    alert("항공편(" + f.flightNo + ")이 선택되었습니다.");
}

// ---------- 포트원 결제 (오윤섭) ----------
$(document).ready(function() {
    window.IMP.init("imp43028000");
});

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

function payment(method) {
    var IMP = window.IMP;
    var plan = document.getElementById("reservationPlan").value;
    var plan1_price = document.getElementById("estimatedPriceInput").value;
    var price = plan === '1' ? plan1_price : 5000;

    if (method === "kakaoPay") {
        IMP.request_pay({
            pg: "kakaopay",
            pay_method: "card",
            merchant_uid: "ORD_" + new Date().getTime(),
            name: "인천공항 주차장 예약",
            amount: price,
            buyer_name: "${empty sessionName ? '이용자' : sessionName}",
            buyer_email: "test@example.com"
        }, handleResponse);

    } else if (method === "creditCard") {
        IMP.request_pay({
            pg: "html5_inicis.INIpayTest",
            pay_method: "card",
            merchant_uid: "ORD_" + new Date().getTime(),
            name: "인천공항 주차장 예약",
            amount: price,
            buyer_name: "${empty sessionName ? '이용자' : sessionName}",
            buyer_email: "test@example.com"
        }, handleResponse);

    } else if (method === "naverPay") {
        IMP.request_pay({
            pg: "naverpay",
            pay_method: "card",
            merchant_uid: "ORD_" + new Date().getTime(),
            name: "인천공항 주차장 예약",
            amount: price,
            buyer_name: "${empty sessionName ? '이용자' : sessionName}",
            buyer_email: "test@example.com",
            naverPopupMode: true
        }, handleResponse);

    } else {
        alert("선택하신 결제 수단은 현재 미지원입니다.");
    }
}

function handleResponse(rsp) {
    if (rsp.success) {
        document.getElementById("impUidInput").value = rsp.imp_uid;
        document.getElementById("merchantUidInput").value = rsp.merchant_uid;
        var form = document.pay;
        form.t_gubun.value = "payment";
        form.method = "post";
        form.action = "${pageContext.request.contextPath}/Reservation";
        form.submit();
    } else {
        alert("결제에 실패했거나 취소되었습니다.\n사유: " + rsp.error_msg);
    }
}
</script>
</head>

<body>
<div class="wrap detail_body_top">

	<%@ include file="../common_header.jsp" %>

	<section class="zone_hero">
		<div class="zone_hero_inner">
			<div>
				<a href="${pageContext.request.contextPath}/ParkingStatus" class="zone_back">← 전체 주차맵으로</a>
				<div class="zone_hero_eyebrow">INCHEON AIRPORT T1 PARKING</div>
				<h1>
					<span id="zoneTitle">${selectedLotId} 구역</span>
					<small id="zoneType">-</small>
				</h1>
				<span id="liveBadge" class="live_badge" style="display:none"></span>
			</div>
			<div class="zone_hero_right">
				<div><span>이 구역 잔여</span><strong id="zoneRemain">-</strong></div>
				<div><span>전체 좌석</span><strong id="zoneTotal">-</strong></div>
				<div><span>혼잡도</span><strong id="zoneStatus">-</strong></div>
			</div>
		</div>
	</section>

	<div class="zone_tabs_wrap">
		<div class="zone_tabs" id="zoneTabs"></div>
	</div>

	<main class="main">
		<div class="container">
			<section class="detail_section">

				<%-- 이용 방식 : 구역에 따라 하나만 (P6~P9 예약형 / P1~P5 자유출차형) --%>
				<div class="detail_box">
					<div class="detail_box_head">
						<div>
							<h3>이용 방식</h3>
							<p>이 구역에서 이용할 수 있는 방식입니다. 구역 탭을 바꾸면 방식도 바뀝니다.</p>
						</div>
					</div>
					<div class="detail_box_body">
						<div class="plan_cards">
							<c:choose>
								<c:when test="${selectedLotId eq 'P6' or selectedLotId eq 'P7' or selectedLotId eq 'P8' or selectedLotId eq 'P9'}">
									<label class="plan_card selected">
										<input type="radio" name="planType" value="1" checked>
										<span class="plan_card_title">예약형 (1안)</span>
										<span class="plan_card_desc">시작·종료 시각을 미리 정합니다. 왕복 항공권 정보 입력 필수. 예약 시 전액 결제.</span>
									</label>
								</c:when>
								<c:otherwise>
									<label class="plan_card selected">
										<input type="radio" name="planType" value="2" checked>
										<span class="plan_card_title">자유출차형 (2안)</span>
										<span class="plan_card_desc">시작 시각만 정하고 종료는 자유입니다. 예약금 5,000원, 출차 때 30분당 4,500원 정산.</span>
									</label>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>

				<%-- 좌석 선택 --%>
				<div class="detail_box">
					<div class="detail_box_head">
						<div>
							<h3><span id="seatBoxZone">${selectedLotId}</span> 구역 좌석</h3>
							<p>자리를 클릭하면 결제 창이 열립니다. 예약된 자리와 결항 재배정 중인 자리는 선택할 수 없습니다.</p>
						</div>
						<div class="seat_toolbar" style="margin:0">
							<div class="seat_remain">잔여 <strong id="seatRemainCount">-</strong>석</div>
						</div>
					</div>

					<div class="detail_box_body">
						<div class="seat_legend">
							<span><i class="legend_box legend_free"></i> 선택 가능</span>
							<span><i class="legend_box legend_selected"></i> 선택됨</span>
							<span><i class="legend_box legend_taken"></i> 예약됨</span>
							<span><i class="legend_box legend_disabled"></i> ♿ 장애인</span>
							<span><i class="legend_box legend_ev"></i> 🔌 전기차</span>
							<span><i class="legend_box legend_cancelled"></i> ✈️ 결항 재배정중</span>
						</div>

						<div class="lot_map">
							<div class="lot_gate">
								<span class="gate_in">▲ 터미널 방향</span>
								<span id="lotMapZoneLabel">${selectedLotId} 구역 · 지상</span>
								<span class="gate_out">진출입로</span>
							</div>
							<svg id="lotSvg" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="구역 상세 주차맵">
								<image id="lotBaseImage" href="${pageContext.request.contextPath}/images/parking_map.png" x="0" y="0" width="1600" height="900"/>
								<path id="lotZoneOutline" class="lot_zone_outline"/>
								<g id="seatGrid"></g>
							</svg>
						</div>
					</div>

					<div class="seat_bottom">
						<span id="selectedSeatText">선택된 자리가 없습니다.</span>
						<button class="go_pay_btn" id="goPayBtn" type="button" disabled>결제 진행</button>
					</div>
				</div>

			</section>
		</div>
	</main>

	<%@ include file="../common_footer.jsp" %>
</div>

<%-- ============================================================ --%>
<%-- 결제 모듈 (오윤섭) - js/payment.js 가 이 폼의 id 들을 그대로 쓴다 --%>
<%-- ============================================================ --%>
<form name="pay">
<input type="hidden" name="t_gubun">
	<div id="paymentModal" class="hidden">
		<div id="paymentModalInner">
			<button id="paymentCloseBtn" type="button">&times;</button>

			<h3 id="paymentSeatTitle">-</h3>
			<p id="paymentLotInfo">-</p>
			<p id="paymentPlanInfo">-</p>

			<input type="hidden" id="reservationPlan" name="t_reservation_plan">
			<input type="hidden" id="reservationSeat" name="t_reservation_seat">

			<div class="formRow">
				<label data-i18n="res_dateLabel">주차 날짜</label>
				<input type="date" id="startDateInput" name="t_reservation_start_date">
			</div>
			<div class="formRow">
				<label data-i18n="res_startTimeLabel">주차 시각</label>
				<select id="startTimeInput" name="t_reservation_start_time"></select>
			</div>
			<div class="formRow plan1Only hidden">
				<label data-i18n="res_dateLabel">예상 출차 날짜</label>
				<input type="date" id="endDateInput" name="t_reservation_end_date">
			</div>
			<div class="formRow plan1Only hidden" id="durationRow">
				<label data-i18n="res_durationLabel">예상 출차 시각</label>
				<select id="endTimeInput" name="t_reservation_end_time"></select>
			</div>

			<div class="plan2Only hidden" id="endFreeNotice">
				<p data-i18n="res_endFreeNotice">종료 시각은 정하지 않습니다 (자유출차, 출차 시 정산)</p>
			</div>

			<fieldset class="plan1Only hidden" id="flightFieldset">
				<legend data-i18n="res_flightSectionTitle">✈️ 항공권 정보 (필수)</legend>
				<div class="formRow">
					<label data-i18n="res_flightNo">항공편명</label>
					<input type="text" id="flightNoInput" name="t_reservation_flight_no" disabled>
					<button type="button" onclick="goFlightSearch()" style="height:37.5px; width:50px;">검색</button>
				</div>
				<div class="formRow">
					<label data-i18n="res_flightRoundtrip">왕복 여부</label>
					<select id="flightRoundtripInput" disabled>
						<option value="round">왕복</option>
					</select>
				</div>
				<div class="formRow">
					<label>귀국 도착 날짜</label>
					<input type="date" id="flightArriveDateInput" name="t_reservation_flight_arrive_date" readonly>
				</div>
				<div class="formRow">
					<label data-i18n="res_flightArriveTime">귀국 도착 예정 시각</label>
					<input type="time" id="flightArriveInput" name="t_reservation_flight_arrive_time" readonly>
				</div>
			</fieldset>

			<div id="estimatedPriceBox"><span data-i18n="res_estimated">예상 금액</span>: <strong id="estimatedPrice">-</strong></div>
			<input type="hidden" name="t_reservation_estimate_amount" id="estimatedPriceInput">

			<div id="payMethodArea">
				<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="kakaoPay"> 카카오페이</label>
				<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="naverPay"> 네이버페이</label>
				<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="creditCard"> 카드</label>
				<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="account"> 계좌이체</label>
			</div>

			<div id="paymentFooter">
				<div id="payBarPrice"><span data-i18n="res_depositLabel">예약금</span> <strong id="payBarAmount">-</strong>원</div>
				<input type="hidden" id="depositAmount" name="t_reservation_deposit_amount" value="5000">
				<input type="hidden" name="t_imp_uid" id="impUidInput">
				<input type="hidden" name="t_merchant_uid" id="merchantUidInput">
				<button id="payBtn" data-i18n="res_payBtn" onclick="goPayment()" disabled type="button">결제하기</button>
			</div>
		</div>
	</div>
</form>

<script>
// ---------- DB 좌석 목록 (안병찬 ReservationMap 커맨드가 넘긴 seatList) ----------
var dbSeatList = [
<c:forEach var="mapDto" items="${seatList}" varStatus="status">
	{ seatNo: "${mapDto.seatId}", typeNm: "${mapDto.type}", status: "${mapDto.isReserved}" }<c:if test="${!status.last}">,</c:if>
</c:forEach>
];
var memberType = "${memberType}";
var isManager  = "${sessionScope.sessionLevel}" === "top";

// 인덱스(index.jsp) SVG 와 같은 도면 좌표 (viewBox 1600x900)
var ZONES = [
	{ id:"P1", type:"장기주차장", price:2000, total:50, rows:4,
	  box:{x:810,y:350,w:368,h:165},
	  path:"M810 375 Q810 350 835 350 L1010 360 Q1040 370 1065 390 L1075 420 Q1090 430 1115 445 L1150 450 Q1170 460 1178 490 L1175 510 Q1165 515 1140 515 L845 515 Q810 515 810 515 Z" },
	{ id:"P2", type:"장기주차장", price:2000, total:50, rows:4,
	  box:{x:390,y:355,w:375,h:160},
	  path:"M430 445 Q490 445 500 430 L510 420 Q520 390 535 375 L545 370 Q565 360 590 355 L760 355 Q765 355 765 355 L765 455 Q765 515 765 515 L420 515 Q390 515 395 505 Z" },
	{ id:"P3", type:"단기주차장", price:3000, total:50, rows:2,
	  box:{x:820,y:545,w:355,h:95},
	  path:"M820 545 L1140 545 Q1175 560 1175 580 L1170 620 Q1140 640 1130 640 L820 640 Z" },
	{ id:"P4", type:"단기주차장", price:3000, total:50, rows:2,
	  box:{x:400,y:550,w:370,h:85},
	  path:"M400 575 Q435 550 435 550 L755 550 Q770 550 770 575 L770 635 Q760 635 755 635 L435 635 Q410 630 410 630 Z" },
	{ id:"P5", type:"단기주차장", price:3000, total:50, rows:2,
	  box:{x:440,y:690,w:315,h:90},
	  path:"M440 690 Q460 690 475 690 L735 690 Q755 690 755 690 L755 765 Q755 780 755 780 L475 780 Q440 780 440 780 Z" },
	{ id:"P6", type:"단기주차장", price:3000, total:50, rows:3, bayW:6.2,
	  box:{x:840, y:255, w:140, h:65},
	  path:"M850 282 L980 258 L968 318 L840 318 Z" },
	{ id:"P7", type:"단기주차장", price:3000, total:50, rows:3, bayW:6.2,
	  box:{x:600, y:255, w:140, h:65},
	  path:"M600 258 L730 282 L740 318 L612 318 Z" },
	{ id:"P8", type:"단기주차장", price:3000, total:50, rows:2,
	  box:{x:860, y:195, w:125, h:45},
	  path:"M860 195 L985 195 L985 240 L860 240 Z" },
	{ id:"P9", type:"단기주차장", price:3000, total:50, rows:2,
	  box:{x:600, y:195, w:125, h:45},
	  path:"M600 195 L725 195 L725 240 L600 240 Z" }
];

var currentZone  = "${selectedLotId}" || getZoneFromUrl() || "P1";
var selectedSeat = null;

function getZoneFromUrl(){
	var m = location.search.match(/[?&]zone=(P[1-9])/i);
	return m ? m[1].toUpperCase() : null;
}

function findZone(id){
	for (var i=0;i<ZONES.length;i++){
		if (ZONES[i].id === id) return ZONES[i];
	}
	return ZONES[0];
}

// ---------- 좌석 : DB 값으로 만든다. 없으면(서버 없이 열었을 때) 임의 데이터 ----------
function makeSeats(zone, count){
	var seats = [];

	if (dbSeatList && dbSeatList.length > 0) {
		for (var i = 0; i < dbSeatList.length; i++) {
			var dbSeat = dbSeatList[i];

			var state = "free";
			if (dbSeat.status === "예약중") {
				state = "taken";
			} else if (dbSeat.status === "결항 재배정중") {
				// 결항 재배정은 예약형 구역(P6~P9)에서만 표시. 자유출차형 구역은 그냥 이용 중으로 본다
				state = (zone.id === "P6" || zone.id === "P7" || zone.id === "P8" || zone.id === "P9") ? "cancelled" : "taken";
			}

			var kind = "N";
			if (dbSeat.typeNm === "장애인차") kind = "D";
			else if (dbSeat.typeNm === "수소차" || dbSeat.typeNm === "전기차") kind = "E";

			seats.push({ no: dbSeat.seatNo, state: state, kind: kind });
		}
		return seats;
	}

	var seed = zone.id.charCodeAt(1);
	var n_total = count || zone.total;
	for (var j=1; j<=n_total; j++){
		var n = (j*7 + seed*13) % 100;
		var st = "free";
		if (n < 34) st = "taken";
		else if (n < 40) st = "cancelled";
		var kd = "N";
		if (j % 12 === 0) kd = "D";
		else if (j % 9 === 0) kd = "E";
		seats.push({ no: zone.id + "-" + String(j).padStart(2,"0"), state: st, kind: kd });
	}
	return seats;
}

// ---------- 구역 블록 안에 주차 칸 배치 ----------
function layoutBays(zone, outlineEl, svgEl){
	function inside(x, y){
		var p = svgEl.createSVGPoint();
		p.x = x; p.y = y;
		return outlineEl.isPointInFill(p);
	}

	function spanAt(yTop, yBottom){
		var step = zone.box.w / 300;
		var start = null, end = null;
		for (var x = zone.box.x; x <= zone.box.x + zone.box.w; x += step){
			if (inside(x, yTop) && inside(x, yBottom) && inside(x, (yTop+yBottom)/2)){
				if (start === null) start = x;
				end = x;
			} else if (start !== null && end !== null && (x - end) > step*2){
				break;
			}
		}
		if (start === null) return null;
		var inset = step * 1.5;
		if ((end - start) <= inset*2) return null;
		return { x1:start + inset, x2:end - inset };
	}

	function build(rows, bayW){
		var laneCount = Math.max(1, Math.floor(rows / 2));
		var laneH     = zone.box.h * (rows > 2 ? 0.10 : 0.16);
		var bayH      = (zone.box.h - laneH*laneCount - zone.box.h*0.06) / rows;
		if (bayH <= 0.5) return null;

		var res = { bays:[], lanes:[], bayW:bayW, bayH:bayH, rows:rows };
		var y = zone.box.y + zone.box.h*0.03;

		var baseSpan = null;
		for (var pr=0; pr<rows; pr++){
			var probeY = zone.box.y + zone.box.h*0.03 + pr*bayH + (pr>0 ? laneH*Math.floor(pr/2) : 0);
			var sp = spanAt(probeY + 0.8, probeY + bayH - 0.8);
			if (sp && (!baseSpan || (sp.x2-sp.x1) > (baseSpan.x2-baseSpan.x1))) baseSpan = sp;
		}
		var gridX0 = baseSpan ? baseSpan.x1 : zone.box.x;

		for (var r=0; r<rows; r++){
			var span = spanAt(y + 0.8, y + bayH - 0.8);
			if (span){
				var startIdx = Math.ceil((span.x1 - gridX0) / bayW);
				for (var c=startIdx; ; c++){
					var bx = gridX0 + c*bayW;
					if (bx + bayW > span.x2) break;
					if (bx < span.x1) continue;
					var b = { x:bx + 0.3, y:y + 0.3, w:bayW - 0.6, h:bayH - 0.6 };
					if (inside(b.x, b.y) && inside(b.x+b.w, b.y) &&
					    inside(b.x, b.y+b.h) && inside(b.x+b.w, b.y+b.h)){
						res.bays.push(b);
					}
				}
			}
			y += bayH;
			if (r % 2 === 1 && r < rows-1){
				var laneSpan = spanAt(y + laneH*0.35, y + laneH*0.65);
				if (laneSpan) res.lanes.push({ x1:laneSpan.x1, x2:laneSpan.x2, y:y + laneH/2 });
				y += laneH;
			}
		}
		return res;
	}

	var TARGET_RATIO = 0.5;
	var best = null, bestScore = Infinity;
	for (var rows = 2; rows <= 6; rows++){
		for (var step = 0; step <= 40; step++){
			var bw = zone.box.h * (0.06 + step*0.012);
			var cand = build(rows, bw);
			if (!cand || !cand.bays.length) continue;
			var ratio = cand.bayW / cand.bayH;
			if (ratio < 0.3 || ratio > 0.75) continue;
			var score = Math.abs(cand.bays.length - zone.total) + Math.abs(ratio - TARGET_RATIO) * 22;
			if (score < bestScore){ bestScore = score; best = cand; }
		}
	}
	return best || { bays:[], lanes:[] };
}

// ---------- 실시간 구역 현황 (/ParkingStatus, 공공데이터) ----------
var LIVE_ZONE_STATUS = null;

function normalizeZoneStatus(raw){
	var out = {};
	function put(row, keyName){
		if (!row || !row[keyName]) return;
		out[row[keyName]] = {
			remain : row.availableCount,
			total  : row.totalCount,
			status : row.congestion,
			floor  : row.floor  || "",
			datetm : row.datetm || ""
		};
	}
	(raw.longTerm  || []).forEach(function(r){ put(r, "parkLotNo");  });
	(raw.shortTerm || []).forEach(function(r){ put(r, "parkZoneNo"); });
	return out;
}

function loadLiveZoneStatus(cb){
	if (LIVE_ZONE_STATUS !== null) { cb && cb(); return; }
	try {
		var xhr = new XMLHttpRequest();
		xhr.open("GET", "${pageContext.request.contextPath}/ParkingStatus?refresh=true", true);
		xhr.onreadystatechange = function(){
			if (xhr.readyState !== 4) return;
			if (xhr.status === 200){
				try { LIVE_ZONE_STATUS = normalizeZoneStatus(JSON.parse(xhr.responseText)); }
				catch(e){ LIVE_ZONE_STATUS = {}; }
			} else {
				LIVE_ZONE_STATUS = {};
			}
			cb && cb();
		};
		xhr.send();
	} catch(e){
		LIVE_ZONE_STATUS = {};
		cb && cb();
	}
}

function applyLiveZoneStatus(zoneId){
	var badgeEl = document.getElementById("liveBadge");
	if (!LIVE_ZONE_STATUS) return;
	var live = LIVE_ZONE_STATUS[zoneId];
	if (!live){
		if (badgeEl) badgeEl.style.display = "none";
		return;
	}
	document.getElementById("zoneRemain").textContent = live.remain.toLocaleString() + "석";
	document.getElementById("zoneTotal").textContent  = live.total.toLocaleString() + "석";
	document.getElementById("zoneStatus").textContent = live.status;
	if (badgeEl){
		badgeEl.style.display = "";
		badgeEl.textContent = "실시간 · " + live.floor + " (" + formatDatetm(live.datetm) + " 기준)";
	}
}

function formatDatetm(s){
	if (!s || s.length < 12) return "";
	return s.substring(4,6) + "-" + s.substring(6,8) + " " + s.substring(8,10) + ":" + s.substring(10,12);
}

// ---------- 구역 탭 : 좌석이 DB 에서 오므로 서버를 다시 부른다 (조회 시간대는 유지) ----------
function renderZoneTabs(){
	var html = "";
	for (var i=0;i<ZONES.length;i++){
		var z = ZONES[i];
		html += '<button type="button" class="zone_tab' + (z.id===currentZone ? ' active' : '') + '" data-zone="' + z.id + '">'
		      + z.id + '<span class="zone_tab_sub">' + z.type.replace("주차장","") + '</span></button>';
	}
	document.getElementById("zoneTabs").innerHTML = html;

	var tabs = document.querySelectorAll(".zone_tab");
	for (var j=0;j<tabs.length;j++){
		tabs[j].addEventListener("click", function(){
			var url = "${pageContext.request.contextPath}/Reservation?t_gubun=ReservationMap&zone=" + this.getAttribute("data-zone");
			var qs  = "${param.reqStartTime}";
			var qe  = "${param.reqEndTime}";
			if (qs) url += "&reqStartTime=" + encodeURIComponent(qs);
			if (qe) url += "&reqEndTime="   + encodeURIComponent(qe);
			location.href = url;
		});
	}
}

// ---------- 화면 그리기 ----------
function renderAll(){
	var zone = findZone(currentZone);

	document.getElementById("zoneTitle").textContent = zone.id + " 구역";
	document.getElementById("zoneType").textContent  = zone.type + " · 시간당 " + zone.price.toLocaleString() + "원";
	document.getElementById("seatBoxZone").textContent = zone.id;
	document.getElementById("lotMapZoneLabel").textContent = zone.id + " 구역 · 지상";

	var tabs = document.querySelectorAll(".zone_tab");
	for (var t=0;t<tabs.length;t++){
		tabs[t].classList.toggle("active", tabs[t].getAttribute("data-zone") === currentZone);
	}

	var pad = Math.max(zone.box.w, zone.box.h) * 0.15;
	document.getElementById("lotSvg").setAttribute("viewBox",
		(zone.box.x - pad) + " " + (zone.box.y - pad) + " " + (zone.box.w + pad*2) + " " + (zone.box.h + pad*2));

	var outlineEl = document.getElementById("lotZoneOutline");
	outlineEl.setAttribute("d", zone.path);

	var layout = layoutBays(zone, outlineEl, document.getElementById("lotSvg"));
	var seats  = makeSeats(zone, layout.bays.length);
	for (var q=0; q<layout.bays.length; q++){
		if (seats[q]) layout.bays[q].seat = seats[q];
	}

	var remain = 0;
	for (var rr=0; rr<seats.length; rr++){
		if (seats[rr].state === "free") remain++;
	}
	document.getElementById("zoneRemain").textContent = remain + "석";
	document.getElementById("zoneTotal").textContent  = seats.length + "석";
	document.getElementById("seatRemainCount").textContent = remain;
	var ratio = seats.length ? remain / seats.length : 0;
	document.getElementById("zoneStatus").textContent = ratio > 0.5 ? "여유" : (ratio > 0.2 ? "보통" : "혼잡");

	applyLiveZoneStatus(zone.id);

	var svg = "";
	for (var l=0; l<layout.lanes.length; l++){
		var lane = layout.lanes[l];
		svg += '<line class="lot_lane" x1="' + lane.x1 + '" y1="' + lane.y + '" x2="' + lane.x2 + '" y2="' + lane.y + '"/>';
	}
	for (var b=0; b<layout.bays.length; b++){
		var it = layout.bays[b];
		var seat = it.seat;
		if (!seat) continue;

		var cls = "bay_g";
		if (seat.state === "taken")     cls += " taken";
		if (seat.state === "cancelled") cls += " cancelled";
		if (seat.kind === "D") cls += " disabled_seat";
		if (seat.kind === "E") cls += " ev_seat";

		var label = seat.no.indexOf("-") > -1 ? seat.no.split("-")[1] : seat.no;
		if (seat.kind === "D") label = "♿";
		else if (seat.kind === "E") label = "⚡";
		else if (seat.state === "cancelled") label = "✈";

		svg += '<g class="' + cls + '" data-seat="' + seat.no + '" data-state="' + seat.state + '" data-kind="' + seat.kind + '">'
		     + '<rect class="bay" x="' + it.x.toFixed(1) + '" y="' + it.y.toFixed(1) + '" width="' + it.w.toFixed(1) + '" height="' + it.h.toFixed(1) + '" rx="1"/>'
		     + '<text class="bay_label" x="' + (it.x + it.w/2).toFixed(1) + '" y="' + (it.y + it.h/2).toFixed(1) + '">' + label + '</text>'
		     + '</g>';
	}
	document.getElementById("seatGrid").innerHTML = svg;

	var seatEls = document.querySelectorAll(".bay_g");
	for (var k=0; k<seatEls.length; k++){
		seatEls[k].addEventListener("click", function(){
			if (this.getAttribute("data-state") !== "free") return;

			// 회원 차량 종류에 맞는 자리만 (관리자는 제한 없음)
			var seatKind = this.getAttribute("data-kind");
			if (!isManager) {
				if (seatKind === "D" && memberType !== "D") {
					alert("♿ 장애인 전용 구역은 장애인 등록 회원만 선택하실 수 있습니다.");
					return;
				}
				if (seatKind === "E" && memberType !== "E") {
					alert("⚡ 전기차/수소차 전용 구역은 친환경차 등록 회원만 선택하실 수 있습니다.");
					return;
				}
			}

			var prev = document.querySelector(".bay_g.selected");
			if (prev) prev.classList.remove("selected");
			this.classList.add("selected");
			selectedSeat = this.getAttribute("data-seat");
			document.getElementById("selectedSeatText").innerHTML = "선택한 자리 : <strong>" + selectedSeat + "</strong>";
			document.getElementById("goPayBtn").disabled = false;
		});
	}

	document.getElementById("selectedSeatText").textContent = "선택된 자리가 없습니다.";
	document.getElementById("goPayBtn").disabled = true;
}

// ---------- 결제 모달 열기 (payment.js 의 openPaymentModal) ----------
document.getElementById("goPayBtn").addEventListener("click", function() {
	if (!selectedSeat) return;
	<c:if test="${empty sessionScope.sessionId}">
	if (confirm("로그인 후 예약할 수 있습니다. 로그인 화면으로 이동할까요?")) {
		location.href = "${pageContext.request.contextPath}/Member";
	}
	return;
	</c:if>

	var currentPlan = "1";
	var selectedRadio = document.querySelector("input[name='planType']:checked");
	if (selectedRadio) currentPlan = selectedRadio.value;

	document.getElementById("reservationPlan").value = currentPlan;
	document.getElementById("reservationSeat").value = selectedSeat;

	if (typeof window.openPaymentModal === "function") {
		window.openPaymentModal(selectedSeat, currentPlan);
	} else {
		document.getElementById("paymentModal").classList.remove("hidden");
	}
});

renderZoneTabs();
renderAll();
loadLiveZoneStatus(function(){ applyLiveZoneStatus(currentZone); });
</script>
<script src="${pageContext.request.contextPath}/js/payment.js"></script>
<script>
// payment.js 는 ?lot= 파라미터로 요금을 찾는데 이 화면은 zone 을 쓰므로, 현재 구역의 요금을 돌려주게 바꿔 끼운다
window.getCurrentLotInfo = function(){
	var z = findZone(currentZone);
	return { name: "인천공항 1터미널 " + z.id + " 구역 · " + z.type, addr: "", price: z.price };
};
</script>
</body>
</html>
