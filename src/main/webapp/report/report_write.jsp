<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>문의하기 | 인천공항 주차예약</title>
<link href="${pageContext.request.contextPath}/css/index1.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/report.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/report.js"></script>
</head>

<body>

<div class="wrap">

	<%@ include file="../common_header.jsp" %>

	<main class="main rp_page">
	<div class="container">

		<section class="section" style="padding-top:0">

			<div class="section_head">
				<div>
					<h2>문의하기</h2>
					<p>문의 종류를 고르고 내용을 적어 주세요. 좌석이나 예약과 관련된 문의라면 번호를 같이 적어 주시면 더 빨리 확인할 수 있습니다.</p>
				</div>
			</div>

			<form name="report" method="post" action="${pageContext.request.contextPath}/Report" onsubmit="return checkReportForm(this)">
				<input type="hidden" name="t_gubun" value="save">

				<table class="rp_form">
					<tr>
						<th>문의 종류 <em>*</em></th>
						<td>
							<select name="t_report_type" class="rp_select">
								<option value="">문의 종류를 선택하세요</option>
								<c:forEach var="t" items="${types}">
									<option value="${t.key}" ${type == t.key ? 'selected' : ''}>${t.value}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th>제목 <em>*</em></th>
						<td><input type="text" name="t_title" maxlength="60" placeholder="예) 예약한 자리에 다른 차가 주차되어 있습니다"></td>
					</tr>
					<tr>
						<th>좌석 번호</th>
						<td>
							<input type="text" name="t_seat_no" maxlength="20" class="rp_short" placeholder="예) P1-07">
							<span class="rp_help">선택</span>
						</td>
					</tr>
					<tr>
						<th>예약 번호</th>
						<td>
							<input type="text" name="t_reservation_id" maxlength="30" class="rp_short" placeholder="예) R26-09-0010">
							<span class="rp_help">선택</span>
						</td>
					</tr>
					<tr>
						<th>내용 <em>*</em></th>
						<td>
							<textarea name="t_content" maxlength="600" placeholder="언제, 어디서, 어떤 일이 있었는지 적어 주세요."></textarea>
							<p class="rp_help rp_count"><span id="rpCount">0</span> / 600자</p>
						</td>
					</tr>
				</table>

				<div class="rp_btns">
					<button type="submit" class="rp_btn rp_btn_primary">문의 접수</button>
					<a href="${pageContext.request.contextPath}/Index" class="rp_btn">취소</a>
				</div>
			</form>

		</section>

	</div>
	</main>

	<%@ include file="../common_footer.jsp" %>

</div>

<script>
	(function() {
		var area = document.report.t_content;
		var count = document.getElementById("rpCount");
		area.addEventListener("input", function() { count.textContent = area.value.length; });
	})();
</script>

</body>
</html>
