<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
 관리자 대시보드. Manager 서블릿이 kpi / daily / lots / unsold / recent / check 를 담아 forward 한다.
 숫자는 전부 ManagerDao 의 SQL 이 계산한 값. 이 화면은 보여주기만 한다.
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>대시보드 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<%-- ① 핵심 지표 --%>
		<section class="kpi_row">
			<div class="kpi kpi_blue">
				<p class="kpi_label">오늘 이용 예약</p>
				<p class="kpi_value"><fmt:formatNumber value="${kpi.today_cnt}" pattern="#,##0" />건</p>
				<p class="kpi_sub">어제 <fmt:formatNumber value="${kpi.yesterday_cnt}" pattern="#,##0" />건</p>
			</div>
			<div class="kpi kpi_green">
				<p class="kpi_label">현재 주차 중</p>
				<p class="kpi_value"><fmt:formatNumber value="${kpi.parking_cnt}" pattern="#,##0" />대</p>
				<p class="kpi_sub">입차 대기 <fmt:formatNumber value="${kpi.waiting_cnt}" pattern="#,##0" />건</p>
			</div>
			<div class="kpi kpi_navy">
				<p class="kpi_label">이번 달 확정 매출</p>
				<p class="kpi_value"><fmt:formatNumber value="${kpi.this_month}" pattern="#,##0" />원</p>
				<p class="kpi_sub">지난달 <fmt:formatNumber value="${kpi.last_month}" pattern="#,##0" />원 · 출차 완료 기준</p>
			</div>
			<div class="kpi kpi_amber">
				<p class="kpi_label">받아둔 예약금</p>
				<p class="kpi_value"><fmt:formatNumber value="${kpi.deposit_sum}" pattern="#,##0" />원</p>
				<p class="kpi_sub"><fmt:formatNumber value="${kpi.deposit_cnt}" pattern="#,##0" />건 · 출차 전이라 매출 아님</p>
			</div>
		</section>

		<%-- ② 추이 그래프 + 구역별 이용률 --%>
		<section class="grid_2_1">

			<div class="card">
				<div class="card_head">
					<h2>최근 14일 예약·입금 추이</h2>
					<span class="card_note">막대 = 예약 건수 · 선 = 입금액(예약금 포함, 환불 차감)</span>
				</div>
				<div class="chart_box">
					<canvas id="dailyChart" height="110"></canvas>
				</div>
				<%-- 그래프 라이브러리를 못 불러올 때(오프라인) 대신 보여줄 표 --%>
				<table class="tbl chart_fallback" hidden>
					<thead><tr><th>날짜</th><th>예약</th><th>입금액</th></tr></thead>
					<tbody>
					<c:forEach var="d" items="${daily}">
						<tr><td>${d.day_label}</td><td>${d.resv_cnt}</td><td><fmt:formatNumber value="${d.pay_amt}" pattern="#,##0" /></td></tr>
					</c:forEach>
					</tbody>
				</table>
			</div>

			<div class="card">
				<div class="card_head">
					<h2>구역별 현재 이용률</h2>
					<span class="card_note">사용 중 / 전체 좌석</span>
				</div>
				<ul class="bar_list">
				<c:forEach var="lot" items="${lots}">
					<li>
						<span class="bar_name">${lot.lot_id}</span>
						<span class="bar_track">
							<span class="bar_fill ${lot.use_rate >= 80 ? 'hot' : (lot.use_rate >= 50 ? 'warm' : '')}" style="width:${lot.use_rate}%"></span>
						</span>
						<span class="bar_num">${lot.used_cnt} / ${lot.total_cnt}</span>
					</li>
				</c:forEach>
				</ul>
			</div>

		</section>

		<%-- ③ 미판매 좌석 + 최근 예약 --%>
		<section class="grid_1_2">

			<div class="card">
				<div class="card_head">
					<h2>최근 30일 미판매 좌석</h2>
					<span class="card_note">한 번도 예약되지 않은 좌석</span>
				</div>
				<table class="tbl">
					<thead><tr><th>구역</th><th>좌석</th><th>미판매</th><th style="width:40%">비율</th></tr></thead>
					<tbody>
					<c:forEach var="u" items="${unsold}">
						<tr>
							<td><strong>${u.lot_id}</strong></td>
							<td>${u.total_cnt}</td>
							<td>${u.unsold_cnt}</td>
							<td>
								<span class="mini_track"><span class="mini_fill ${u.unsold_rate >= 90 ? 'hot' : ''}" style="width:${u.unsold_rate}%"></span></span>
								<span class="mini_num">${u.unsold_rate}%</span>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</div>

			<div class="card">
				<div class="card_head">
					<h2>최근 예약</h2>
					<span class="card_note">이용 시작 시각 기준 10건</span>
				</div>
				<c:if test="${empty recent}">
					<p class="empty">예약이 없습니다.</p>
				</c:if>
				<c:if test="${not empty recent}">
				<table class="tbl">
					<thead><tr><th>예약번호</th><th>회원</th><th>좌석</th><th>유형</th><th>상태</th><th>이용 시작</th><th class="r">예약금</th></tr></thead>
					<tbody>
					<c:forEach var="r" items="${recent}">
						<tr>
							<td class="mono">${r.reservation_id}</td>
							<td>${r.member_name}</td>
							<td>${r.seat_no}</td>
							<td>${r.type_label}</td>
							<td><span class="badge st${r.status}">${r.status_label}</span></td>
							<td>${r.start_text}</td>
							<td class="r"><fmt:formatNumber value="${r.deposit}" pattern="#,##0" /></td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>
			</div>

		</section>

		<%-- ④ 데이터 점검 --%>
		<section class="card">
			<div class="card_head">
				<h2>데이터 점검</h2>
				<span class="card_note">결제·예약 기록이 서로 맞는지 매번 조회 시점에 확인</span>
			</div>
			<ul class="check_list">
				<li>
					<span class="badge ${check.dup_payment > 0 ? 'warn' : 'ok'}">${check.dup_payment > 0 ? check.dup_payment : '정상'}</span>
					같은 예약에 같은 종류의 결제가 2건 이상
				</li>
				<li>
					<span class="badge ${check.orphan_payment > 0 ? 'warn' : 'ok'}">${check.orphan_payment > 0 ? check.orphan_payment : '정상'}</span>
					예약이 없는 결제
				</li>
				<li>
					<span class="badge ${check.resv_no_payment > 0 ? 'warn' : 'ok'}">${check.resv_no_payment > 0 ? check.resv_no_payment : '정상'}</span>
					결제 기록이 없는 예약
				</li>
				<li>
					<span class="badge ${check.overdue_resv > 0 ? 'warn' : 'ok'}">${check.overdue_resv > 0 ? check.overdue_resv : '정상'}</span>
					종료 시각이 지났는데 입차 처리가 없는 예약 (노쇼 의심)
				</li>
			</ul>
		</section>

	</main>
</div><%-- .adm_main (manager_menu.jsp 에서 열림) --%>

<%-- 그래프 데이터 : 서버가 계산한 값을 JS 배열로 --%>
<script>
var dailyLabels = [<c:forEach var="d" items="${daily}" varStatus="s">"${d.day_label}"<c:if test="${!s.last}">,</c:if></c:forEach>];
var dailyResv   = [<c:forEach var="d" items="${daily}" varStatus="s">${d.resv_cnt}<c:if test="${!s.last}">,</c:if></c:forEach>];
var dailyPay    = [<c:forEach var="d" items="${daily}" varStatus="s">${d.pay_amt}<c:if test="${!s.last}">,</c:if></c:forEach>];
</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script src="${pageContext.request.contextPath}/js/manager.js"></script>

</body>
</html>
