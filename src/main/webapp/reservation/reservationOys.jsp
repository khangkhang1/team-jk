<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>주차맵 - 인천공항 주차예약</title>

<!-- 인덱스(index2.html)와 같은 디자인 시스템을 그대로 씀 - 헤더/푸터/컨테이너 스타일 재사용 -->
<link rel="stylesheet" href="css/index1.css">

<link rel="stylesheet" href="css/c.css">
<link rel="stylesheet" href="css/reservation.css">
<link rel="stylesheet" href="css/payment.css">
</head>

<body>
<div class="wrap detail_body_top">

	<!-- ============================================================
	     HEADER - 인덱스(index2.html)와 동일한 헤더를 그대로 씀.
	     나중에 공통 헤더(커먼 메뉴)로 빠지면 이 블록만 include로 교체하면 됨.
	     ============================================================ -->
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

	<!-- ============================================================
	     페이지 타이틀 밴드 - 지금 보고 있는 구역이 뭔지 명확히
	     ============================================================ -->
	<section class="zone_hero">
		<div class="zone_hero_inner">
			<div>
				<a href="index2.html#reserve" class="zone_back">← 전체 주차맵으로</a>
				<div class="zone_hero_eyebrow">INCHEON AIRPORT T1 PARKING</div>
				<h1>
					<span id="zoneTitle">P1 구역</span>
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
	     구역 탭 - 5차 회의에서 "층 개념은 안 쓰고 지상만 상정"으로 정했으므로
	     기존 1층/2층 탭을 없애고 P1~P9 구역 전환 탭으로 교체함.
	     (인덱스에서 P1 눌러 들어와도 여기서 바로 다른 구역으로 이동 가능 - 4차 회의 요청사항)
	     ============================================================ -->
	<div class="zone_tabs_wrap">
		<div class="zone_tabs" id="zoneTabs"></div>
	</div>

	<main class="main">
		<div class="container">
			<section class="detail_section">

				<!-- 이용 방식 -->
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

				<!-- 좌석 선택 -->
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

						<!-- ============================================================
						     주차 상세맵
						     인덱스(index2.html)가 쓰는 실제 주차장 도면(images/parking_map.png)을
						     그대로 쓰되, SVG viewBox를 그 구역의 좌표로 잘라서 "확대"한다.
						     즉 인덱스에서 보던 그 블록이 그대로 커져서 보이고,
						     주차 칸은 그 블록의 실제 모양 안에 배치된다.
						     ============================================================ -->
						<div class="lot_map">
							<div class="lot_gate">
								<span class="gate_in">▲ 터미널 방향</span>
								<span id="lotMapZoneLabel">P1 구역 · 지상</span>
								<span class="gate_out">진출입로</span>
							</div>
							<svg id="lotSvg" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="구역 상세 주차맵">
								<!-- 실제 도면 (viewBox로 이 구역만 확대해서 보임) -->
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

	<!-- FOOTER - 인덱스와 동일 -->
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
<!-- 결제 모듈 (담당: 오윤섭) — 이 블록은 껍데기 스타일만 인덱스 톤에 맞췄고 -->
<!-- 내용/기능은 원본 그대로. 완성되면 이 자리에 통째로 교체하면 됨.        -->
<!-- ============================================================ -->
<form name="pay">
<input type="hidden" name="t_gubun" value="payment">
	<div id="paymentModal" class="hidden">
		<div id="paymentModalInner">
			<button id="paymentCloseBtn">&times;</button>

			<h3 id="paymentSeatTitle">-</h3>
			<p id="paymentLotInfo">-</p>
			<p id="paymentPlanInfo">-</p>

<!-- Servlet으로 예약 유형 및 좌석 정보 넘기기 위한 input / 결제 시 DB에 저장 위함-->
<input type="text" id="reservationPlan" name="t_reservation_plan">
<input type="text" id="reservationSeat" name="t_reservation_seat">

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
					<input type="text" id="flightNoInput" placeholder="예: KE001" name="t_reservation_flight_no">
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
			<input type="text" name="t_reservation_estimate_amount" id="estimatedPriceInput">
			<div id="payMethodArea">
				<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="kakaoPay"> 카카오페이</label>
				<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="naverPay"> 네이버페이</label>
				<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="creditCard"> 카드</label>
				<label class="payOption"><input type="radio" name="t_reservation_pay_method" value="account"> 계좌이체</label>
			</div>

			<div id="paymentFooter">
				<div id="payBarPrice"><span data-i18n="res_depositLabel">예약금</span> <strong id="payBarAmount">-</strong>원</div>
