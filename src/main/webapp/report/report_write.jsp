<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><fmt:message key="rpt.001"/></title>
<link href="${pageContext.request.contextPath}/css/index1.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/report.css?v=20260922" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/report.js?v=20260922"></script>
</head>

<body>

<div class="wrap">

	<%@ include file="../common_header.jsp" %>

	<main class="main rp_page">
	<div class="container">

		<section class="section" style="padding-top:0">

			<div class="section_head">
				<div>
					<h2><fmt:message key="hdr.013"/></h2>
					<p><fmt:message key="rpt.002"/></p>
				</div>
			</div>

			<form name="report" method="post" action="${pageContext.request.contextPath}/Report" onsubmit="return checkReportForm(this)">
				<input type="hidden" name="t_gubun" value="save">

				<table class="rp_form">
					<tr>
						<th><fmt:message key="rpt.003"/> <em>*</em></th>
						<td>
							<select name="t_report_type" class="rp_select">
								<option value=""><fmt:message key="rpt.004"/></option>
								<c:forEach var="t" items="${types}">
									<option value="${t.key}" ${type == t.key ? 'selected' : ''}><fmt:message key="rpt.type.${t.key}"/></option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th><fmt:message key="ntc.005"/> <em>*</em></th>
						<td><input type="text" name="t_title" maxlength="60" placeholder="<fmt:message key='rpt.010'/>"></td>
					</tr>
					<tr>
						<th><fmt:message key="rpt.005"/></th>
						<td>
							<input type="text" name="t_seat_no" maxlength="20" class="rp_short" placeholder="<fmt:message key='rpt.011'/>">
							<span class="rp_help"><fmt:message key="rpt.006"/></span>
						</td>
					</tr>
					<tr>
						<th><fmt:message key="rpt.007"/></th>
						<td>
							<input type="text" name="t_reservation_id" maxlength="30" class="rp_short" placeholder="<fmt:message key='rpt.012'/>">
							<span class="rp_help"><fmt:message key="rpt.006"/></span>
						</td>
					</tr>
					<tr>
						<th><fmt:message key="ntc.006"/> <em>*</em></th>
						<td>
							<textarea name="t_content" maxlength="600" placeholder="<fmt:message key='rpt.013'/>"></textarea>
							<p class="rp_help rp_count"><span id="rpCount">0</span> <fmt:message key="rpt.008"/></p>
						</td>
					</tr>
				</table>

				<div class="rp_btns">
					<button type="submit" class="rp_btn rp_btn_primary"><fmt:message key="rpt.009"/></button>
					<a href="${pageContext.request.contextPath}/ParkingStatus" class="rp_btn"><fmt:message key="join.022"/></a>
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
