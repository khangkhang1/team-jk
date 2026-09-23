<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
 FAQ 등록 / 수정 (관리자 콘솔 안). dto 가 있으면 수정, 없으면 등록.
 한 화면으로 겸용하는 이유 : 입력 항목이 같은데 화면을 둘로 나누면 고칠 때 두 번 고쳐야 한다.
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>FAQ ${empty dto ? '등록' : '수정'} | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/manager.js"></script>
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<section class="card form_card">
			<div class="card_head">
				<h2>FAQ ${empty dto ? '등록' : '수정'}
					<c:if test="${not empty dto}"><span class="dim">#${dto.faq_id}</span></c:if>
				</h2>
				<a class="card_link" href="${ctx}/Manager?t_gubun=faq">목록으로</a>
			</div>

			<form method="post" action="${ctx}/Manager" onsubmit="return checkFaqForm(this)">
				<input type="hidden" name="t_gubun" value="${empty dto ? 'faqSave' : 'faqUpdate'}">
				<c:if test="${not empty dto}">
					<input type="hidden" name="t_faq_id" value="${dto.faq_id}">
				</c:if>

				<table class="dl form_tbl">
					<tr>
						<th>카테고리</th>
						<td>
							<select name="t_category" class="adm_sel">
								<c:forEach var="cg" items="${categories}">
									<option value="${cg}" ${dto.category == cg ? 'selected' : ''}>${cg}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th>질문</th>
						<td><input type="text" name="t_question" class="adm_input" maxlength="150"
							value="${dto.question}" placeholder="예) 주차 예약은 며칠 전부터 할 수 있나요?"></td>
					</tr>
					<tr>
						<th>답변</th>
						<td><textarea name="t_answer" class="adm_area" rows="10"
							placeholder="이용자에게 보여줄 답변을 적습니다.">${dto.answer}</textarea></td>
					</tr>
					<tr>
						<th>정렬 번호</th>
						<td>
							<input type="number" name="t_sort_no" class="adm_input adm_input_sm"
								value="${empty dto ? 100 : dto.sort_no}" min="1" max="9999">
							<span class="card_note">작을수록 위에 나옵니다. 보통 100 으로 두면 됩니다.</span>
						</td>
					</tr>
					<tr>
						<th>노출</th>
						<td>
							<select name="t_use_yn" class="adm_sel">
								<option value="Y" ${dto.use_yn == 'N' ? '' : 'selected'}>노출 (이용자에게 보임)</option>
								<option value="N" ${dto.use_yn == 'N' ? 'selected' : ''}>숨김 (관리자만 보임)</option>
							</select>
						</td>
					</tr>
				</table>

				<div class="form_foot">
					<button type="submit" class="adm_btn adm_btn_green">${empty dto ? '등록' : '수정'}</button>
					<a href="${ctx}/Manager?t_gubun=faq" class="adm_btn adm_btn_ghost">취소</a>
				</div>
			</form>
		</section>

	</main>
</div><%-- .adm_main (manager_menu.jsp 에서 열림) --%>

</body>
</html>