<!-- Servlet으로 예약금 넘기기 위한 input / 예약 목록 확인 시 예약금 노출 / 필요 없는 경우 삭제 예정 -->
				<input type="hidden" name="t_reservation_deposit_amount">
				<button id="payBtn" data-i18n="res_payBtn" disabled>결제하기</button>
			</div>
		</div>
	</div>
</form>
<!-- 결제 모듈 END -->

<script>
/* ============================================================
   세부 주차맵 - 구역별 좌석 렌더링
   5차 회의 결정 반영:
     - 인천공항은 개별 주차칸 단위 API/지도가 없음 -> 좌석은 전부 임의 데이터
     - 실제 규모(1터미널 1.7만대)는 보여줄 의미가 없어서 구역당 30~40석으로 축소
     - 층 개념은 쓰지 않음(지상만) -> 층 탭 대신 구역 탭
   ============================================================ */

/* 인덱스(index2.html)의 P1~P9와 동일한 구역 구성.
   path / box 값은 인덱스 SVG(viewBox 1600x900)에서 그대로 가져온 실제 도면 좌표다.
   - path : 인덱스에서 클릭하던 그 블록 모양 그대로
   - box  : 그 블록의 실제 위치·크기 (여기 안에 주차 칸을 배치한다)
   - rows : 블록의 실제 가로세로 비율에 맞춘 주차 열 수
            (P1·P2는 두툼해서 4열, P3~P5는 납작해서 2열, 터미널 앞 P6~P9는 작아서 2열) */
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

		/* P6: 상단을 대칭 각도로 비스듬히 깎은 외곽선 적용 */
		{ id:"P6", type:"단기주차장", price:3000, total:50, rows:3, bayW:6.2,
		  box:{x:840, y:255, w:140, h:65},
 		 path:"M850 282 L980 258 L968 318 L840 318 Z" },

		/* P7: 대칭 구조로 3개 레이어 정렬되도록 box 및 path 확장 */
		{ id:"P7", type:"단기주차장", price:3000, total:50, rows:3, bayW:6.2,
  		box:{x:600, y:255, w:140, h:65},
  		path:"M600 258 L730 282 L740 318 L612 318 Z" },
  		{ id:"P8", type:"단기주차장", price:3000, total:50, rows:2,
  		  box:{x:860, y:195, w:125, h:45},
  		  path:"M860 195 L985 195 L985 240 L860 240 Z" },

  		/* P9: P8과 동일한 직사각형 구조로 통일 */
  		{ id:"P9", type:"단기주차장", price:3000, total:50, rows:2,
  		  box:{x:600, y:195, w:125, h:45},
  		  path:"M600 195 L725 195 L725 240 L600 240 Z" }
	];


var currentZone = getZoneFromUrl() || "P1";
var selectedSeat = null;

// 인덱스에서 넘어올 때 ?zone=P1 형태로 받는다 (인덱스의 reserveParking()에서 이 주소로 이동시키면 됨)
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

/* 좌석 임의 생성 - 같은 구역이면 항상 같은 배치가 나오도록 구역 id를 시드로 씀
   (새로고침할 때마다 자리가 바뀌면 테스트가 어려워서) */
function makeSeats(zone, count){
	var seats = [];
	var seed = zone.id.charCodeAt(1);
	var n_total = count || zone.total;
	for (var i=1;i<=n_total;i++){
		var n = (i*7 + seed*13) % 100;
		var state = "free";
		if (n < 34)      state = "taken";      // 약 1/3은 이미 예약됨
		else if (n < 40) state = "cancelled";  // 결항 재배정중
		var kind = "normal";
		if (i % 12 === 0)     kind = "disabled"; // 장애인 구역
		else if (i % 9 === 0) kind = "ev";       // 전기차 충전 구역
		seats.push({ no: zone.id + "-" + String(i).padStart(2,"0"), state: state, kind: kind });
	}
	return seats;
}

/* ============================================================
   구역 블록의 실제 모양(box) 안에 주차 칸을 배치한다.
   실제 주차장처럼 "두 열이 등을 맞대고, 그 사이/바깥에 주행로"가 되도록
   열(row) 사이에 통로 간격을 준다.
   ============================================================ */
