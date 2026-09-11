<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<form name="work">
	<input type="hidden" name="t_gubun" value="${t_gubun}">
	<input type="hidden" name="t_id" value="${t_id}">
	<input type="hidden" name="t_no" value="${t_no}">
	<input type="hidden" name="t_order_no" value="${t_o_no}">
	<input type="hidden" name="start_date" value="${start_date}">
	<input type="hidden" name="end_date" value="${end_date}">
	<input type="hidden" name="t_search" value="${t_search}">
</form>
<script>
	alert("${t_msg}");
	work.method = "post";
	work.action = "${t_url}";
	work.submit();
</script>