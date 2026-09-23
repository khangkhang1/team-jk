<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
 공지사항 관리 (관리자 콘솔 안). icn_notice 를 관리자 화면에서 바로 등록·수정·삭제한다.
 이용자용 공지 게시판 화면은 정규상 파트라 건드리지 않았고, 테이블만 같이 쓴다.
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>공지사항 관리 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<section class="card">
			<form method="get" action="${ctx}/Manager" class="adm_form search_bar">
				<input type="hidden" name="t_gubun" value="notice">
				<label>제목</label>
				<input type="text" name="t_search" value="<c:out value='${search}'/>" placeholder="검색어">
				<button type="submit" class="adm_btn">검색</button>
				<a href="${ctx}/Manager?t_gubun=notice" class="adm_btn adm_btn_ghost">초기화</a>
				<span class="search_total">총 <strong>${totalCount}</strong>건
					<c:if test="${importantCount > 0}"> · 중요 <strong>${importantCount}</strong>건</c:if>
				</span>
			</form>
		</section>

		<section class="card">
			<div class="card_head">
				<h2>공지사항</h2>
				<a href="${ctx}/Manager?t_gubun=noticeForm" class="adm_btn">+ 새 공지 등록</a>
				<span class="card_note">중요 공지는 목록 맨 위에 고정됩니다</span>
			</div>

			<c:if test="${empty dtos}"><p class="empty">등록된 공지사항이 없습니다.</p></c:if>
			<c:if test="${not empty dtos}">
			<table class="tbl">
				<thead><tr>
					<th style="width:64px">번호</th><th>제목</th><th style="width:70px">조회수</th>
					<th style="width:90px">등록자</th><th style="width:100px">등록일</th><th style="width:120px">관리</th>
				</tr></thead>
				<tbody>
				<c:forEach var="n" items="${dtos}">
					<tr>
						<td class="mono">${n.no}</td>
						<td>
							<c:if test="${n.important == 'Y'}"><span class="badge st4">중요</span> </c:if>
							<a href="${ctx}/Manager?t_gubun=noticeForm&t_no=${n.no}" class="row_link"><c:out value="${n.title}"/></a>
						</td>
						<td class="dim">${n.hit}</td>
						<td class="dim">${n.reg_id}</td>
						<td class="dim">${n.reg_date}</td>
						<td class="row_btns">
							<a href="${ctx}/Manager?t_gubun=noticeForm&t_no=${n.no}" class="adm_btn adm_btn_ghost adm_btn_sm">수정</a>
							<form method="post" action="${ctx}/Manager" style="display:inline"
								onsubmit="return confirm('${n.no}번 공지를 삭제하시겠습니까?\n삭제하면 되돌릴 수 없습니다.')">
								<input type="hidden" name="t_gubun" value="noticeDelete">
								<input type="hidden" name="t_no" value="${n.no}">
								<button type="submit" class="adm_btn adm_btn_red adm_btn_sm">삭제</button>
							</form>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
			</c:if>
		</section>

	</main>
</div><%-- .adm_main (manager_menu.jsp 에서 열림) --%>

</body>
</html>
