<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<!DOCTYPE html>



<html lang="ko">



<head>



<meta charset="UTF-8">



<meta name="viewport" content="width=device-width, initial-scale=1.0">



<title>주차맵 - 인천공항 주차예약</title>







<!-- 인덱스 디자인 시스템 CSS -->



<link rel="stylesheet" href="css/index1.css">



<link rel="stylesheet" href="css/c.css">



<link rel="stylesheet" href="css/reservation.css">



<link rel="stylesheet" href="css/payment.css">



</head>

<script>

//1. Document가 준비된 후 식별코드 초기화

$(document).ready(function() {

var IMP = window.IMP;

IMP.init("imp43028000");

});



// 2. 예약 버튼 클릭 시 호출

function goPayment() {

var method = document.pay.t_reservation_pay_method.value;



if (!method) {

alert("결제 수단을 선택해 주세요.");

return;

}



if (confirm("예약 및 결제를 진행하시겠습니까?")) {

payment(method); // 결제 프로세스 시작

}

}



// 3. 포트원 결제창 호출 함수

function payment(method) {

var IMP = window.IMP;



// 모달창 등에 입력된 예상 금액 가져오기 (없으면 기본값 설정)

// var amountVal = document.getElementById("estimatedPriceInput").value;

// var price = amountVal ? parseInt(amountVal) : document.pay.t_reservation_deposit_amount;

var price = document.getElementById("depositAmount").value;



// 카카오페이 결제

if (method === "kakaoPay") {

IMP.request_pay({

pg: "kakaopay",

pay_method: "card",

merchant_uid: "ORD_" + new Date().getTime(),

name: "인천공항 주차장 예약",

amount: price,

buyer_name: "홍길동", // 필요 시 로그인 회원 이름으로 교체

buyer_email: "test@example.com"

}, handleResponse);



// 신용카드 결제

} else if (method === "creditCard") {

IMP.request_pay({

pg: "html5_inicis.INIpayTest",

pay_method: "card",

merchant_uid: "ORD_" + new Date().getTime(),

name: "인천공항 주차장 예약",

amount: price,

buyer_name: "홍길동",

buyer_email: "test@example.com"

}, handleResponse);



} else {

alert("선택하신 결제 수단은 현재 미지원입니다.");

return;

}

}



// 4. 포트원 결제 완료 응답 처리 함수

function handleResponse(rsp) {

if (rsp.success) {

/*

var seat = document.pay.t_reservation_seat.value;

var plan = document.pay.t_reservation_plan.value;

if(plan === '1') plan = "예약형"

else plan = "자율출차형"

var deposit_amount = document.pay.t_reservation_deposit_amount.value;

alert(

'예약이 완료되었습니다.\n\n' +

'좌석: ' + seat + '\n' +

'이용방식: ' + plan + '\n' +

'예약금: ' + deposit_amount + '원 결제\n\n' +

'(실제 결제/서버 저장 및 항공편 결항 감지 API 연동은 다음 단계에서 연결됩니다)'

);

*/



// 서버(Servlet)로 보낼 hidden input에 포트원 번호 등록

document.getElementById("impUidInput").value = rsp.imp_uid;

document.getElementById("merchantUidInput").value = rsp.merchant_uid;



// 결제 성공 시에만 최종적으로 Servlet으로 Form Submit 전송!

var form = document.pay;

form.method = "post";

form.action = "Reservation"; // Reservation Servlet으로 전송

form.submit();



} else {

alert("결제에 실패했거나 취소되었습니다.\n사유: " + rsp.error_msg);

return;

}

}

</script>





<body>



<div class="wrap detail_body_top">







<!-- ============================================================



HEADER



============================================================ -->



<header class="header scrolled">



<div class="header_inner">







<a href="index2.html" class="logo">



인천공항 주차예약



<small>INCHEON AIRPORT PARKING</small>



</a>







<nav class="header_menu">



<li>



<a href="${pageContext.request.contextPath}/index.jsp#parking">교통 · 주차</a>



<div class="header_dropdown">



<a href="${pageContext.request.contextPath}/index.jsp#guide">주차장 이용 안내</a>



<a href="${pageContext.request.contextPath}/index.jsp#parking">주차 요금</a>



<a href="${pageContext.request.contextPath}/index.jsp#parking">주차장 혼잡도</a>



</div>



</li>



<li>



<a href="index" class="active">주차 예약 조회</a>



<div class="header_dropdown">



