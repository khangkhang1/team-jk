<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- 좌석·구역 현황. 구역 탭(t_lot)을 고르면 그 구역 좌석 전부를 상태별 색으로 보여준다 --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>좌석·구역 현황 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<%-- 구역 탭 : 구역명 + 사용/전체 --%>
		<section class="tabs">
			<c:forEach var="l" items="${lots}">
				<a href="${ctx}/Manager?t_gubun=seat&t_lot=${l.lot_id}" class="${l.lot_id == lot ? 'on' : ''}">
					<strong>${l.lot_id}</strong><span>${l.used_cnt}/${l.total_cnt}</span>
				</a>
			</c:forEach>
		</section>

		<section class="card">
			<div class="card_head">
				<h2>${lot} 구역 · 좌석 ${fn:length(seats)}면</h2>
				<div class="legend">
					<span><i class="sw parking"></i>주차 중 ${parkingCnt}</span>
					<span><i class="sw reserved"></i>예약 ${reservedCnt}</span>
					<span><i class="sw free"></i>빈 좌석 ${freeCnt}</span>
					<span class="dim">♿ 장애인 · ⚡ 전기차</span>
				</div>
			</div>

			<div class="seat_grid">
			<c:forEach var="s" items="${seats}">
				<c:set var="tip" value="${s.seat_no} · ${s.seat_type_label}${empty s.reservation_id ? '' : ' · '}${s.member_name} ${s.start_text}" />
				<c:choose>
					<c:when test="${empty s.reservation_id}">
						<div class="seat ${s.state}" title="${tip}">
							<span class="seat_no">${fn:substringAfter(s.seat_no, '-')}</span>
							<c:if test="${s.seat_type == 'D'}"><span class="seat_ic">♿</span></c:if>
							<c:if test="${s.seat_type == 'E'}"><span class="seat_ic">⚡</span></c:if>
						</div>
					</c:when>
					<c:otherwise>
						<a class="seat ${s.state}" title="${tip}" href="${ctx}/Manager?t_gubun=gate&t_reservation_id=${s.reservation_id}">
							<span class="seat_no">${fn:substringAfter(s.seat_no, '-')}</span>
							<c:if test="${s.seat_type == 'D'}"><span class="seat_ic">♿</span></c:if>
							<c:if test="${s.seat_type == 'E'}"><span class="seat_ic">⚡</span></c:if>
						</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			</div>
			<p class="card_note" style="margin-top:14px">색이 있는 좌석을 누르면 그 예약의 입·출차 화면으로 이동합니다. "예약"은 24시간 안에 시작하거나 아직 입차하지 않은 예약입니다.</p>
		</section>

	</main>
</div>

</body>
</html>
