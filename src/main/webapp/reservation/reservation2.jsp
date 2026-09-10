<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>주차맵 - 인천공항 주차예약</title>

<link rel="stylesheet" href="css/index1.css">
<link rel="stylesheet" href="css/c.css">
<link rel="stylesheet" href="css/reservation.css">
<link rel="stylesheet" href="css/payment.css">
</head>

<body>
<div class="wrap detail_body_top">

	<header class="header scrolled">
		<div class="header_inner">
			<a href="index2.html" class="logo">
				인천공항 주차예약
				<small>INCHEON AIRPORT PARKING</small>
			</a>

			<nav class="header_menu">
				<li>
					<a href="index2.html#parking">교통 · 주차</a>
					<div class="header_dropdown">
						<a href="index2.html#guide">주차장 이용 안내</a>
						<a href="index2.html#parking">주차 요금</a>
						<a href="index2.html#parking">주차장 혼잡도</a>
					</div>
				</li>
				<li>
					<a href="index2.html#reserve" class="active">주차 예약 조회</a>
					<div class="header_dropdown">
						<a href="#">예약 내역</a>
						<a href="#">예약 확인</a>
						<a href="#">예약 취소</a>
						<a href="#">이용 내역</a>
					</div>
				</li>
				<li>
					<a href="index2.html#notice">공지 사항</a>
					<div class="header_dropdown">
						<a href="#">공지 사항</a>
						<a href="#">자주 하는 질문</a>
					</div>
				</li>
			</nav>

			<div class="header_right">
				<a href="login.html">로그인</a>
				<span>|</span>
				<a href="login.html">회원가입</a>
			</div>

			<button class="menu_btn" aria-label="메뉴">☰</button>
		</div>
	</header>

	<section class="zone_hero">
		<div class="zone_hero_inner">
			<div>
				<a href="index2.html#reserve" class="zone_back">← 전체 주차맵으로</a>
				<div class="zone_hero_eyebrow">INCHEON AIRPORT T1 PARKING</div>
				<h1>
					<span id="zoneTitle">P1 구역</span>
					<small id="zoneType">단기주차장 · 시간당 3,000원</small>
				</h1>
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

	<div class="zone_tabs_wrap">
		<div class="zone_tabs" id="zoneTabs"></div>
	</div>

	<main class="main">
		<div class="container">
			<section class="detail_section">

				<div class="detail_box">
					<div class="detail_box_head">
						<div>
							<h3>이용 방식 선택</h3>
							<p>먼저 이용 방식을 고르면, 그 방식으로 이용 가능한 자리만 활성화됩니다.</p>
						</div>
					</div>
					<div class="detail_box_body">
						<div class="plan_cards">
							<label class="plan_card selected">
								<input type="radio" name="planType" value="1" checked>
								<span class="plan_card_title">예약형 (1안)</span>
								<span class="plan_card_desc">시작·종료 시각을 미리 정합니다. 왕복 항공권 정보 입력 필수. 기본 요금.</span>
							</label>
							<label class="plan_card">
								<input type="radio" name="planType" value="2">
								<span class="plan_card_title">자유출차형 (2안)</span>
								<span class="plan_card_desc">시작 시각만 정하고 종료는 자유입니다. 장기주차 구역 전용, 페널티 요금 적용.</span>
							</label>
						</div>
					</div>
				</div>

				<div class="detail_box">
					<div class="detail_box_head">
						<div>
							<h3><span id="seatBoxZone">P1</span> 구역 좌석</h3>
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
								<span id="lotMapZoneLabel">P1 구역 · 지상</span>
								<span class="gate_out">진출입로</span>
							</div>
							<svg id="lotSvg" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="구역 상세 주차맵">
								<image id="lotBaseImage" href="images/parking_map.png" x="0" y="0" width="1600" height="900"/>
								<path id="lotZoneOutline" class="lot_zone_outline"/>
								<g id="seatGrid"></g>
							</svg>
						</div>
					</div>

					<!-- 다음 페이지/결제 모달 전달용 Hidden 필드 -->
					<form id="reservationForm">
						<input type="hidden" id="hiddenZoneId" name="zoneId" value="">
						<input type="hidden" id="hiddenSeatNo" name="seatNo" value="">
						
						<div class="seat_bottom">
							<span id="selectedSeatText">선택된 자리가 없습니다.</span>
							<button class="go_pay_btn" id="goPayBtn" type="button" disabled>결제 진행</button>
						</div>
					</form>
				</div>

			</section>
		</div>
	</main>

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