function layoutBays(zone, outlineEl, svgEl){
	/* 블록의 실제 윤곽을 따라가는 배치.
	   바운딩 박스에 격자를 깔고 깎아내면 가장자리가 너덜너덜해져서,
	   대신 "가로 띠(열)마다 블록의 실제 폭을 스캔해서 그 폭을 칸으로 채우는" 방식을 쓴다.
	   -> 블록이 좁아지거나 사선이어도 칸이 윤곽을 따라 딱 맞게 들어간다.
	   칸 비율은 실제 주차 칸처럼 세로로 길쭉하게(폭 2.5m x 깊이 5m 기준) 잡는다. */

	function inside(x, y){
		var p = svgEl.createSVGPoint();
		p.x = x; p.y = y;
		return outlineEl.isPointInFill(p);
	}

	// 주어진 y 높이대에서 블록이 채워진 가로 구간(x 시작~끝)을 찾는다.
	// 가장자리에서 칸이 삐져나오지 않게 양끝을 살짝 안쪽으로 물린다.
	function spanAt(yTop, yBottom){
		var step = zone.box.w / 300;
		var start = null, end = null;
		for (var x = zone.box.x; x <= zone.box.x + zone.box.w; x += step){
			if (inside(x, yTop) && inside(x, yBottom) && inside(x, (yTop+yBottom)/2)){
				if (start === null) start = x;
				end = x;
			} else if (start !== null && end !== null && (x - end) > step*2){
				break;  // 첫 덩어리만 사용(구멍 건너뛰지 않음)
			}
		}
		if (start === null) return null;
		var inset = step * 1.5;
		if ((end - start) <= inset*2) return null;
		return { x1:start + inset, x2:end - inset };
	}

	/* 한 가지 배치(열 수, 칸 폭)로 실제 배치를 만들어보는 함수.
	   블록이 사선/쐐기 모양이라 열 수에 따라 들어가는 칸 수가 크게 달라진다. */
	function build(rows, bayW){
		var laneCount = Math.max(1, Math.floor(rows / 2));
		var laneH     = zone.box.h * (rows > 2 ? 0.10 : 0.16);
		var bayH      = (zone.box.h - laneH*laneCount - zone.box.h*0.06) / rows;
		if (bayH <= 0.5) return null;

		var res = { bays:[], lanes:[], bayW:bayW, bayH:bayH, rows:rows };
		var y = zone.box.y + zone.box.h*0.03;

		// 모든 줄이 같은 격자 위에 놓이도록 기준선을 하나 잡는다
		// (줄마다 따로 가운데정렬하면 세로 열이 어긋나 보임)
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
					// 네 모서리가 전부 블록 안에 있는 칸만 채택 (사선 구간에서 삐져나오는 것 방지)
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

	/* 열 수와 칸 폭 조합을 훑어서
	   ① 칸 수가 목표(zone.total)에 가깝고
	   ② 칸 비율이 실제 주차칸(폭:깊이 = 1:2, 즉 0.5)에 가까운
	   배치를 자동으로 고른다. 손으로 값 맞추지 않아도 구역 모양에 맞게 결정됨. */
	var TARGET_RATIO = 0.5;
	var best = null, bestScore = Infinity;
	for (var rows = 2; rows <= 6; rows++){
		for (var step = 0; step <= 40; step++){
			var bw = zone.box.h * (0.06 + step*0.012);
			var cand = build(rows, bw);
			if (!cand || !cand.bays.length) continue;
			var ratio = cand.bayW / cand.bayH;
			if (ratio < 0.3 || ratio > 0.75) continue;          // 너무 얇거나 뚱뚱한 칸은 후보 제외
			var score = Math.abs(cand.bays.length - zone.total)
			          + Math.abs(ratio - TARGET_RATIO) * 22;    // 비율 어긋남에 가중치
			if (score < bestScore){ bestScore = score; best = cand; }
		}
	}
	return best || { bays:[], lanes:[] };
}

/* ============================================================
   실시간 구역 주차 현황 (공공데이터 StatusOfParking)
   - 서버(/Parking?t_gubun=zoneStatus)를 통해 받는다. 프론트에서 data.go.kr을 직접 부르지 않음
     (서비스키 노출 방지 - 노션 '시스템 아키텍처'의 외부 API 처리 원칙).
   - 실데이터가 있는 구역: P1, P2, P3, P5
     (P4는 2026-07 폐지, P6~P9는 실제 인천공항에 없는 구역이라 임의 데이터 유지)
   - 정적 HTML로 열었을 때(서버 없이 미리보기)는 조용히 실패하고 임의 데이터를 그대로 둔다.
   ============================================================ */
var LIVE_ZONE_STATUS = null;   // 한 번 받아서 캐시 (구역 탭 전환마다 재호출하지 않음)

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
				LIVE_ZONE_STATUS = {};   // 서버 없이 열었을 때 등 - 임의 데이터로 진행
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
		// 실데이터 없는 구역(P4, P6~P9)은 임의 데이터 그대로 두고 배지도 숨긴다.
		// (숨기지 않으면 직전 구역의 배지가 남아 실시간인 것처럼 보임)
		if (badgeEl) badgeEl.style.display = "none";
		return;
	}

	document.getElementById("zoneRemain").textContent = live.remain.toLocaleString() + "석";
	document.getElementById("zoneTotal").textContent  = live.total.toLocaleString() + "석";
	document.getElementById("zoneStatus").textContent = live.status;

	// 실시간 값임을 화면에 표시 (발표 때 "실제 API 연동"임을 보여주는 근거)
	if (badgeEl){
		badgeEl.style.display = "";
		badgeEl.textContent = "실시간 · " + live.floor + " (" + formatDatetm(live.datetm) + " 기준)";
	}
}

