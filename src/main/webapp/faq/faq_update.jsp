<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*,dto.*" %>
<%--
 FAQ 수정 화면 (관리자 전용).
 목록의 [수정] → faq.js goUpdateForm(faq_id) 가 t_faq_id 를 POST 로 보내서 이 화면이 열린다.
 수정완료 버튼 → faq.js goUpdate() → db_faq_update.jsp
--%>
<%
	String loginLevel = (String)session.getAttribute("sessionLevel");
	boolean isAdmin = (loginLevel != null && loginLevel.equals("admin"));

	// 글번호가 없거나 숫자가 아니면(주소를 직접 친 경우) 0 으로 두고 아래에서 "없는 글" 처리
	String faqId = request.getParameter("t_faq_id");
	int faq_id = 0;
	if (faqId != null && faqId.matches("[0-9]+")) faq_id = Integer.parseInt(faqId);

	FaqDto dto = null;
	if (isAdmin) {
		FaqDao dao = new FaqDao();
		dto = dao.getFaqView(faq_id);
	}

	if (!isAdmin) {
%>
<script>
	alert("관리자만 사용할 수 있는 메뉴입니다.");
	location.href = "faq_list.jsp";
</script>
<%
	} else if (dto == null) {
%>
<script>
	alert("존재하지 않는 글입니다.");
	location.href = "faq_list.jsp";
</script>
<%
	} else {
		// 스크립틀릿의 dto 를 EL 에서 쓰기 위해 request 에 담는다
		request.setAttribute("dto", dto);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>FAQ 수정 | 인천공항 주차예약</title>
<link href="${pageContext.request.contextPath}/css/index1.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/faq.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/faq.js"></script>
</head>

<body>

<div class="wrap">

	<%@ include file="../common_header.jsp" %>

	<main class="main faq_page">
	<div class="container">

		<section class="section" style="padding-top:0">

			<div class="section_head">
				<div>
					<h2>FAQ 수정</h2>
					<p>${dto.reg_id} 등록 · ${dto.reg_date}</p>
				</div>
			</div>

			<form name="faq" method="post">
				<%-- 어느 글을 고칠지 알아야 하므로 글번호를 hidden 으로 같이 보낸다 --%>
				<input type="hidden" name="t_faq_id" value="${dto.faq_id}">

				<table class="faq_form">
					<tr>
						<th>카테고리</th>
						<td>
							<%-- 저장돼 있던 값에 selected. 값이 같은지만 비교하면 된다 --%>
							<select name="t_category">
								<option value="예약"      ${dto.category == '예약'      ? 'selected' : ''}>예약</option>
								<option value="요금·결제" ${dto.category == '요금·결제' ? 'selected' : ''}>요금·결제</option>
								<option value="입·출차"   ${dto.category == '입·출차'   ? 'selected' : ''}>입·출차</option>
								<option value="항공편"    ${dto.category == '항공편'    ? 'selected' : ''}>항공편</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>질문</th>
						<td><input type="text" name="t_question" maxlength="300" value="${dto.question}"></td>
					</tr>
					<tr>
						<th>답변</th>
						<td><textarea name="t_answer">${dto.answer}</textarea></td>
					</tr>
					<tr>
						<th>정렬순서</th>
						<td>
							<input type="number" name="t_sort_no" value="${dto.sort_no}" min="1" max="9999">
							<span class="faq_help">숫자가 작을수록 위에 표시. 같으면 등록순</span>
						</td>
					</tr>
					<tr>
						<th>노출여부</th>
						<td>
							<label><input type="radio" name="t_use_yn" value="Y" ${dto.use_yn != 'N' ? 'checked' : ''}> 노출</label>
							<label><input type="radio" name="t_use_yn" value="N" ${dto.use_yn == 'N' ? 'checked' : ''}> 숨김</label>
							<span class="faq_help">숨김이면 이용자 목록에는 안 나오고 관리자에게만 보입니다</span>
						</td>
					</tr>
				</table>

				<div class="faq_btns">
					<input type="button" value="수정완료" class="faq_btn faq_btn_primary" onclick="goUpdate()">
					<input type="reset" value="다시쓰기" class="faq_btn">
					<input type="button" value="목록" class="faq_btn" onclick="location.href='faq_list.jsp'">
				</div>
			</form>

		</section>

	</div>
	</main>

</div>

</body>
</html>
<%
	}
%>