<!-- ============================================================ -->
<!-- 결제 모듈 (담당 원본 100% 유지)                             -->
<!-- ============================================================ -->
<div class="pay_modal hidden" id="paymentModal">
	<div class="pay_modal_inner">
		<button class="pay_close" id="paymentCloseBtn">&times;</button>

		<h3 id="paymentSeatTitle">-</h3>
		<p class="pay_sub" id="paymentLotInfo">-</p>
		<p class="pay_sub" id="paymentPlanInfo">-</p>

		<div class="form_row">
			<label>날짜</label>
			<input type="date" id="dateInput">
		</div>
		<div class="form_row">
			<label>시작 시각</label>
			<select id="startTimeInput"></select>
		</div>

		<div class="form_row plan1Only" id="durationRow">
			<label>이용 시간</label>
			<select id="durationInput">
				<option value="1">1시간</option>
				<option value="2" selected>2시간</option>
				<option value="3">3시간</option>
				<option value="4">4시간</option>
				<option value="6">6시간</option>
			</select>
		</div>

		<div class="plan2Only hidden" id="endFreeNotice">
			<p class="pay_sub" style="margin-top:12px">종료 시각은 정하지 않습니다 (자유출차, 페널티 요금 적용)</p>
		</div>

		<fieldset class="pay_fieldset plan1Only" id="flightFieldset">
			<legend>✈️ 항공권 정보 (필수)</legend>
			<div class="form_row">
				<label>항공편명</label>
				<input type="text" id="flightNoInput" placeholder="예: KE001" autocomplete="off">
			</div>
			<div class="form_row">
				<label>왕복 여부</label>
				<select id="flightRoundtripInput">
					<option value="round">왕복</option>
					<option value="oneway">편도 (이용 불가)</option>
				</select>
			</div>
			<div class="form_row">
				<label>귀국 도착 예정</label>
				<input type="time" id="flightArriveInput">
			</div>
		</fieldset>

		<div class="price_box">예상 금액 <strong id="estimatedPrice">-</strong></div>

		<div class="pay_methods">
			<label class="pay_option"><input type="radio" name="payMethod" value="kakao"> 카카오페이</label>
			<label class="pay_option"><input type="radio" name="payMethod" value="naver"> 네이버페이</label>
			<label class="pay_option"><input type="radio" name="payMethod" value="card"> 카드</label>
			<label class="pay_option"><input type="radio" name="payMethod" value="account"> 계좌이체</label>
		</div>

		<div class="pay_footer">
			<div class="pay_amount">예약금 <strong id="payBarAmount">-</strong>원</div>
			<button class="pay_submit" id="payBtn" disabled>결제하기</button>
		</div>
	</div>
</div>

<script>
/* DB/시간 범위 조회 데이터 매핑 */
var SERVER_SEAT_LIST = [
	<c:forEach var="dto" items="${seatList}" varStatus="status">
		{
			no: "${dto.seatNo}",
			state: "${dto.state}", // 'free', 'taken', 'cancelled' (시간 비교 SQL 처리 결과)
			kind: "${dto.seatType == 'D' ? 'disabled' : (dto.seatType == 'E' ? 'ev' : 'normal')}"
		}${!status.last ? ',' : ''}
	</c:forEach>
];

var ZONES = [
	{ id:"P1", type:"장기주차장", price:2000, total:50, rows:4, box:{x:810,y:350,w:368,h:165}, path:"M810 375 Q810 350 835 350 L1010 360 Q1040 370 1065 390 L1075 420 Q1090 430 1115 445 L1150 450 Q1170 460 1178 490 L1175 510 Q1165 515 1140 515 L845 515 Q810 515 810 515 Z" },
	{ id:"P2", type:"장기주차장", price:2000, total:50, rows:4, box:{x:390,y:355,w:375,h:160}, path:"M430 445 Q490 445 500 430 L510 420 Q520 390 535 375 L545 370 Q565 360 590 355 L760 355 Q765 355 765 355 L765 455 Q765 515 765 515 L420 515 Q390 515 395 505 Z" },
	{ id:"P3", type:"단기주차장", price:3000, total:50, rows:2, box:{x:820,y:545,w:355,h:95}, path:"M820 545 L1140 545 Q1175 560 1175 580 L1170 620 Q1140 640 1130 640 L820 640 Z" },
	{ id:"P4", type:"단기주차장", price:3000, total:50, rows:2, box:{x:400,y:550,w:370,h:85}, path:"M400 575 Q435 550 435 550 L755 550 Q770 550 770 575 L770 635 Q760 635 755 635 L435 635 Q410 630 410 630 Z" },
	{ id:"P5", type:"단기주차장", price:3000, total:50, rows:2, box:{x:440,y:690,w:315,h:90}, path:"M440 690 Q460 690 475 690 L735 690 Q755 690 755 690 L755 765 Q755 780 755 780 L475 780 Q440 780 440 780 Z" },
	{ id:"P6", type:"단기주차장", price:3000, total:50, rows:3, bayW:6.2, box:{x:840, y:255, w:140, h:65}, path:"M850 282 L980 258 L968 318 L840 318 Z" },
	{ id:"P7", type:"단기주차장", price:3000, total:50, rows:3, bayW:6.2, box:{x:600, y:255, w:140, h:65}, path:"M600 258 L730 282 L740 318 L612 318 Z" },
	{ id:"P8", type:"단기주차장", price:3000, total:50, rows:2, box:{x:860, y:195, w:125, h:45}, path:"M860 195 L985 195 L985 240 L860 240 Z" },
	{ id:"P9", type:"단기주차장", price:3000, total:50, rows:2, box:{x:600, y:195, w:125, h:45}, path:"M600 195 L725 195 L725 240 L600 240 Z" }
];

