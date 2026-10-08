<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String msg = (String) request.getAttribute("t_msg");
String url = (String) request.getAttribute("t_url");
if (msg == null) msg = "";
if (url == null) url = "Member";
String safeMsg = msg.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "\\n");
String safeUrl = url.replace("'", "\\'");
%>
<!DOCTYPE html>
<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}"><head><meta charset="UTF-8"><title><fmt:message key="alert.001"/></title></head>
<body><script>alert("<%= safeMsg %>"); location.replace('<%= safeUrl %>');</script></body></html>
