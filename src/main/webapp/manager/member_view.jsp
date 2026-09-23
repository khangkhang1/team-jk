<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원 상세 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<c:if test="${empty view}">
			<section class="card">
				<div class="card_head">
					<h2>회원 상세</h2>
					<a class="card_link" href="${ctx}/${listUrl}">목록으로</a>
				</div>
				<p class="empty">"<c:out value="${memberId}"/>" 회원을 찾을 수 없습니다.</p>
			</section>
		</c:if>

		<c:if test="${not empty view}">

		<section class="kpi_row">
			<div class="kpi kpi_blue">
				<p class="kpi_label">총 예약</p>
				<p class="kpi_value">${view.resv_cnt}건</p>
				<p class="kpi_sub">출차 완료 ${view.done_cnt} · 취소 ${view.cancel_cnt}</p>
			</div>
			<div class="kpi kpi_green">
				<p class="kpi_label">현재 주차 중</p>
				<p class="kpi_value">${view.parking_cnt}건</p>
				<p class="kpi_sub">최근 이용 <c:out value="${empty view.last_use ? '없음' : view.last_use}"/></p>
			</div>
			<div class="kpi kpi_navy">
				<p class="kpi_label">누적 결제 금액</p>
				<p class="kpi_value"><fmt:formatNumber value="${view.paid}" pattern="#,##0"/>원</p>
				<p class="kpi_sub">환불 차감 후</p>
			</div>
			<div class="kpi kpi_amber">
				<p class="kpi_label">문의</p>
				<p class="kpi_value">${fn:length(reports)}건</p>
				<p class="kpi_sub">최근 10건까지 표시</p>
			</div>
		</section>

		<section class="grid_1_2">

			<div class="card">
				<div class="card_head">
					<h2>${view.name} <span class="dim">${view.member_id}</span></h2>
					<a class="card_link" href="${ctx}/${listUrl}">목록으로</a>
				</div>
				<table class="dl">
					<tr><th>연락처</th><td>${view.phone_number}</td></tr>
					<tr><th>이메일</th><td><c:out value="${view.email}"/></td></tr>
					<tr><th>차량</th><td><strong>${view.vehicle_number}</strong> · ${view.vehicle_type_label}</td></tr>
					<tr><th>가입일</th><td>${view.reg_date}</td></tr>
					<tr><th>상태</th><td>
						<c:choose>
							<c:when test="${empty view.exit_date}"><span class="badge ok">이용 중</span></c:when>
							<c:otherwise><span class="badge st4">탈퇴</span> <span class="dim">${view.exit_date}</span></c:otherwise>
						</c:choose>
					</td></tr>
				</table>

				<h3 class="sub_h">문의 내역</h3>
				<c:if test="${empty reports}"><p class="empty small">문의한 내역이 없습니다.</p></c:if>
				<c:if test="${not empty reports}">
				<table class="tbl tbl_click">
					<thead><tr><th>상태</th><th>유형</th><th>제목</th><th>접수일</th></tr></thead>
					<tbody>
					<c:forEach var="rp" items="${reports}">
						<tr onclick="location.href='${ctx}/Manager?t_gubun=reportView&t_report_id=${rp.report_id}'">
							<td><span class="badge rp${rp.report_status}">${rp.status_label}</span></td>
							<td>${rp.type_label}</td>
							<td class="ellipsis"><c:out value="${rp.title}"/></td>
							<td class="dim">${rp.reg_date}</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>
			</div>

			<div class="card">
				<div class="card_head">
					<h2>예약 내역</h2>
					<span class="card_note">최근 50건 · 행을 누르면 입·출차 화면으로</span>
				</div>
				<c:if test="${empty reservations}"><p class="empty">예약 내역이 없습니다.</p></c:if>
				<c:if test="${not empty reservations}">
				<table class="tbl tbl_click">
					<thead><tr>
						<th>예약번호</th><th>좌석</th><th>유형</th><th>상태</th>
						<th>이용 시작</th><th>종료 예정</th><th>실제 출차</th>
						<th class="r">선결제</th><th class="r">최종 요금</th>
					</tr></thead>
					<tbody>
					<c:forEach var="r" items="${reservations}">
						<tr onclick="location.href='${ctx}/Manager?t_gubun=gate&t_reservation_id=${r.reservation_id}'">
							<td class="mono">${r.reservation_id}</td>
							<td>${r.seat_no}</td>
							<td>${r.type_label}</td>
							<td><span class="badge st${r.status}">${r.status_label}</span></td>
							<td>${r.start_text}</td>
							<td class="dim"><c:out value="${empty r.end_text ? '-' : r.end_text}"/></td>
							<td class="dim"><c:out value="${empty r.out_text ? '-' : r.out_text}"/></td>
							<td class="r"><fmt:formatNumber value="${r.prepaid}" pattern="#,##0"/></td>
							<td class="r"><c:choose><c:when test="${r.final_amount > 0}"><fmt:formatNumber value="${r.final_amount}" pattern="#,##0"/></c:when><c:otherwise><span class="dim">-</span></c:otherwise></c:choose></td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>
			</div>

		</section>
		</c:if>

	</main>
</div>

</body>
</html>