<a href="#">예약 내역</a>



<a href="#">예약 확인</a>



<a href="#">예약 취소</a>



<a href="#">이용 내역</a>



</div>



</li>



<li>



<a href="${pageContext.request.contextPath}/index.jsp#notice">공지 사항</a>



<div class="header_dropdown">



<a href="#">공지 사항</a>



<a href="#">자주 하는 질문</a>



</div>



</li>



</nav>







<div class="header_right">



<a href="${pageContext.request.contextPath}/member/member_login.jsp">로그인</a>



<span>|</span>



<a href="${pageContext.request.contextPath}/member/member_join.jsp">회원가입</a>



</div>







<button class="menu_btn" aria-label="메뉴">☰</button>



</div>



</header>







<!-- ============================================================



페이지 타이틀 밴드



============================================================ -->



<section class="zone_hero">



<div class="zone_hero_inner">



<div>



<a href="${pageContext.request.contextPath}/index.jsp#reserve" class="zone_back">← 전체 주차맵으로</a>



<div class="zone_hero_eyebrow">INCHEON AIRPORT T1 PARKING</div>



<h1>



<span id="zoneTitle">${selectedLotId} 구역</span>



<small id="zoneType">단기주차장 · 시간당 3,000원</small>



</h1>



<!-- 공공데이터 실시간 현황을 쓰는 구역일 때만 표시됨 -->



<span id="liveBadge" class="live_badge" style="display:none"></span>



</div>







<div class="zone_hero_right">



<div>



<span>이 구역 잔여</span>



<strong id="zoneRemain">-</strong>



</div>



<div>



<span>전체 좌석</span>



<strong id="zoneTotal">-</strong>



</div>



<div>



<span>혼잡도</span>



<strong id="zoneStatus">-</strong>



</div>



</div>



</div>



</section>







<!-- ============================================================



구역 탭



============================================================ -->



<div class="zone_tabs_wrap">



<div class="zone_tabs" id="zoneTabs"></div>



</div>







<main class="main">



<div class="container">



<section class="detail_section">







<!-- 이용 방식 -->



<div class="detail_box">





<div class="detail_box_body">



<div class="plan_cards">



<c:if test="${ selectedLotId eq 'P6'|| selectedLotId eq 'P7'|| selectedLotId eq 'P8'||selectedLotId eq 'P9'}">



<label class="plan_card selected">



<input type="hidden" name="planType" value="1" checked>



<span class="plan_card_title">예약형 (1안)</span>



<span class="plan_card_desc">시작·종료 시각을 미리 정합니다. 왕복 항공권 정보 입력 필수. 기본 요금.</span>



</label>



</c:if>



<c:if test="${ selectedLotId eq 'P1'|| selectedLotId eq 'P2'|| selectedLotId eq 'P3'||selectedLotId eq 'P4'|| selectedLotId eq 'P5'}">



<label class="plan_card">



<input type="hidden" name="planType" value="2">



<span class="plan_card_title">자유출차형 (2안)</span>



<span class="plan_card_desc">시작 시각만 정하고 종료는 자유입니다. 장기주차 구역 전용, 페널티 요금 적용.</span>



</label>



</c:if>



</div>



</div>



</div>







<!-- 좌석 선택 -->



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







<!-- 주차 상세맵 -->



<div class="lot_map">



<div class="lot_gate">



<span class="gate_in">▲ 터미널 방향</span>



<span id="lotMapZoneLabel">${selectedLotId} 구역 · 지상</span>



<span class="gate_out">진출입로</span>



</div>



<svg id="lotSvg" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="구역 상세 주차맵">



<!-- 실제 도면 -->



<image id="lotBaseImage" href="images/parking_map.png" x="0" y="0" width="1600" height="900"/>



<!-- 이 구역 블록 외곽선 -->



<path id="lotZoneOutline" class="lot_zone_outline"/>



<!-- 주차 칸들 -->



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







<!-- FOOTER -->



<footer class="footer">



<div class="footer_inner">



<div class="footer_top">



<div class="footer_logo">



인천공항 주차예약



<small>INCHEON AIRPORT PARKING</small>



</div>



<div class="footer_links">



<a href="#">이용약관</a>



<a href="#">개인정보처리방침</a>



<a href="#">사이트맵</a>



</div>



</div>



<div class="footer_info">



<p>제1여객터미널 주차예약 서비스 · 본 사이트는 팀프로젝트 목적으로 제작되었습니다.</p>



