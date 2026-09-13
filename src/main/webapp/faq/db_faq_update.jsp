<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*,dto.*,common.*" %>
<%--
 FAQ 수정 처리. faq_update.jsp 의 폼을 POST 로 받아 UPDATE 하고 목록으로 보낸다.
--%>
<%
	request.setCharacterEncoding("utf-8");

	String loginLevel = (String)session.getAttribute("sessionLevel");
	boolean isAdmin = (loginLevel != null && loginLevel.equals("admin"));

	String msg = "";

	if (!isAdmin) {
		msg = "관리자만 수정할 수 있습니다.";
	} else {
		FaqDao dao = new FaqDao();

		String faqId    = CommonUtil.getCheckNull(request.getParameter("t_faq_id"));
		String category = CommonUtil.getCheckNull(request.getParameter("t_category"));
		String question = CommonUtil.getCheckNull(request.getParameter("t_question"));
		String answer   = CommonUtil.getCheckNull(request.getParameter("t_answer"));
		String sortNo   = CommonUtil.getCheckNull(request.getParameter("t_sort_no"));
		String useYn    = CommonUtil.getCheckNull(request.getParameter("t_use_yn"));

		// 등록 때와 똑같이 따옴표를 엔티티로 바꿔서 저장 (db_faq_save.jsp 참고)
		question = CommonUtil.getSingleQuot(question);
		question = CommonUtil.getDoubleQuot(question);
		answer   = CommonUtil.getSingleQuot(answer);
		answer   = CommonUtil.getDoubleQuot(answer);

		int faq_id = 0;
		if (faqId.matches("[0-9]+")) faq_id = Integer.parseInt(faqId);

		int sort_no = 100;
		if (sortNo.matches("[0-9]+")) sort_no = Integer.parseInt(sortNo);

		if (useYn.equals("")) useYn = "Y";

		FaqDto dto = new FaqDto();
		dto.setFaq_id(faq_id);
		dto.setCategory(category);
		dto.setQuestion(question);
		dto.setAnswer(answer);
		dto.setSort_no(sort_no);
		dto.setUse_yn(useYn);

		int result = dao.faqUpdate(dto);
		msg = (result == 1) ? "수정되었습니다." : "수정에 실패했습니다.";
	}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>FAQ 수정</title>
</head>
<body>
<script>
	alert("<%=msg%>");
	location.href = "faq_list.jsp";
</script>
</body>
</html>
