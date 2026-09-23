<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원 관리 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<section class="card">
			<form method="get" action="${ctx}/Manager" class="adm_form search_bar">
				<input type="hidden" name="t_gubun" value="member">
				<select name="t_select">
					<option value="member_id"      ${select == 'member_id'      ? 'selected' : ''}>회원 ID</option>
					<option value="name"           ${select == 'name'           ? 'selected' : ''}>이름</option>
					<option value="phone_number"   ${select == 'phone_number'   ? 'selected' : ''}>연락처</option>
					<option value="vehicle_number" ${select == 'vehicle_number' ? 'selected' : ''}>차량 번호</option>
				</select>
				<input type="text" name="t_search" value="<c:out value='${search}'/>" placeholder="검색어">
				<button type="submit" class="adm_btn">검색</button>
				<a href="${ctx}/Manager?t_gubun=member" class="adm_btn adm_btn_ghost">초기화</a>
				<span class="search_total">총 <strong><fmt:formatNumber value="${totalCount}" pattern="#,##0"/></strong>명</span>
			</form>
		</section>

		<section class="card">
			<c:if test="${empty dtos}"><p class="empty">조건에 맞는 회원이 없습니다.</p></c:if>
			<c:if test="${not empty dtos}">
			<table class="tbl tbl_click">
				<thead><tr>
					<th>No</th><th>회원 ID</th><th>이름</th><th>연락처</th><th>차량</th><th>종류</th>
					<th>가입일</th><th class="r">예약</th><th class="r">이용 금액</th><th>최근 이용</th><th>상태</th>
				</tr></thead>
				<tbody>
				<c:forEach var="m" items="${dtos}" varStatus="s">
					<c:url var="viewUrl" value="/Manager">
						<c:param name="t_gubun" value="memberView"/>
						<c:param name="t_member_id" value="${m.member_id}"/>
						<c:param name="t_select" value="${select}"/>
						<c:param name="t_search" value="${search}"/>
						<c:param name="t_nowPage" value="${nowPage}"/>
					</c:url>
					<tr onclick="location.href='${viewUrl}'">
						<td class="dim">${startNo + s.index}</td>
						<td class="mono">${m.member_id}</td>
						<td><strong>${m.name}</strong></td>
						<td class="dim">${m.phone_number}</td>
						<td class="dim">${m.vehicle_number}</td>
						<td>${m.vehicle_type_label}</td>
						<td class="dim">${m.reg_date}</td>
						<td class="r">
							${m.resv_cnt}건
							<c:if test="${m.parking_cnt > 0}"> <span class="badge st2">주차 중</span></c:if>
						</td>
						<td class="r"><fmt:formatNumber value="${m.paid}" pattern="#,##0"/></td>
						<td class="dim"><c:out value="${empty m.last_use ? '-' : m.last_use}"/></td>
						<td>
							<c:choose>
								<c:when test="${empty m.exit_date}"><span class="badge ok">이용 중</span></c:when>
								<c:otherwise><span class="badge st4">탈퇴</span></c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>

			<c:set var="pStart" value="${nowPage - ((nowPage - 1) mod 5)}" />
			<c:set var="pEnd"   value="${pStart + 4 > totalPage ? totalPage : pStart + 4}" />
			<div class="pager">
				<c:if test="${pStart > 1}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="member"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_nowPage" value="${pStart - 1}"/></c:url>
					<a href="${u}">&laquo;</a>
				</c:if>
				<c:forEach var="p" begin="${pStart}" end="${pEnd}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="member"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_nowPage" value="${p}"/></c:url>
					<a href="${u}" class="${p == nowPage ? 'on' : ''}">${p}</a>
				</c:forEach>
				<c:if test="${pEnd < totalPage}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="member"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_nowPage" value="${pEnd + 1}"/></c:url>
					<a href="${u}">&raquo;</a>
				</c:if>
			</div>
			</c:if>
		</section>

	</main>
</div>

</body>
</html>