function formatDatetm(s){
	// 20260908112732.000 -> 09-08 11:27
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
			// 주소도 같이 바꿔줘서 새로고침/공유해도 같은 구역이 열리게
			history.replaceState(null, "", "?zone=" + currentZone);
			renderAll();
		});
	}
}

function renderAll(){
	var zone = findZone(currentZone);
	var seats = makeSeats(zone);

	// 타이틀 영역
	document.getElementById("zoneTitle").textContent = zone.id + " 구역";
	document.getElementById("zoneType").textContent  = zone.type + " · 시간당 " + zone.price.toLocaleString() + "원";
	document.getElementById("seatBoxZone").textContent = zone.id;

	var remain = 0;
	for (var i=0;i<seats.length;i++){
		if (seats[i].state === "free") remain++;
	}
	document.getElementById("zoneRemain").textContent = remain + "석";
	document.getElementById("zoneTotal").textContent  = zone.total + "석";
	document.getElementById("seatRemainCount").textContent = remain;

	var ratio = remain / zone.total;
	var statusEl = document.getElementById("zoneStatus");
	statusEl.textContent = ratio > 0.5 ? "여유" : (ratio > 0.2 ? "보통" : "혼잡");

	// 탭 active 갱신
	var tabs = document.querySelectorAll(".zone_tab");
	for (var t=0;t<tabs.length;t++){
		tabs[t].classList.toggle("active", tabs[t].getAttribute("data-zone") === currentZone);
	}

	// ── 주차 상세맵 렌더링 ──────────────────────────────
	// 인덱스가 쓰는 실제 도면(parking_map.png)을 viewBox로 이 구역만 잘라서 확대하고,
	// 그 블록의 실제 모양 안에 주차 칸을 배치한다.
	document.getElementById("lotMapZoneLabel").textContent = zone.id + " 구역 · 지상";

	var pad = Math.max(zone.box.w, zone.box.h) * 0.15;
	document.getElementById("lotSvg").setAttribute("viewBox",
		(zone.box.x - pad) + " " + (zone.box.y - pad) + " " +
		(zone.box.w + pad*2) + " " + (zone.box.h + pad*2));

	var outlineEl = document.getElementById("lotZoneOutline");
	outlineEl.setAttribute("d", zone.path);
	
	// 블록 실제 모양 안에 들어가는 칸만 배치 -> 배치된 칸 수에 맞춰 좌석 데이터를 다시 맞춘다
	var layout = layoutBays(zone, outlineEl, document.getElementById("lotSvg"));
	seats = makeSeats(zone, layout.bays.length);
	for (var q=0; q<layout.bays.length; q++){
		layout.bays[q].seat = seats[q];
	}

	// 잔여/전체 표시도 실제 배치된 칸 기준으로 갱신
	remain = 0;
	for (var rr=0; rr<seats.length; rr++){
		if (seats[rr].state === "free") remain++;
	}
	document.getElementById("zoneRemain").textContent = remain + "석";
	document.getElementById("zoneTotal").textContent  = seats.length + "석";
	document.getElementById("seatRemainCount").textContent = remain;
	ratio = seats.length ? remain / seats.length : 0;
	statusEl.textContent = ratio > 0.5 ? "여유" : (ratio > 0.2 ? "보통" : "혼잡");

	// 실시간 주차 현황 API(구역 단위)가 있는 구역이면 상단 요약을 실데이터로 덮어쓴다.
	// 개별 칸(좌석 맵)은 그대로 우리 임의 데이터를 쓴다 - 실제 개별 주차면은 T1만 4,614면이라
	// 전부 DB에 넣는 게 불가능해서, 구역 잔여대수만 실연동하기로 함(2026-09-08).
	applyLiveZoneStatus(zone.id);

	var svg = "";

	// 주행로(점선)
	for (var l=0; l<layout.lanes.length; l++){
		var lane = layout.lanes[l];
		svg += '<line class="lot_lane" x1="' + lane.x1 + '" y1="' + lane.y + '" x2="' + lane.x2 + '" y2="' + lane.y + '"/>';
	}

	// 주차 칸
	for (var b=0; b<layout.bays.length; b++){
		var it = layout.bays[b];
		var seat = it.seat;
		var cls = "bay_g";
		if (seat.state === "taken")     cls += " taken";
		if (seat.state === "cancelled") cls += " cancelled";
		if (seat.kind === "disabled")   cls += " disabled_seat";
		if (seat.kind === "ev")         cls += " ev_seat";

		var label = seat.no.split("-")[1];
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
			document.getElementById("selectedSeatText").innerHTML =
				"선택한 자리 : <strong>" + selectedSeat + "</strong>";
			document.getElementById("goPayBtn").disabled = false;
		});
	}

	document.getElementById("selectedSeatText").textContent = "선택된 자리가 없습니다.";
	document.getElementById("goPayBtn").disabled = true;
}

