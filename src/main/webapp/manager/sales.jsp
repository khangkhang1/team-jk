<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- 매출 통계. 기간(t_from~t_to, 결제일 기준) 합계 + 일별/구역별/결제수단별 표 + CSV 내려받기 --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>매출 통계 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<section class="card">
			<form method="get" action="${ctx}/Manager" class="adm_form search_bar">
				<input type="hidden" name="t_gubun" value="sales">
				<label>기간</label>
				<input type="date" name="t_from" value="${from}">
				<span>~</span>
				<input type="date" name="t_to" value="${to}">
				<button type="submit" class="adm_btn">조회</button>
				<a href="${ctx}/Manager?t_gubun=salesCsv&t_from=${from}&t_to=${to}" class="adm_btn adm_btn_green">CSV(엑셀) 내려받기</a>
				<span class="card_note">결제일 기준 · 확정 매출은 출차 완료된 예약의 결제만 합산</span>
			</form>
		</section>

		<section class="kpi_row">
			<div class="kpi kpi_navy">
				<p class="kpi_label">확정 매출</p>
				<p class="kpi_value"><fmt:formatNumber value="${summary.confirmed_sales}" pattern="#,##0"/>원</p>
				<p class="kpi_sub">출차 완료 <fmt:formatNumber value="${summary.done_cnt}" pattern="#,##0"/>건</p>
			</div>
			<div class="kpi kpi_blue">
				<p class="kpi_label">입금 합계</p>
				<p class="kpi_value"><fmt:formatNumber value="${summary.paid_in}" pattern="#,##0"/>원</p>
				<p class="kpi_sub">예약금 + 출차 결제</p>
			</div>
			<div class="kpi kpi_amber">
				<p class="kpi_label">환불</p>
				<p class="kpi_value"><fmt:formatNumber value="${summary.refund}" pattern="#,##0"/>원</p>
				<p class="kpi_sub">취소·초과 예약금 반환</p>
			</div>
			<div class="kpi kpi_green">
				<p class="kpi_label">결제 건수</p>
				<p class="kpi_value"><fmt:formatNumber value="${summary.pay_cnt}" pattern="#,##0"/>건</p>
				<p class="kpi_sub">${from} ~ ${to}</p>
			</div>
		</section>

		<section class="grid_2_1">
			<div class="card">
				<div class="card_head"><h2>일별</h2></div>
				<c:if test="${empty daily}"><p class="empty">기간 안에 결제가 없습니다.</p></c:if>
				<c:if test="${not empty daily}">
				<table class="tbl">
					<thead><tr><th>날짜</th><th class="r">확정 매출</th><th class="r">입금 합계</th><th class="r">환불</th><th class="r">건수</th></tr></thead>
					<tbody>
					<c:forEach var="d" items="${daily}">
						<tr>
							<td>${d.day}</td>
							<td class="r"><fmt:formatNumber value="${d.confirmed_sales}" pattern="#,##0"/></td>
							<td class="r"><fmt:formatNumber value="${d.paid_in}" pattern="#,##0"/></td>
							<td class="r ${d.refund > 0 ? 'minus' : ''}"><fmt:formatNumber value="${d.refund}" pattern="#,##0"/></td>
							<td class="r">${d.pay_cnt}</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>
			</div>

			<div class="card">
				<div class="card_head"><h2>구역별</h2></div>
				<c:if test="${empty byLot}"><p class="empty">없음</p></c:if>
				<c:if test="${not empty byLot}">
				<table class="tbl">
					<thead><tr><th>구역</th><th class="r">확정 매출</th><th class="r">입금</th><th class="r">건수</th></tr></thead>
					<tbody>
					<c:forEach var="l" items="${byLot}">
						<tr>
							<td><strong>${l.lot_id}</strong></td>
							<td class="r"><fmt:formatNumber value="${l.confirmed_sales}" pattern="#,##0"/></td>
							<td class="r"><fmt:formatNumber value="${l.paid_in}" pattern="#,##0"/></td>
							<td class="r">${l.pay_cnt}</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>

				<div class="card_head" style="margin-top:22px"><h2>결제수단별</h2></div>
				<c:if test="${empty byMethod}"><p class="empty">없음</p></c:if>
				<c:if test="${not empty byMethod}">
				<table class="tbl">
					<thead><tr><th>수단</th><th class="r">입금</th><th class="r">환불</th><th class="r">건수</th></tr></thead>
					<tbody>
					<c:forEach var="m" items="${byMethod}">
						<tr>
							<td>${m.method}</td>
							<td class="r"><fmt:formatNumber value="${m.paid_in}" pattern="#,##0"/></td>
							<td class="r"><fmt:formatNumber value="${m.refund}" pattern="#,##0"/></td>
							<td class="r">${m.pay_cnt}</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>
			</div>
		</section>

	</main>
</div>

</body>
</html>
