<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%--
 FAQ 등록 화면 (관리자 전용). 저장 버튼 → faq.js goSave() → db_faq_save.jsp
--%>
<%
	// 관리자 전용 화면. 목록에서 버튼을 숨겨도 주소를 직접 치면 들어올 수 있으니
	// 화면 자체에서 한 번 더 막는다. (수업 faq_update.jsp 와 같은 방식)
	String loginLevel = (String)session.getAttribute("sessionLevel");
	boolean isAdmin = (loginLevel != null && loginLevel.equals("admin"));

	if (!isAdmin) {
%>
<script>
	alert("관리자만 사용할 수 있는 메뉴입니다.");
	location.href = "faq_list.jsp";
</script>
<%
	} else {
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>FAQ 등록 | 인천공항 주차예약</title>
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
					<h2>FAQ 등록</h2>
					<p>자주 묻는 질문을 새로 등록합니다.</p>
				</div>
			</div>

			<%-- 파라미터는 팀 규칙대로 전부 t_ 접두어 --%>
			<form name="faq" method="post">
				<table class="faq_form">
					<tr>
						<th>카테고리</th>
						<td>
							<select name="t_category">
								<option value="예약">예약</option>
								<option value="요금·결제">요금·결제</option>
								<option value="입·출차">입·출차</option>
								<option value="항공편">항공편</option>
							</select>
						</td>
					</tr>
					<tr>
						<th>질문</th>
						<td><input type="text" name="t_question" maxlength="300" placeholder="예) 예약을 취소하면 예약금은 돌려받나요?"></td>
					</tr>
					<tr>
						<th>답변</th>
						<td><textarea name="t_answer" placeholder="줄바꿈은 그대로 화면에 반영됩니다."></textarea></td>
					</tr>
					<tr>
						<th>정렬순서</th>
						<td>
							<input type="number" name="t_sort_no" value="100" min="1" max="9999">
							<span class="faq_help">숫자가 작을수록 위에 표시. 같으면 등록순</span>
						</td>
					</tr>
					<tr>
						<th>노출여부</th>
						<td>
							<label><input type="radio" name="t_use_yn" value="Y" checked> 노출</label>
							<label><input type="radio" name="t_use_yn" value="N"> 숨김</label>
							<span class="faq_help">숨김이면 이용자 목록에는 안 나오고 관리자에게만 보입니다</span>
						</td>
					</tr>
				</table>

				<div class="faq_btns">
					<input type="button" value="저장" class="faq_btn faq_btn_primary" onclick="goSave()">
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
