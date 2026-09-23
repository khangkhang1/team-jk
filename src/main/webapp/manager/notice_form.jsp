<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
 공지사항 등록 / 수정 (관리자 콘솔 안). dto 가 있으면 수정, 없으면 등록.
 공지 내용은 저장할 때 손대지 않고 넣으므로 출력할 때 c:out 으로 이스케이프한다 (出力時エスケープ).
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>공지사항 ${empty dto ? '등록' : '수정'} | 관리자 콘솔</title>
<link href="${pageContext.request.contextPath}/css/manager.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/manager.js"></script>
</head>

<body class="adm">

	<%@ include file="manager_menu.jsp" %>

	<main class="adm_content">

		<section class="card form_card">
			<div class="card_head">
				<h2>공지사항 ${empty dto ? '등록' : '수정'}
					<c:if test="${not empty dto}"><span class="dim">${dto.no}번 · 조회 ${dto.hit}</span></c:if>
				</h2>
				<a class="card_link" href="${ctx}/Manager?t_gubun=notice">목록으로</a>
			</div>

			<form method="post" action="${ctx}/Manager" onsubmit="return checkNoticeForm(this)">
				<input type="hidden" name="t_gubun" value="${empty dto ? 'noticeSave' : 'noticeUpdate'}">
				<c:if test="${not empty dto}">
					<input type="hidden" name="t_no" value="${dto.no}">
				</c:if>

				<table class="dl form_tbl">
					<tr>
						<th>제목</th>
						<td><input type="text" name="t_title" class="adm_input" maxlength="100"
							value="<c:out value='${dto.title}'/>" placeholder="예) 추석 연휴 주차장 혼잡 안내"></td>
					</tr>
					<tr>
						<th>내용</th>
						<td><textarea name="t_content" class="adm_area" rows="12"
							placeholder="공지 내용을 적습니다. 줄바꿈은 그대로 보입니다."><c:out value="${dto.content}"/></textarea></td>
					</tr>
					<tr>
						<th>중요 공지</th>
						<td>
							<label class="chk">
								<input type="checkbox" name="t_important" value="Y" ${dto.important == 'Y' ? 'checked' : ''}>
								목록 맨 위에 고정하고 "중요" 표시를 답니다
							</label>
						</td>
					</tr>
					<c:if test="${not empty dto}">
						<tr>
							<th>등록</th>
							<td class="dim">${dto.reg_id} · ${dto.reg_date}</td>
						</tr>
					</c:if>
				</table>

				<div class="form_foot">
					<button type="submit" class="adm_btn adm_btn_green">${empty dto ? '등록' : '수정'}</button>
					<a href="${ctx}/Manager?t_gubun=notice" class="adm_btn adm_btn_ghost">취소</a>
				</div>
			</form>
		</section>

	</main>
</div><%-- .adm_main (manager_menu.jsp 에서 열림) --%>

</body>
</html>