// 
/* 이용 방식 카드 선택 시 hidden input(t_reservation_plan) 업데이트 */
var planCards = document.querySelectorAll(".plan_card");
for (var p = 0; p < planCards.length; p++) {
    planCards[p].addEventListener("click", function() {
        for (var q = 0; q < planCards.length; q++) {
            planCards[q].classList.remove("selected");
        }
        this.classList.add("selected");
        
        var selectedRadio = this.querySelector("input[name='planType']");
        if (selectedRadio) {
            selectedRadio.checked = true;
            
            // 1. 선택된 이용 방식 값(1 또는 2) 추출
            var planVal = selectedRadio.value;
            
            // 2. 결제 폼 내 hidden input에 대입
            var planInput = document.getElementById("reservationPlan");
            if (planInput) {
                planInput.value = planVal;
            }

            // UI 보이기/숨기기 처리
            var isPlan1 = (planVal === "1");
            document.getElementById("flightFieldset").classList.toggle("hidden", !isPlan1);
            document.getElementById("durationRow").classList.toggle("hidden", !isPlan1);
            document.getElementById("endFreeNotice").classList.toggle("hidden", isPlan1);
        }
    });
}

/* 결제 모달 열기 이벤트 (payment.js의 openPaymentModal 호출) */
document.getElementById("goPayBtn").addEventListener("click", function() {
    if (!selectedSeat) return;
    
    // 현재 선택된 이용 방식 값('1' 또는 '2') 추출
    var currentPlan = "1";
    var selectedRadio = document.querySelector("input[name='planType']:checked");
    if (selectedRadio) {
        currentPlan = selectedRadio.value;
    }
    
    // hidden input에 기본값 세팅
    var planInput = document.getElementById("reservationPlan");
    if (planInput) planInput.value = currentPlan;
    
    var seatInput = document.getElementById("reservationSeat");
    if (seatInput) seatInput.value = selectedSeat;
    
    // payment.js에 정의된 전역 모달 열기 함수 호출 (이 안에서 시간 옵션 및 날짜 자동 생성됨)
    if (typeof window.openPaymentModal === "function") {
        window.openPaymentModal(selectedSeat, currentPlan);
    } else {
        // payment.js가 로드되지 않았을 경우 예외 처리
        document.getElementById("paymentModal").classList.remove("hidden");
    }
});

document.getElementById("paymentCloseBtn").addEventListener("click", function() {
    document.getElementById("paymentModal").classList.add("hidden");
});

renderZoneTabs();
renderAll();

// 실시간 구역 현황을 받아온 뒤 상단 요약만 실데이터로 갱신 (좌석 맵은 그대로)
loadLiveZoneStatus(function(){
	applyLiveZoneStatus(currentZone);
});
</script>
<script src="js/payment.js"></script>
</body>
</html>
