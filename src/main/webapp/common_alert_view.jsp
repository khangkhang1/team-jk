<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<form name="work">
	<input type="hidden" name="t_gubun" value="${t_gubun}">
	<!-- 최종 결제 커맨드(FinalPaymentPage.java)에서 받아오는 값 -->
	<input type="hidden" name="reservation_start_time" value="${dto.getReservation_start_time()}">
	<input type="hidden" name="seat_no" value="${dto.getSeat_no()}">
	<input type="hidden" name="paymentTime" value="${paymentTime}">
	<input type="hidden" name="totalPrice" value="${totalPrice}">
</form>
<script>
	alert("${t_msg}");
	work.method = "post";
	work.action = "${t_url}";
	work.submit();
</script>