<p class="copyright">Copyright © Parking Reservation Project. All rights reserved.</p>



</div>



</div>



</footer>



</div>







<form name="pay">

<input type="hidden" name="t_gubun" value="payment">

<div id="paymentModal" class="hidden">

<div id="paymentModalInner">

<button id="paymentCloseBtn" type="button">&times;</button>



<h3 id="paymentSeatTitle">-</h3>

<p id="paymentLotInfo">-</p>

<p id="paymentPlanInfo">-</p>



<!-- Servlet으로 예약 유형 및 좌석 정보 넘기기 위한 input / 결제 시 DB에 저장 위함-->

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

<div class="formRow">

<label data-i18n="res_dateLabel">예상 출차 날짜</label>

<input type="date" id="endDateInput" name="t_reservation_end_date">

</div>

<div class="formRow plan1Only hidden" id="durationRow">

<label data-i18n="res_durationLabel">이용 시간</label>

<select id="endTimeInput" name="t_reservation_end_time"></select>

</div>



<div class="plan2Only hidden" id="endFreeNotice">

<p data-i18n="res_endFreeNotice">종료 시각은 정하지 않습니다 (자유출차, 페널티 요금 적용)</p>

</div>



<fieldset class="plan1Only hidden" id="flightFieldset">

<legend data-i18n="res_flightSectionTitle">✈️ 항공권 정보 (필수)</legend>

<div class="formRow">

<label data-i18n="res_flightNo">항공편명</label>

<input type="text" id="flightNoInput" placeholder="1 입력 필요(test단계)" name="t_reservation_flight_no">

</div>

<!-- 예약 유형 선택 후 결제창 진입: 왕복 여부 선택 불필요 판단 / 이후 수정 필요할 것 같음 -->

<div class="formRow">

<label data-i18n="res_flightRoundtrip">왕복 여부</label>

<select id="flightRoundtripInput">

<option value="round">왕복</option>

<option value="oneway">편도 (이용 불가)</option>

</select>

</div>

<!-- 귀국 도착 예정 시간은 name으로 넘길 필요가 있는가? -->

<div class="formRow">

<label data-i18n="res_flightArriveTime">귀국 도착 예정</label>

<input type="time" id="flightArriveInput">

</div>

</fieldset>



<div id="estimatedPriceBox"><span data-i18n="res_estimated">예상 금액</span>: <strong id="estimatedPrice">-</strong></div>

<!-- Servlet으로 예상 금액 넘기기 위한 input(payment.js수정) / 예약 목록 확인 시 예상 금액 노출-->

<input type="hidden" name="t_reservation_estimate_amount" id="estimatedPriceInput">

<div id="payMethodArea">

<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="kakaoPay"> 카카오페이</label>

<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="naverPay"> 네이버페이</label>

<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="creditCard"> 카드</label>

<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="account"> 계좌이체</label>

</div>



<div id="paymentFooter">

<div id="payBarPrice"><span data-i18n="res_depositLabel">예약금</span> <strong id="payBarAmount">-</strong>원</div>

<!-- Servlet으로 예약금 넘기기 위한 input / 예약 목록 확인 시 예약금 노출 / 필요 없는 경우 삭제 예정 -->

<input type="hidden" id="depositAmount" name="t_reservation_deposit_amount" value="5000">

<!-- 포트원 결제 검증 및 DB 저장을 위한 hidden input 추가 -->

<input type="hidden" name="t_imp_uid" id="impUidInput">

<input type="hidden" name="t_merchant_uid" id="merchantUidInput">

<button id="payBtn" data-i18n="res_payBtn" onclick="goPayment()" disabled type="button">결제하기</button>

</div>

</div>

</div>

</form>

<!-- 결제 모듈 END -->









<script>



// DB에서 넘어온 좌석 리스트 데이터 바인딩



var dbSeatList = [



<c:forEach var="mapDto" items="${seatList}" varStatus="status">



{



seatNo: "${mapDto.seatId}",



typeNm: "${mapDto.type}",



status: "${mapDto.isReserved}"



}<c:if test="${!status.last}">,</c:if>



</c:forEach>



];







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







var currentZone = "${selectedLotId}" || getZoneFromUrl() || "P1";



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







/* DB 데이터를 기반으로 좌석 정보 생성 */



