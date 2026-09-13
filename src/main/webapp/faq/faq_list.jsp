<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
 FAQ 목록. Faq 서블릿이 dtos / category / isAdmin 을 request 에 담아 forward 한다.
 (이 JSP 를 직접 열면 빈 목록이 나온다. 반드시 /Faq 로 들어올 것)
--%>
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

	<%-- 수정/삭제 이동용 숨김 폼. faq.js 가 t_gubun / t_faq_id 를 채워서 Faq 서블릿으로 POST 한다 --%>
	<form name="faq" method="post" action="${pageContext.request.contextPath}/Faq">
		<input type="hidden" name="t_gubun">
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
					<a href="${pageContext.request.contextPath}/Faq?t_gubun=writeForm" class="faq_btn faq_btn_primary">글쓰기</a>
				</c:if>
			</div>

			<div class="faq_cate">
				<a href="${pageContext.request.contextPath}/Faq"                     class="${empty category        ? 'on' : ''}">전체</a>
				<a href="${pageContext.request.contextPath}/Faq?t_category=예약"      class="${category == '예약'      ? 'on' : ''}">예약</a>
				<a href="${pageContext.request.contextPath}/Faq?t_category=요금·결제" class="${category == '요금·결제' ? 'on' : ''}">요금·결제</a>
				<a href="${pageContext.request.contextPath}/Faq?t_category=입·출차"   class="${category == '입·출차'   ? 'on' : ''}">입·출차</a>
				<a href="${pageContext.request.contextPath}/Faq?t_category=항공편"    class="${category == '항공편'    ? 'on' : ''}">항공편</a>
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
