<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
 좌석·구역 현황 (관리자 콘솔). 강선구.

 [2026-10-06] 격자판 대신 안병찬의 이용자 주차맵(reservation/reservation2.jsp)을 그대로 가져왔다.
   - 구역 도형(ZONES)·칸 배치(build)·실시간 배지·탭·렌더 JS 는 reservation2.jsp 의 것을 손대지 않고 복사.
     같은 맵을 두 화면이 쓰므로, 도형을 고칠 일이 생기면 reservation2.jsp 를 고친 뒤 이 파일의
     JS 블록도 같이 갈아끼울 것 (ZONES ~ renderAll).
   - 관리자용으로 바꾼 것은 세 군데뿐:
       1) 좌석 데이터 : 예약 DAO 대신 ManagerDao.getSeatStatus() 결과(${seats})를 같은 모양으로 공급
       2) 구역 탭 클릭 : Reservation 이 아니라 Manager?t_gubun=seat&t_lot=.. 로 이동
       3) 칸 클릭      : 결제가 아니라, 예약이 걸린 칸이면 그 예약의 입·출차 화면으로 이동
   - 맵 스타일은 css/reservation.css 에만 있고 manager.css 와 겹치는 클래스가 없어 둘을 같이 링크한다.
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>좌석·구역 현황 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/reservation.css" rel="stylesheet">
<style>
	/* 관리자 레이아웃 안에서 맵이 넘치지 않게 */
	.adm_content .lot_map { max-width: 100%; }
	.adm_content .zone_tabs_wrap { margin: 0 0 14px; }
	/* 이용자 화면 전용 요소는 JS 가 참조만 하므로 숨겨 둔다 */
	.adm_hidden { display: none !important; }
	.adm_zone_info { display:flex; gap:18px; flex-wrap:wrap; font-size:14px; color:#555; margin:6px 0 10px; }
	.adm_zone_info strong { color:#1a5fd0; }
	.adm_zone_info #liveBadge { font-size:12px; color:#2a7d2a; }
</style>
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<%-- 구역 탭 : 안병찬 맵의 탭을 JS 가 그린다 (클릭 시 t_lot 으로 이동) --%>
		<div class="zone_tabs_wrap">
			<div class="zone_tabs" id="zoneTabs"></div>
		</div>

		<section class="card">
			<div class="card_head">
				<h2><span id="zoneTitle">${lot} 구역</span> · 좌석 ${fn:length(seats)}면</h2>
				<div class="legend">
					<span><i class="sw parking"></i>주차 중 ${parkingCnt}</span>
					<span><i class="sw reserved"></i>예약 ${reservedCnt}</span>
					<span><i class="sw free"></i>빈 좌석 ${freeCnt}</span>
				</div>
			</div>

			<%-- renderAll() 이 채우는 구역 요약. 이용자 화면의 상단 요약과 같은 id 를 쓴다 --%>
			<div class="adm_zone_info">
				<span id="zoneType"></span>
				<span>잔여 <strong id="zoneRemain">-</strong> / 전체 <strong id="zoneTotal">-</strong></span>
				<span>혼잡도 <strong id="zoneStatus">-</strong></span>
				<span id="liveBadge" style="display:none"></span>
			</div>

			<div class="seat_legend">
				<span><i class="legend_box legend_free"></i> 빈 자리</span>
				<span><i class="legend_box legend_taken"></i> 예약·주차 중</span>
				<span><i class="legend_box legend_disabled"></i> ♿ 장애인</span>
				<span><i class="legend_box legend_ev"></i> 🔌 전기차</span>
				<span><i class="legend_box legend_cancelled"></i> ✈️ 결항 재배정중</span>
			</div>

			<!-- 주차 상세맵 (안병찬 reservation2.jsp 와 동일 마크업) -->
			<div class="lot_map">
				<div class="lot_gate">
					<span class="gate_in">▲ 터미널 방향</span>
					<span id="lotMapZoneLabel">${lot} 구역 · 지상</span>
					<span class="gate_out">진출입로</span>
				</div>
				<svg id="lotSvg" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="구역 상세 주차맵">
					<image id="lotBaseImage" href="${pageContext.request.contextPath}/images/parking_map.png" x="0" y="0" width="1600" height="900"/>
					<path id="lotZoneOutline" class="lot_zone_outline"/>
					<g id="seatGrid"></g>
				</svg>
			</div>

			<p class="card_note" style="margin-top:14px">색이 있는 칸을 누르면 그 예약의 입·출차 화면으로 이동합니다. "예약"은 24시간 안에 시작하거나 아직 입차하지 않은 예약입니다.</p>

			<%-- 이용자 화면 전용 요소. 공통 JS 가 참조하므로 비워서 숨겨 둔다 --%>
			<span id="seatBoxZone" class="adm_hidden"></span>
			<span id="seatRemainCount" class="adm_hidden"></span>
			<span id="selectedSeatText" class="adm_hidden"></span>
			<button id="goPayBtn" type="button" class="adm_hidden" disabled></button>
		</section>

	</main>

<script>
// ── 관리자 좌석 데이터 → 안병찬 맵이 기대하는 모양으로 ─────────────────────────
// reservation2.jsp 는 예약 DAO 의 {seatNo, typeNm, status} 를 쓴다. 여기서는 ManagerDao.getSeatStatus()
// 의 행(seat_no / seat_type N·D·E / state free·reserved·parking)을 같은 이름·같은 값 체계로 바꿔 넣는다.
//   typeNm : D -> "장애인차", E -> "전기차", 그 외 "일반"     (makeSeats 가 "장애인차"/"전기차" 로 kind 를 정함)
//   status : reserved·parking -> "예약중", free -> ""        (makeSeats 가 "예약중" 을 taken 으로 봄)
var dbSeatList = [
<c:forEach var="s" items="${seats}" varStatus="st">
	{ seatNo: "${s.seat_no}",
	  typeNm: "${s.seat_type == 'D' ? '장애인차' : (s.seat_type == 'E' ? '전기차' : '일반')}",
	  status: "${s.state == 'free' ? '' : '예약중'}" }<c:if test="${!st.last}">,</c:if>
</c:forEach>
];
// 칸 클릭 → 입·출차 화면으로 가기 위한 관리자 전용 부가 정보
var adminSeatInfo = {};
<c:forEach var="s" items="${seats}">
	adminSeatInfo["${s.seat_no}"] = { rid: "${s.reservation_id}", who: "${fn:escapeXml(s.member_name)}", start: "${s.start_text}", state: "${s.state}" };
</c:forEach>

// ── 아래는 reservation2.jsp 의 맵 JS 를 그대로 복사한 것 (관리자용 패치: currentZone, 탭 이동 URL, 실시간 API 경로) ──
var ZONES = [

{ id:"P1", type:"장기주차장", price:2000, total:50, rows:4,

box:{x:810,y:350,w:368,h:165},

path:"M810 375 Q810 350 835 350 L1010 360 Q1040 370 1065 390 L1075 420 Q1090 430 1115 445 L1150 450 Q1170 460 1178 490 L1175 510 Q1165 515 1140 515 L845 515 Q810 515 810 515 Z" },



{ id:"P2", type:"장기주차장", price:2000, total:50, rows:4,

box:{x:390,y:355,w:375,h:160},

path:"M430 445 Q490 445 500 430 L510 420 Q520 390 535 375 L545 370 Q565 360 590 355 L760 355 Q765 355 765 355 L765 455 Q765 515 765 515 L420 515 Q390 515 395 505 Z" },



{ id:"P3", type:"장기주차장", price:3000, total:50, rows:2,

box:{x:820,y:545,w:355,h:95},

path:"M820 545 L1140 545 Q1175 560 1175 580 L1170 620 Q1140 640 1130 640 L820 640 Z" },



{ id:"P4", type:"장기주차장", price:3000, total:50, rows:2,

box:{x:400,y:550,w:370,h:85},

path:"M400 575 Q435 550 435 550 L755 550 Q770 550 770 575 L770 635 Q760 635 755 635 L435 635 Q410 630 410 630 Z" },



{ id:"P5", type:"장기주차장", price:3000, total:50, rows:2,

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



var currentZone = "${lot}" || "P1";

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



/* /ParkingStatus 응답 { longTerm:[{parkLotNo,totalCount,availableCount,congestion,floor,datetm}..], shortTerm:[{parkZoneNo,..}..] }
   을 applyLiveZoneStatus 가 쓰는 { P1:{remain,total,status,floor,datetm}, .. } 로 변환 (이용자 화면과 동일한 어댑터) */
function normalizeZoneStatus(raw){
	var out = {};
	function put(row, keyName){
		if (!row || !row[keyName]) return;
		out[row[keyName]] = { remain: row.availableCount, total: row.totalCount, status: row.congestion,
		                      floor: row.floor || "", datetm: row.datetm || "" };
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

document.getElementById("zoneTotal").textContent = live.total.toLocaleString() + "석";

document.getElementById("zoneStatus").textContent = live.status;



if (badgeEl){

badgeEl.style.display = "";

var badgeParts = ["실시간"]; if (live.floor) badgeParts.push(live.floor);
var badgeWhen = formatDatetm(live.datetm);
badgeEl.textContent = badgeParts.join(" · ") + (badgeWhen ? " (" + badgeWhen + " 기준)" : "");

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

location.href = "${pageContext.request.contextPath}/Manager?t_gubun=seat&t_lot=" + targetZone;

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



// ── 관리자용 칸 클릭 (원본의 예약 선택 루프 자리) : 예약이 걸린 칸이면 그 예약의 입·출차 화면으로
var seatEls = document.querySelectorAll(".bay_g");
for (var k = 0; k < seatEls.length; k++) {
	var __no = seatEls[k].getAttribute("data-seat");
	var __info = adminSeatInfo[__no];
	if (__info) seatEls[k].setAttribute("title", __no + (__info.rid ? " · " + __info.who + " " + __info.start : " · 빈 자리"));
	seatEls[k].addEventListener("click", function(){
		var i = adminSeatInfo[this.getAttribute("data-seat")];
		if (!i || !i.rid) return;
		location.href = "${pageContext.request.contextPath}/Manager?t_gubun=gate&t_reservation_id=" + encodeURIComponent(i.rid);
	});
}
document.getElementById("selectedSeatText").textContent = "선택된 자리가 없습니다.";

document.getElementById("goPayBtn").disabled = true;

}




// ── 초기화 (칸 클릭 처리는 renderAll() 안에서 관리자용으로 바뀌어 있다) ─────────────
renderZoneTabs();
renderAll();
loadLiveZoneStatus(function(){
	applyLiveZoneStatus(currentZone);
});
</script>
</body>
</html>
