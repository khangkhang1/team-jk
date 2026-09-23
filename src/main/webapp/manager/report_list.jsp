<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>문의 내역 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<section class="card">
			<form method="get" action="${ctx}/Manager" class="adm_form search_bar">
				<input type="hidden" name="t_gubun" value="report">
				<select name="t_select">
					<option value="title"     ${select == 'title'     ? 'selected' : ''}>제목</option>
					<option value="member_id" ${select == 'member_id' ? 'selected' : ''}>작성자 ID</option>
					<option value="seat_no"   ${select == 'seat_no'   ? 'selected' : ''}>좌석</option>
				</select>
				<input type="text" name="t_search" value="<c:out value='${search}'/>" placeholder="검색어">
				<select name="t_status">
					<option value=""  ${status == ''  ? 'selected' : ''}>상태 전체</option>
					<option value="1" ${status == '1' ? 'selected' : ''}>접수</option>
					<option value="2" ${status == '2' ? 'selected' : ''}>처리 중</option>
					<option value="3" ${status == '3' ? 'selected' : ''}>처리 완료</option>
					<option value="4" ${status == '4' ? 'selected' : ''}>반려</option>
				</select>
				<select name="t_type">
					<option value=""  ${type == ''  ? 'selected' : ''}>유형 전체</option>
					<option value="1" ${type == '1' ? 'selected' : ''}>자리 무단점유</option>
					<option value="2" ${type == '2' ? 'selected' : ''}>시설 파손·고장</option>
					<option value="3" ${type == '3' ? 'selected' : ''}>차량 훼손</option>
					<option value="4" ${type == '4' ? 'selected' : ''}>불법 주차</option>
					<option value="5" ${type == '5' ? 'selected' : ''}>기타 문의</option>
				</select>
				<button type="submit" class="adm_btn">검색</button>
				<a href="${ctx}/Manager?t_gubun=report" class="adm_btn adm_btn_ghost">초기화</a>
				<span class="search_total">총 <strong><fmt:formatNumber value="${totalCount}" pattern="#,##0"/></strong>건
					<c:if test="${reportWaiting > 0}"> · 미처리 <strong>${reportWaiting}</strong>건</c:if>
				</span>
			</form>
		</section>

		<section class="card">
			<c:if test="${empty dtos}"><p class="empty">조건에 맞는 문의가 없습니다.</p></c:if>
			<c:if test="${not empty dtos}">
			<table class="tbl tbl_click">
				<thead><tr>
					<th>No</th><th>상태</th><th>유형</th><th>제목</th><th>작성자</th><th>좌석</th>
					<th>접수 일시</th><th>경과</th><th>처리자</th>
				</tr></thead>
				<tbody>
				<c:forEach var="r" items="${dtos}" varStatus="s">
					<c:url var="viewUrl" value="/Manager">
						<c:param name="t_gubun" value="reportView"/>
						<c:param name="t_report_id" value="${r.report_id}"/>
						<c:param name="t_select" value="${select}"/>
						<c:param name="t_search" value="${search}"/>
						<c:param name="t_status" value="${status}"/>
						<c:param name="t_type" value="${type}"/>
						<c:param name="t_nowPage" value="${nowPage}"/>
					</c:url>
					<tr onclick="location.href='${viewUrl}'">
						<td class="dim">${startNo + s.index}</td>
						<td><span class="badge rp${r.report_status}">${r.status_label}</span></td>
						<td>${r.type_label}</td>
						<td class="ellipsis"><c:out value="${r.title}"/></td>
						<td>${r.member_name}</td>
						<td><c:out value="${empty r.seat_no ? '-' : r.seat_no}"/></td>
						<td>${r.reg_date}</td>
						<td>
							<%-- 아직 안 끝난 신고만 경과일을 보여준다. 3일 넘게 잡고 있으면 눈에 띄게 --%>
							<c:choose>
								<c:when test="${r.report_status == '1' or r.report_status == '2'}">
									<span class="badge ${r.elapsed_days >= 3 ? 'warn' : 'ok'}">${r.elapsed_days}일</span>
								</c:when>
								<c:otherwise><span class="dim">-</span></c:otherwise>
							</c:choose>
						</td>
						<td><c:out value="${empty r.answer_id ? '-' : r.answer_id}"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>

			<c:set var="pStart" value="${nowPage - ((nowPage - 1) mod 5)}" />
			<c:set var="pEnd"   value="${pStart + 4 > totalPage ? totalPage : pStart + 4}" />
			<div class="pager">
				<c:if test="${pStart > 1}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="report"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_status" value="${status}"/><c:param name="t_type" value="${type}"/><c:param name="t_nowPage" value="${pStart - 1}"/></c:url>
					<a href="${u}">&laquo;</a>
				</c:if>
				<c:forEach var="p" begin="${pStart}" end="${pEnd}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="report"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_status" value="${status}"/><c:param name="t_type" value="${type}"/><c:param name="t_nowPage" value="${p}"/></c:url>
					<a href="${u}" class="${p == nowPage ? 'on' : ''}">${p}</a>
				</c:forEach>
				<c:if test="${pEnd < totalPage}">
					<c:url var="u" value="/Manager"><c:param name="t_gubun" value="report"/><c:param name="t_select" value="${select}"/><c:param name="t_search" value="${search}"/><c:param name="t_status" value="${status}"/><c:param name="t_type" value="${type}"/><c:param name="t_nowPage" value="${pEnd + 1}"/></c:url>
					<a href="${u}">&raquo;</a>
				</c:if>
			</div>
			</c:if>
		</section>

	</main>
</div>

</body>
</html>