function makeSeats(zone, count){



var seats = [];





if (dbSeatList && dbSeatList.length > 0) {



for (var i = 0; i < dbSeatList.length; i++) {



var dbSeat = dbSeatList[i];





// ★ DB에서 넘어오는 status 문자열 값에 따른 분기 처리



var state = "free";



if (dbSeat.status === "예약중") {



state = "taken";



} else if (dbSeat.status === "결항 재배정중") {



// ★ 추가된 핵심 로직: P6~P9(예약형)일 때만 결항 아이콘 적용



if (zone.id === "P6" || zone.id === "P7" || zone.id === "P8" || zone.id === "P9") {



state = "cancelled"; // cancelled 클래스를 부여하여 클릭 불가 및 ✈️ 아이콘 표시



} else {



// P1~P5(자유출차형)는 결항이 떠도 그냥 '예약중(이용중)'으로 덮어버림



state = "taken";



}



}





var kind = "N";



if (dbSeat.typeNm === "장애인차") kind = "D";



else if (dbSeat.typeNm === "수소차" || dbSeat.typeNm === "전기차") kind = "E";





seats.push({



no: dbSeat.seatNo,



state: state,



kind: kind



});



}



} else {



// DB 데이터가 없는 경우 임의 데이터 생성 예외 처리



var seed = zone.id.charCodeAt(1);



var n_total = count || zone.total;



for (var i=1; i<=n_total; i++){



var n = (i*7 + seed*13) % 100;



var state = "free";



if (n < 34) state = "taken";



else if (n < 40) state = "cancelled";



var kind = "normal";



if (i % 12 === 0) kind = "D";



else if (i % 9 === 0) kind = "E";



seats.push({ no: zone.id + "-" + String(i).padStart(2,"0"), state: state, kind: kind });



}



}



return seats;



}







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



var laneH = zone.box.h * (rows > 2 ? 0.10 : 0.16);



var bayH = (zone.box.h - laneH*laneCount - zone.box.h*0.06) / rows;



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







var LIVE_ZONE_STATUS = null;







