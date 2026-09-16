<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
 신고 상세 + 처리. Manager 서블릿이 dto / rid / listUrl 을 넘긴다.
 신고 내용과 처리 내용은 이용자가 쓴 글이므로 <c:out> 으로 출력한다.
 태그 문자를 그대로 뿌리면 화면이 깨지거나 스크립트가 섞여 들어올 수 있어서다 (出力時エスケープ).
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>신고 상세 | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/manager.js"></script>
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<c:if test="${empty dto}">
			<section class="card">
				<div class="card_head">
					<h2>신고 상세</h2>
					<a class="card_link" href="${ctx}/${listUrl}">목록으로</a>
				</div>
				<p class="empty">"<c:out value="${rid}"/>" 번 신고를 찾을 수 없습니다.</p>
			</section>
		</c:if>

		<c:if test="${not empty dto}">
		<section class="grid_2_1">

			<%-- 왼쪽 : 신고 내용 --%>
			<div class="card">
				<div class="card_head">
					<h2>신고 ${dto.report_id}번 <span class="badge rp${dto.report_status}">${dto.status_label}</span></h2>
					<a class="card_link" href="${ctx}/${listUrl}">목록으로</a>
				</div>

				<table class="dl">
					<tr><th>유형</th><td>${dto.type_label}</td></tr>
					<tr><th>제목</th><td><strong><c:out value="${dto.title}"/></strong></td></tr>
					<tr><th>신고자</th><td>
						${dto.member_name}
						<c:if test="${not empty dto.member_id}"><span class="dim">(${dto.member_id})</span></c:if>
						<c:if test="${not empty dto.phone_number}"> · ${dto.phone_number}</c:if>
					</td></tr>
					<tr><th>좌석</th><td>
						<c:choose>
							<c:when test="${empty dto.seat_no}"><span class="dim">지정 없음</span></c:when>
							<c:otherwise>
								<strong>${dto.seat_no}</strong>
								<c:if test="${not empty dto.lot_id}">
									· <a class="card_link" style="margin:0" href="${ctx}/Manager?t_gubun=seat&t_lot=${dto.lot_id}">${dto.lot_id} 구역 현황 보기</a>
								</c:if>
							</c:otherwise>
						</c:choose>
					</td></tr>
					<tr><th>관련 예약</th><td>
						<c:choose>
							<c:when test="${empty dto.reservation_id}"><span class="dim">없음</span></c:when>
							<c:otherwise>
								<a class="card_link" style="margin:0" href="${ctx}/Manager?t_gubun=gate&t_reservation_id=${dto.reservation_id}">${dto.reservation_id}</a>
							</c:otherwise>
						</c:choose>
					</td></tr>
					<tr><th>접수 일시</th><td>${dto.reg_date}
						<c:if test="${dto.report_status == '1' or dto.report_status == '2'}">
							<span class="badge ${dto.elapsed_days >= 3 ? 'warn' : 'ok'}">${dto.elapsed_days}일 경과</span>
						</c:if>
					</td></tr>
				</table>

				<h3 class="sub_h">신고 내용</h3>
				<div class="doc"><c:out value="${dto.content}"/></div>
			</div>

			<%-- 오른쪽 : 처리 --%>
			<div class="card">
				<div class="card_head"><h2>처리</h2></div>

				<c:if test="${not empty dto.answer_content}">
					<h3 class="sub_h">지금까지의 처리 내용</h3>
					<div class="doc"><c:out value="${dto.answer_content}"/></div>
					<p class="card_note" style="margin:8px 0 18px">
						${dto.answer_id} · ${dto.answer_date}
					</p>
				</c:if>

				<form method="post" action="${ctx}/Manager" onsubmit="return checkReportAnswer(this)">
					<input type="hidden" name="t_gubun" value="reportAnswer">
					<input type="hidden" name="t_report_id" value="${dto.report_id}">

					<div class="adm_form" style="margin-bottom:10px">
						<label>처리 상태</label>
						<select name="t_report_status">
							<option value="1" ${dto.report_status == '1' ? 'selected' : ''}>접수</option>
							<option value="2" ${dto.report_status == '2' ? 'selected' : ''}>처리 중</option>
							<option value="3" ${dto.report_status == '3' ? 'selected' : ''}>처리 완료</option>
							<option value="4" ${dto.report_status == '4' ? 'selected' : ''}>반려</option>
						</select>
					</div>

					<textarea name="t_answer_content" class="adm_area" rows="9"
						placeholder="어떻게 조치했는지 적습니다. 예) 현장 확인 후 차주에게 연락하여 이동 조치했습니다."><c:out value="${dto.answer_content}"/></textarea>

					<p class="card_note" style="margin:8px 0 14px">처리 완료·반려로 바꿀 때는 처리 내용을 남겨야 저장됩니다.</p>
					<button type="submit" class="adm_btn adm_btn_green">저장</button>
				</form>
			</div>

		</section>
		</c:if>

	</main>
</div><%-- .adm_main (manager_menu.jsp 에서 열림) --%>

</body>
</html>
