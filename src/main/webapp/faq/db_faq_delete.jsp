<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.*,common.*" %>
<%--
 FAQ 삭제 처리. 목록의 [삭제] → faq.js goDelete(faq_id) (confirm 후) → 여기로 t_faq_id 가 POST 된다.
 실제로 행을 지운다. 잠깐 감추기만 할 거면 수정 화면에서 노출여부를 "숨김"으로.
--%>
<%
	request.setCharacterEncoding("utf-8");

	String loginLevel = (String)session.getAttribute("sessionLevel");
	boolean isAdmin = (loginLevel != null && loginLevel.equals("admin"));

	String msg = "";

	if (!isAdmin) {
		msg = "관리자만 삭제할 수 있습니다.";
	} else {
		FaqDao dao = new FaqDao();

		String faqId = CommonUtil.getCheckNull(request.getParameter("t_faq_id"));
		int faq_id = 0;
		if (faqId.matches("[0-9]+")) faq_id = Integer.parseInt(faqId);

		int result = dao.faqDelete(faq_id);
		msg = (result == 1) ? "삭제되었습니다." : "삭제에 실패했습니다.";
	}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>FAQ 삭제</title>
</head>
<body>
<script>
	alert("<%=msg%>");
	location.href = "faq_list.jsp";
</script>
</body>
</html>