function loadLiveZoneStatus(cb){



if (LIVE_ZONE_STATUS !== null) { cb && cb(); return; }



try {



var xhr = new XMLHttpRequest();



xhr.open("GET", "Parking?t_gubun=zoneStatus", true);



xhr.onreadystatechange = function(){



if (xhr.readyState !== 4) return;



if (xhr.status === 200){



try { LIVE_ZONE_STATUS = JSON.parse(xhr.responseText); }



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



document.getElementById("zoneTotal").textContent = live.total.toLocaleString() + "석";



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







function renderZoneTabs(){



var html = "";



for (var i=0;i<ZONES.length;i++){



var z = ZONES[i];



html += '<button type="button" class="zone_tab' + (z.id===currentZone ? ' active' : '') + '" data-zone="' + z.id + '">'



+ z.id



+ '<span class="zone_tab_sub">' + z.type.replace("주차장","") + '</span>'



+ '</button>';



}



document.getElementById("zoneTabs").innerHTML = html;







var tabs = document.querySelectorAll(".zone_tab");



for (var j=0;j<tabs.length;j++){



tabs[j].addEventListener("click", function(){



var targetZone = this.getAttribute("data-zone");



location.href = "Reservation?t_gubun=ReservationMap&zone=" + targetZone;



});



}



}







function renderAll(){



var zone = findZone(currentZone);



var seats = makeSeats(zone);







document.getElementById("zoneTitle").textContent = zone.id + " 구역";



document.getElementById("zoneType").textContent = zone.type + " · 시간당 " + zone.price.toLocaleString() + "원";



document.getElementById("seatBoxZone").textContent = zone.id;







var remain = 0;



for (var i=0;i<seats.length;i++){



if (seats[i].state === "free") remain++;



}



document.getElementById("zoneRemain").textContent = remain + "석";



document.getElementById("zoneTotal").textContent = seats.length + "석";



document.getElementById("seatRemainCount").textContent = remain;







var ratio = seats.length ? remain / seats.length : 0;



var statusEl = document.getElementById("zoneStatus");



statusEl.textContent = ratio > 0.5 ? "여유" : (ratio > 0.2 ? "보통" : "혼잡");







var tabs = document.querySelectorAll(".zone_tab");



for (var t=0;t<tabs.length;t++){



tabs[t].classList.toggle("active", tabs[t].getAttribute("data-zone") === currentZone);



}







document.getElementById("lotMapZoneLabel").textContent = zone.id + " 구역 · 지상";







var pad = Math.max(zone.box.w, zone.box.h) * 0.15;



document.getElementById("lotSvg").setAttribute("viewBox",



(zone.box.x - pad) + " " + (zone.box.y - pad) + " " +



(zone.box.w + pad*2) + " " + (zone.box.h + pad*2));







var outlineEl = document.getElementById("lotZoneOutline");



outlineEl.setAttribute("d", zone.path);





var layout = layoutBays(zone, outlineEl, document.getElementById("lotSvg"));



seats = makeSeats(zone, layout.bays.length);



for (var q=0; q<layout.bays.length; q++){



if (seats[q]) {



layout.bays[q].seat = seats[q];



}



}







remain = 0;



for (var rr=0; rr<seats.length; rr++){



if (seats[rr].state === "free") remain++;



}



document.getElementById("zoneRemain").textContent = remain + "석";



document.getElementById("zoneTotal").textContent = seats.length + "석";



document.getElementById("seatRemainCount").textContent = remain;



ratio = seats.length ? remain / seats.length : 0;



statusEl.textContent = ratio > 0.5 ? "여유" : (ratio > 0.2 ? "보통" : "혼잡");







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



if (seat.state === "taken") cls += " taken";



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



for (var k = 0; k < seatEls.length; k++) {



seatEls[k].addEventListener("click", function(){



if (this.getAttribute("data-state") !== "free") return;





var seatKind = this.getAttribute("data-kind");





// ★ 정의되지 않은 변수 에러 방지용 안전장치 (기본값 설정)



var memberType = "${memberType}"; // "장애인", "전기차", "일반" 등



// 장애인 회원 여부 확인 (DB 반환값 조건에 맞게 설정)

var isUserDisabled = (memberType === "장애인" || memberType === "D" || memberType === "장애인차");

var isUserEv = (memberType === "전기차" || memberType === "수소차" || memberType === "E");

var isManager = ("${sessionScope.m_level}" === "TOP"); // 관리자 권한

// 관리자가 아닐 때만 제한 조건 체크






if (!isManager) {

//장애인인지 체크

if (seatKind === "D" && !isUserDisabled) {

alert("♿ 장애인 전용 구역은 장애인 등록 회원만 선택하실 수 있습니다.");

return; // 선택 차단

}

//아니면 수소차인지 체크

if (seatKind === "E" && !isUserEv) {

alert("⚡ 전기차/수소차 전용 구역은 친환경차 등록 회원만 선택하실 수 있습니다.");

return; // 선택 차단

}

}





// 정상 선택 로직



var prev = document.querySelector(".bay_g.selected");



if (prev) prev.classList.remove("selected");



this.classList.add("selected");



selectedSeat = this.getAttribute("data-seat");



document.getElementById("selectedSeatText").innerHTML =



"선택한 자리 : <strong>" + selectedSeat + "</strong>";



document.getElementById("goPayBtn").disabled = false;



});



}







document.getElementById("selectedSeatText").textContent = "선택된 자리가 없습니다.";



document.getElementById("goPayBtn").disabled = true;



}







/* 이용 방식 카드 선택 */



var planCards = document.querySelectorAll(".plan_card");



for (var p=0;p<planCards.length;p++){



planCards[p].addEventListener("click", function(){



for (var q=0;q<planCards.length;q++) planCards[q].classList.remove("selected");



this.classList.add("selected");



var isPlan1 = this.querySelector("input").value === "1";



document.getElementById("flightFieldset").classList.toggle("hidden", !isPlan1);



document.getElementById("durationRow").classList.toggle("hidden", !isPlan1);



document.getElementById("endFreeNotice").classList.toggle("hidden", isPlan1);



});



}







/* 결제 모달 열기/닫기 */



document.getElementById("goPayBtn").addEventListener("click", function(){



if (!selectedSeat) return;



var zone = findZone(currentZone);



document.getElementById("paymentSeatTitle").textContent = selectedSeat + " 자리 예약";



document.getElementById("paymentLotInfo").textContent = "인천공항 1터미널 " + zone.id + " 구역 · " + zone.type;



document.getElementById("paymentPlanInfo").textContent = "시간당 " + zone.price.toLocaleString() + "원";



document.getElementById("paymentModal").classList.remove("hidden");



});



document.getElementById("paymentCloseBtn").addEventListener("click", function(){



document.getElementById("paymentModal").classList.add("hidden");



});







renderZoneTabs();



renderAll();







loadLiveZoneStatus(function(){



applyLiveZoneStatus(currentZone);



});



</script>







</body>



</html> 

