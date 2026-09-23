<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
 FAQ 관리 (관리자 콘솔 안). 이용자 화면(/Faq)으로 나가지 않고 여기서 등록·수정·삭제까지 끝낸다.
 질문/답변은 저장할 때 따옴표를 HTML 엔티티로 바꿔 넣으므로 c:out 없이 그대로 출력한다.
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>FAQ 관리 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<%-- 카테고리 탭 --%>
		<section class="tabs">
			<a href="${ctx}/Manager?t_gubun=faq" class="${empty category ? 'on' : ''}">
				<strong>전체</strong><span>${totalCount}건</span>
			</a>
			<c:forEach var="cg" items="${categories}">
				<a href="${ctx}/Manager?t_gubun=faq&t_category=${cg}" class="${category == cg ? 'on' : ''}">
					<strong>${cg}</strong><span>보기</span>
				</a>
			</c:forEach>
		</section>

		<section class="card">
			<div class="card_head">
				<h2>
					<c:choose>
						<c:when test="${empty category}">전체 FAQ</c:when>
						<c:otherwise>${category}</c:otherwise>
					</c:choose>
					<span class="dim">${totalCount}건</span>
					<c:if test="${hiddenCount > 0}"><span class="badge warn">숨김 ${hiddenCount}</span></c:if>
				</h2>
				<a href="${ctx}/Manager?t_gubun=faqForm" class="adm_btn">+ 새 FAQ 등록</a>
				<span class="card_note">이용자 화면에는 "노출 Y" 인 글만 보입니다</span>
			</div>

			<c:if test="${empty dtos}"><p class="empty">등록된 FAQ 가 없습니다.</p></c:if>
			<c:if test="${not empty dtos}">
			<table class="tbl">
				<thead><tr>
					<th style="width:46px">No</th><th style="width:90px">카테고리</th><th>질문 / 답변</th>
					<th style="width:56px">정렬</th><th style="width:58px">노출</th>
					<th style="width:80px">등록자</th><th style="width:86px">등록일</th><th style="width:120px">관리</th>
				</tr></thead>
				<tbody>
				<c:forEach var="f" items="${dtos}" varStatus="s">
					<tr class="${f.use_yn == 'N' ? 'row_off' : ''}">
						<td class="dim">${s.index + 1}</td>
						<td>${f.category}</td>
						<td>
							<strong>${f.question}</strong>
							<span class="row_sub">${f.answer}</span>
						</td>
						<td class="dim">${f.sort_no}</td>
						<td>
							<span class="badge ${f.use_yn == 'N' ? 'st4' : 'ok'}">${f.use_yn == 'N' ? '숨김' : '노출'}</span>
						</td>
						<td class="dim">${f.reg_id}</td>
						<td class="dim">${f.reg_date}</td>
						<td class="row_btns">
							<a href="${ctx}/Manager?t_gubun=faqForm&t_faq_id=${f.faq_id}" class="adm_btn adm_btn_ghost adm_btn_sm">수정</a>
							<form method="post" action="${ctx}/Manager" style="display:inline"
								onsubmit="return confirm('이 FAQ 를 삭제하시겠습니까?\n삭제하면 되돌릴 수 없습니다.')">
								<input type="hidden" name="t_gubun" value="faqDelete">
								<input type="hidden" name="t_faq_id" value="${f.faq_id}">
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
