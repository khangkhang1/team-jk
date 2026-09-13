<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dto.*,dao.*,java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	request.setCharacterEncoding("utf-8");
	FaqDao dao = new FaqDao();
	String loginLevel = (String)session.getAttribute("sessionLevel");
	boolean isAdmin = (loginLevel != null && loginLevel.equals("admin"));
	String category = request.getParameter("t_category");
	if (category == null) category = "";

	ArrayList<FaqDto> dtos = dao.getFaqList(category, isAdmin);

	request.setAttribute("dtos", dtos);
	request.setAttribute("category", category);
	request.setAttribute("isAdmin", isAdmin);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>자주 묻는 질문 | 인천공항 주차예약</title>
<link href="${pageContext.request.contextPath}/css/index1.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/faq.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/faq.js"></script>
</head>

<body>

<div class="wrap">

	<%@ include file="../common_header.jsp" %>

	<form name="faq" method="post">
		<input type="hidden" name="t_faq_id">
	</form>

	<main class="main faq_page">
	<div class="container">

		<section class="section" style="padding-top:0">

			<div class="section_head">
				<div>
					<h2>자주 묻는 질문</h2>
					<p>예약부터 출차까지, 자주 들어오는 문의를 모았습니다.</p>
				</div>
				<c:if test="${isAdmin}">
					<a href="faq_write.jsp" class="faq_btn faq_btn_primary">글쓰기</a>
				</c:if>
			</div>

			<div class="faq_cate">
				<a href="faq_list.jsp"                     class="${empty category        ? 'on' : ''}">전체</a>
				<a href="faq_list.jsp?t_category=예약"      class="${category == '예약'      ? 'on' : ''}">예약</a>
				<a href="faq_list.jsp?t_category=요금·결제" class="${category == '요금·결제' ? 'on' : ''}">요금·결제</a>
				<a href="faq_list.jsp?t_category=입·출차"   class="${category == '입·출차'   ? 'on' : ''}">입·출차</a>
				<a href="faq_list.jsp?t_category=항공편"    class="${category == '항공편'    ? 'on' : ''}">항공편</a>
			</div>

			<div class="faq_list">

			<c:if test="${empty dtos}">
				<p class="faq_empty">등록된 질문이 없습니다.</p>
			</c:if>

			<c:forEach var="dto" items="${dtos}" varStatus="status">
				<details class="faq_item" ${status.first ? "open" : ""}>
					<summary>
						<span class="faq_q">Q</span>
						${dto.question}
						<c:if test="${dto.use_yn == 'N'}">
							<span class="faq_badge_hidden">숨김</span>
						</c:if>
						<span class="faq_tag">${dto.category}</span>
					</summary>
					<div class="faq_body">
						<span class="faq_a">A</span>
						<div class="faq_answer">${dto.answer}</div>
					</div>
					<c:if test="${isAdmin}">
					<div class="faq_admin">
						<span class="faq_meta">${dto.reg_id} · ${dto.reg_date} · 정렬 ${dto.sort_no}</span>
						<a href="javascript:goUpdateForm('${dto.faq_id}')" class="faq_btn faq_btn_sm">수정</a>
						<a href="javascript:goDelete('${dto.faq_id}')" class="faq_btn faq_btn_sm faq_btn_danger">삭제</a>
					</div>
					</c:if>
				</details>
			</c:forEach>

			</div>

		</section>

	</div>
	</main>

</div>

</body>
</html>
