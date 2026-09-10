<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String msg = (String) request.getAttribute("t_msg");
String url = (String) request.getAttribute("t_url");
if (msg == null) msg = "";
if (url == null) url = "Member";
String safeMsg = msg.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "\\n");
String safeUrl = url.replace("'", "\\'");
%>
<!DOCTYPE html>
<html lang="ko"><head><meta charset="UTF-8"><title>알림</title></head>
<body><script>alert("<%= safeMsg %>"); location.replace('<%= safeUrl %>');</script></body></html>
