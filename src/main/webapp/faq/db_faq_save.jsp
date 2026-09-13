<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*,dto.*,common.*" %>
<%--
 FAQ 등록 처리. faq_write.jsp 의 폼을 POST 로 받아 DB 에 넣고 결과를 alert 로 알린 뒤 목록으로 보낸다.
 화면이 없는 "처리 전용" JSP - 수업의 db_xxx_save.jsp 와 같은 역할.
--%>
<%
	// POST 로 온 한글 파라미터가 깨지지 않게 getParameter 보다 먼저 호출해야 한다
	request.setCharacterEncoding("utf-8");

	// 등록 화면에서 막았어도 처리 페이지를 직접 호출할 수 있으니 여기서도 관리자 확인
	String loginLevel = (String)session.getAttribute("sessionLevel");
	boolean isAdmin = (loginLevel != null && loginLevel.equals("admin"));

	String msg = "";

	if (!isAdmin) {
		msg = "관리자만 등록할 수 있습니다.";
	} else {
		FaqDao dao = new FaqDao();

		// getCheckNull : null 이면 "" 로. 뒤의 replaceAll 이 null 에서 터지는 걸 막는다
		String category = CommonUtil.getCheckNull(request.getParameter("t_category"));
		String question = CommonUtil.getCheckNull(request.getParameter("t_question"));
		String answer   = CommonUtil.getCheckNull(request.getParameter("t_answer"));
		String sortNo   = CommonUtil.getCheckNull(request.getParameter("t_sort_no"));
		String useYn    = CommonUtil.getCheckNull(request.getParameter("t_use_yn"));
		String reg_id   = (String)session.getAttribute("sessionId");   // 로그인한 관리자 ID

		// 작은따옴표(')는 SQL 문자열을 끊어버리고, 큰따옴표(")는 수정 화면의 value="" 를 끊는다.
		// 둘 다 HTML 엔티티로 바꿔서 저장한다 (수업 방식). 브라우저가 다시 ' " 로 보여준다.
		question = CommonUtil.getSingleQuot(question);
		question = CommonUtil.getDoubleQuot(question);
		answer   = CommonUtil.getSingleQuot(answer);
		answer   = CommonUtil.getDoubleQuot(answer);

		// 정렬순서 : 비웠거나 숫자가 아니면 DB 기본값과 같은 100
		int sort_no = 100;
		if (sortNo.matches("[0-9]+")) sort_no = Integer.parseInt(sortNo);

		if (useYn.equals("")) useYn = "Y";
		if (reg_id == null) reg_id = "manager";

		FaqDto dto = new FaqDto();
		dto.setCategory(category);
		dto.setQuestion(question);
		dto.setAnswer(answer);
		dto.setSort_no(sort_no);
		dto.setUse_yn(useYn);
		dto.setReg_id(reg_id);

		int result = dao.faqSave(dto);
		msg = (result == 1) ? "등록되었습니다." : "등록에 실패했습니다.";
	}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>FAQ 등록</title>
</head>
<body>
<script>
	alert("<%=msg%>");
	location.href = "faq_list.jsp";
</script>
</body>
</html>
