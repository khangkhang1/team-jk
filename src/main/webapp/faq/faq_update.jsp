<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%--
 FAQ 수정 화면. Faq?t_gubun=updateForm&t_faq_id=N 이 dto 를 담아 forward 한다.
 [수정완료] -> faq.js goUpdate() -> Faq?t_gubun=update
--%>
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

			<form name="faq" method="post" action="${pageContext.request.contextPath}/Faq">
				<input type="hidden" name="t_gubun">
				<input type="hidden" name="t_faq_id" value="${dto.faq_id}">

				<table class="faq_form">
					<tr>
						<th>카테고리</th>
						<td>
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
					<input type="button" value="목록" class="faq_btn" onclick="location.href='${pageContext.request.contextPath}/Faq'">
				</div>
			</form>

		</section>

	</div>
	</main>

</div>

</body>
</html>