var currentZone = getZoneFromUrl() || "P1";
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

/* 이전 작업자의 SVG 스캔 알고리즘 100% 보존 */
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
			currentZone = this.getAttribute("data-zone");
			selectedSeat = null;
			history.replaceState(null, "", "?zone=" + currentZone);
			renderAll();
		});
	}
}

function renderAll(){
	var zone = findZone(currentZone);

	document.getElementById("zoneTitle").textContent = zone.id + " 구역";
	document.getElementById("zoneType").textContent  = zone.type + " · 시간당 " + zone.price.toLocaleString() + "원";
	document.getElementById("seatBoxZone").textContent = zone.id;
	document.getElementById("hiddenZoneId").value = zone.id;

	document.getElementById("lotMapZoneLabel").textContent = zone.id + " 구역 · 지상";

	var pad = Math.max(zone.box.w, zone.box.h) * 0.15;
	document.getElementById("lotSvg").setAttribute("viewBox",
		(zone.box.x - pad) + " " + (zone.box.y - pad) + " " +
		(zone.box.w + pad*2) + " " + (zone.box.h + pad*2));

	var outlineEl = document.getElementById("lotZoneOutline");
	outlineEl.setAttribute("d", zone.path);
	
	// 스캔 계산된 좌표에 DB 데이터 매핑
	var layout = layoutBays(zone, outlineEl, document.getElementById("lotSvg"));
	for (var q=0; q<layout.bays.length; q++){
		if (SERVER_SEAT_LIST[q]) {
			layout.bays[q].seat = SERVER_SEAT_LIST[q];
		} else {
			layout.bays[q].seat = { 
				no: zone.id + "-" + String(q + 1).padStart(2, "0"), 
				state: "free", 
				kind: "normal" 
			};
		}
	}

	var remain = 0;
	for (var rr=0; rr<layout.bays.length; rr++){
		if (layout.bays[rr].seat.state === "free") remain++;
	}
	document.getElementById("zoneRemain").textContent = remain + "석";
	document.getElementById("zoneTotal").textContent  = layout.bays.length + "석";
	document.getElementById("seatRemainCount").textContent = remain;
	var ratio = layout.bays.length ? remain / layout.bays.length : 0;
	
	var statusEl = document.getElementById("zoneStatus");
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
		var cls = "bay_g";
		if (seat.state === "taken")     cls += " taken";
		if (seat.state === "cancelled") cls += " cancelled";
		if (seat.kind === "disabled")   cls += " disabled_seat";
		if (seat.kind === "ev")         cls += " ev_seat";

		var label = seat.no.split("-")[1] || seat.no;
		if (seat.kind === "disabled")      label = "♿";
		else if (seat.kind === "ev")       label = "⚡";
		else if (seat.state === "cancelled") label = "✈";

		svg += '<g class="' + cls + '" data-seat="' + seat.no + '" data-state="' + seat.state + '">'
			 + '<rect class="bay" x="' + it.x.toFixed(1) + '" y="' + it.y.toFixed(1) + '" width="' + it.w.toFixed(1) + '" height="' + it.h.toFixed(1) + '" rx="1"/>'
			 + '<text class="bay_label" x="' + (it.x + it.w/2).toFixed(1) + '" y="' + (it.y + it.h/2).toFixed(1) + '">' + label + '</text>'
			 + '</g>';
	}
	document.getElementById("seatGrid").innerHTML = svg;

	var seatEls = document.querySelectorAll(".bay_g");
	for (var k=0;k<seatEls.length;k++){
		seatEls[k].addEventListener("click", function(){
			if (this.getAttribute("data-state") !== "free") return;
			var prev = document.querySelector(".bay_g.selected");
			if (prev) prev.classList.remove("selected");
			this.classList.add("selected");
			
			selectedSeat = this.getAttribute("data-seat");
			
			// Hidden 태그 및 화면 업데이트
			document.getElementById("hiddenSeatNo").value = selectedSeat;
			document.getElementById("selectedSeatText").innerHTML = "선택한 자리 : <strong>" + selectedSeat + "</strong>";
			document.getElementById("goPayBtn").disabled = false;
		});
	}

	document.getElementById("selectedSeatText").textContent = "선택된 자리가 없습니다.";
	document.getElementById("hiddenSeatNo").value = "";
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

/* 결제 모달 열기 이벤트 연동 */
document.getElementById("goPayBtn").addEventListener("click", function(){
	if (!selectedSeat) return;
	var zone = findZone(currentZone);
	document.getElementById("paymentSeatTitle").textContent = selectedSeat + " 자리 예약";
	document.getElementById("paymentLotInfo").textContent   = "인천공항 1터미널 " + zone.id + " 구역 · " + zone.type;
	document.getElementById("paymentPlanInfo").textContent  = "시간당 " + zone.price.toLocaleString() + "원";
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