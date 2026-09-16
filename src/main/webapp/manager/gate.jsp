<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
 입·출차 처리. 차단기·번호판 인식 대신 관리자가 예약번호로 입차/출차를 확인한다 (하드웨어 대체 설계).
 Manager 서블릿이 todayList / rid / view / payments / fee* 를 넘긴다.
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>입·출차 처리 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/manager.js"></script>
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<section class="grid_1_2">

			<%-- 왼쪽 : 예약번호 입력 + 오늘 처리 대상 --%>
			<div class="card">
				<div class="card_head"><h2>예약번호 조회</h2></div>
				<form method="get" action="${ctx}/Manager" class="adm_form">
					<input type="hidden" name="t_gubun" value="gate">
					<input type="text" name="t_reservation_id" value="<c:out value='${rid}'/>" placeholder="예) R26-09-0010" autofocus>
					<button type="submit" class="adm_btn">조회</button>
				</form>

				<div class="card_head" style="margin-top:22px"><h2>오늘 처리 대상</h2><span class="card_note">주차 중 + 미입차</span></div>
				<c:if test="${empty todayList}"><p class="empty">처리할 예약이 없습니다.</p></c:if>
				<c:if test="${not empty todayList}">
				<table class="tbl tbl_click">
					<thead><tr><th>예약번호</th><th>회원</th><th>좌석</th><th>상태</th><th>시작</th></tr></thead>
					<tbody>
					<c:forEach var="t" items="${todayList}">
						<tr onclick="location.href='${ctx}/Manager?t_gubun=gate&t_reservation_id=${t.reservation_id}'" class="${t.reservation_id == rid ? 'sel' : ''}">
							<td class="mono">${t.reservation_id}</td>
							<td>${t.member_name}</td>
							<td>${t.seat_no}</td>
							<td><span class="badge st${t.status}">${t.status_label}</span></td>
							<td>${t.start_text}</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>
			</div>

			<%-- 오른쪽 : 예약 상세 + 처리 --%>
			<div class="card">
				<c:if test="${empty rid}">
					<div class="card_head"><h2>예약 상세</h2></div>
					<p class="empty">왼쪽에서 예약번호를 입력하거나 목록에서 선택하세요.</p>
				</c:if>

				<c:if test="${notFound}">
					<div class="card_head"><h2>예약 상세</h2></div>
					<p class="empty">"<c:out value="${rid}"/>" 예약번호가 없습니다.</p>
				</c:if>

				<c:if test="${not empty view}">
					<div class="card_head">
						<h2>${view.reservation_id} <span class="badge st${view.status}">${view.status_label}</span></h2>
						<a class="card_link" href="${ctx}/Manager?t_gubun=reservation&t_select=reservation_id&t_search=${view.reservation_id}">예약 관리에서 보기</a>
					</div>

					<table class="dl">
						<tr><th>회원</th><td>${view.member_name} <span class="dim">(${view.member_id})</span> · ${view.phone_number} · 차량 ${view.vehicle_number}</td></tr>
						<tr><th>좌석</th><td><strong>${view.seat_no}</strong> · ${view.lot_id} 구역 · ${view.seat_type_label}</td></tr>
						<tr><th>이용 방식</th><td>${view.type_label} <span class="dim">(시간당 <fmt:formatNumber value="${view.reservation_type == '2' ? 4500 : 3000}" pattern="#,##0"/>원)</span></td></tr>
						<tr><th>이용 시간</th><td>${view.start_text} ~ <c:out value="${empty view.end_text ? '(자유출차)' : view.end_text}"/>
							<c:if test="${not empty view.out_text}"> · <strong>실제 출차 ${view.out_text}</strong></c:if></td></tr>
						<tr><th>항공편</th><td>
							<c:if test="${empty view.flight_no}"><span class="dim">없음</span></c:if>
							<c:if test="${not empty view.flight_no}">
								${view.flight_no} · ${view.airport} · 도착 예정 ${view.flight_sched}
								<span class="badge ${view.flight_remark == '결항' ? 'st4' : 'ok'}">${view.flight_remark}</span>
							</c:if>
						</td></tr>
						<tr><th>결제 합계</th><td><fmt:formatNumber value="${view.paid_total}" pattern="#,##0"/>원 <span class="dim">(예약금 <fmt:formatNumber value="${view.paid_deposit}" pattern="#,##0"/>원)</span></td></tr>
					</table>

					<h3 class="sub_h">결제 내역</h3>
					<c:if test="${empty payments}"><p class="empty small">결제 기록이 없습니다.</p></c:if>
					<c:if test="${not empty payments}">
					<table class="tbl">
						<thead><tr><th>결제번호</th><th>종류</th><th class="r">금액</th><th>수단</th><th>일시</th></tr></thead>
						<tbody>
						<c:forEach var="p" items="${payments}">
							<tr>
								<td class="mono">${p.payment_id}</td>
								<td>${p.type_label}</td>
								<td class="r ${p.amount < 0 ? 'minus' : ''}"><fmt:formatNumber value="${p.amount}" pattern="#,##0"/></td>
								<td>${p.method}</td>
								<td>${p.pay_text}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
					</c:if>

					<%-- 상태별 처리 --%>
					<c:choose>
						<c:when test="${view.status == '1'}">
							<div class="action_box">
								<p>예약완료 상태입니다. 차량이 들어오면 입차 처리하세요.</p>
								<form method="post" action="${ctx}/Manager" onsubmit="return confirm('${view.reservation_id} 입차 처리하시겠습니까?')">
									<input type="hidden" name="t_gubun" value="gateIn">
									<input type="hidden" name="t_reservation_id" value="${view.reservation_id}">
									<button type="submit" class="adm_btn adm_btn_green">입차 처리</button>
								</form>
							</div>
						</c:when>

						<c:when test="${view.status == '2'}">
							<div class="action_box">
								<p><strong>지금 출차하면</strong></p>
								<table class="dl compact">
									<tr><th>이용 시간</th><td><fmt:formatNumber value="${feeHours}" pattern="0.0"/>시간 × <fmt:formatNumber value="${feeRate}" pattern="#,##0"/>원</td></tr>
									<tr><th>총 요금</th><td><fmt:formatNumber value="${feeTotal}" pattern="#,##0"/>원</td></tr>
									<tr><th>예약금 차감</th><td>- <fmt:formatNumber value="${feeDeposit}" pattern="#,##0"/>원</td></tr>
									<tr class="total"><th>${feeDue >= 0 ? '추가 결제' : '환불'}</th><td><strong><fmt:formatNumber value="${feeDue < 0 ? -feeDue : feeDue}" pattern="#,##0"/>원</strong></td></tr>
								</table>
								<form method="post" action="${ctx}/Manager" class="adm_form" onsubmit="return confirm('${view.reservation_id} 출차 처리하고 정산하시겠습니까?')">
									<input type="hidden" name="t_gubun" value="gateOut">
									<input type="hidden" name="t_reservation_id" value="${view.reservation_id}">
									<select name="t_pay_method">
										<option value="card">현장 카드</option>
										<option value="cash">현금</option>
										<option value="kakaoPay">카카오페이</option>
									</select>
									<button type="submit" class="adm_btn">출차 처리 + 정산</button>
								</form>
							</div>
						</c:when>

						<c:otherwise>
							<div class="action_box done">
								<p>${view.status_label} 상태입니다. 더 처리할 것이 없습니다.</p>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>

		</section>

	</main>
</div>

</body>
</html>
