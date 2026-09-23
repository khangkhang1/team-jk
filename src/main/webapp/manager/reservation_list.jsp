<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- 예약 관리 목록. 검색(t_select/t_search) + 상태(t_status) + 페이지(t_nowPage). 행을 누르면 입·출차 화면의 상세로 --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>예약 관리 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<section class="card">
			<form method="get" action="${ctx}/Manager" class="adm_form search_bar">
				<input type="hidden" name="t_gubun" value="reservation">
				<select name="t_select">
					<option value="reservation_id" ${select == 'reservation_id' ? 'selected' : ''}>예약번호</option>
					<option value="member_id"      ${select == 'member_id'      ? 'selected' : ''}>회원 ID</option>
					<option value="seat_no"        ${select == 'seat_no'        ? 'selected' : ''}>좌석</option>
				</select>
				<input type="text" name="t_search" value="<c:out value='${search}'/>" placeholder="검색어">
				<select name="t_status">
					<option value=""  ${status == ''  ? 'selected' : ''}>상태 전체</option>
					<option value="1" ${status == '1' ? 'selected' : ''}>예약완료</option>
					<option value="2" ${status == '2' ? 'selected' : ''}>주차 중</option>
					<option value="3" ${status == '3' ? 'selected' : ''}>출차 완료</option>
					<option value="4" ${status == '4' ? 'selected' : ''}>취소</option>
				</select>
				<button type="submit" class="adm_btn">검색</button>
				<a href="${ctx}/Manager?t_gubun=reservation" class="adm_btn adm_btn_ghost">초기화</a>
				<span class="search_total">총 <strong><fmt:formatNumber value="${totalCount}" pattern="#,##0"/></strong>건</span>
			</form>
		</section>

		<section class="card">
			<c:if test="${empty dtos}"><p class="empty">조건에 맞는 예약이 없습니다.</p></c:if>
			<c:if test="${not empty dtos}">
			<table class="tbl tbl_click">
				<thead><tr>
					<th>No</th><th>예약번호</th><th>회원</th><th>좌석</th><th>유형</th><th>상태</th>
					<th>이용 시작</th><th>종료 예정</th><th>실제 출차</th><th>항공편</th><th class="r">선결제</th>
				</tr></thead>
				<tbody>
				<c:forEach var="r" items="${dtos}" varStatus="s">
					<tr onclick="location.href='${ctx}/Manager?t_gubun=gate&t_reservation_id=${r.reservation_id}'">
						<td class="dim">${startNo + s.index}</td>
						<td class="mono">${r.reservation_id}</td>
						<td>${r.member_name}</td>
						<td>${r.seat_no}</td>
						<td>${r.type_label}</td>
						<td><span class="badge st${r.status}">${r.status_label}</span></td>
						<td>${r.start_text}</td>
						<td><c:out value="${empty r.end_text ? '-' : r.end_text}"/></td>
						<td><c:out value="${empty r.out_text ? '-' : r.out_text}"/></td>
						<td>
							<c:if test="${empty r.flight_no}">-</c:if>
							<c:if test="${not empty r.flight_no}">${r.flight_no}
								<c:if test="${r.flight_remark == '결항'}"><span class="badge st4">결항</span></c:if>
							</c:if>
						</td>
						<td class="r"><fmt:formatNumber value="${r.deposit}" pattern="#,##0"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>

			<%-- 페이지 번호 5개씩 (팀 규칙). 검색 조건은 c:url 로 같이 넘긴다 --%>
			<c:set var="pStart" value="${nowPage - ((nowPage - 1) mod 5)}" />
			<c:set var="pEnd"   value="${pStart + 4 > totalPage ? totalPage : pStart + 4}" />
			<div class="pager">
				<c:if test="${pStart > 1}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="reservation"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_status" value="${status}"/><c:param name="t_nowPage" value="${pStart - 1}"/></c:url>
					<a href="${u}">&laquo;</a>
				</c:if>
				<c:forEach var="p" begin="${pStart}" end="${pEnd}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="reservation"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_status" value="${status}"/><c:param name="t_nowPage" value="${p}"/></c:url>
					<a href="${u}" class="${p == nowPage ? 'on' : ''}">${p}</a>
				</c:forEach>
				<c:if test="${pEnd < totalPage}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="reservation"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_status" value="${status}"/><c:param name="t_nowPage" value="${pEnd + 1}"/></c:url>
					<a href="${u}">&raquo;</a>
				</c:if>
			</div>
			</c:if>
		</section>

	</main>
</div>

</body>
</html>
