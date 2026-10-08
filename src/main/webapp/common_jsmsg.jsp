<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- JS 가 만드는 문구용 번역. common_header.jsp 가 include 하므로 헤더가 있는 화면은 자동으로 들어간다.
     JS 에서는 jm("이름", "한국어 기본값") 으로 읽는다. 값은 messages_xx.properties 에서 온다 (줄바꿈 없는 키만). --%>
<script>
window.LANG = "${empty sessionScope.lang ? 'ko' : sessionScope.lang}";
window.JSMSG = {
	longTerm: "<fmt:message key='idx.046'/>", shortTerm: "<fmt:message key='idx.042'/>",
	longShort: "<fmt:message key='map.062'/>", shortShort: "<fmt:message key='map.063'/>",
	free: "<fmt:message key='idx.039'/>", normal: "<fmt:message key='idx.040'/>", busy: "<fmt:message key='idx.041'/>",
	veryBusy: "<fmt:message key='idx.074'/>", noInfo: "<fmt:message key='idx.073'/>",
	carUnit: "<fmt:message key='idx.051'/>", seatUnit: "<fmt:message key='map.016'/>",
	perHour: "<fmt:message key='map.047'/>", won: "<fmt:message key='map.048'/>", zoneWord: "<fmt:message key='map.049'/>",
	ground: "<fmt:message key='map.050'/>", live: "<fmt:message key='map.051'/>", asOf: "<fmt:message key='map.052'/>",
	selectedSeat: "<fmt:message key='map.053'/>", noSeat: "<fmt:message key='map.026'/>",
	onlyDisabled: "<fmt:message key='map.054'/>", onlyEv: "<fmt:message key='map.055'/>", loginToReserve: "<fmt:message key='map.056'/>",
	flightSelected: "<fmt:message key='map.057'/>", selectPayMethod: "<fmt:message key='map.058'/>", confirmPay: "<fmt:message key='map.059'/>",
	payNotSupported: "<fmt:message key='map.060'/>", payFailed: "<fmt:message key='map.061'/>", t1: "<fmt:message key='map.064'/>",
	selectedTimeAsOf: "<fmt:message key='idx.070'/>", nowRealtime: "<fmt:message key='idx.054'/>",
	loadFail: "<fmt:message key='idx.071'/>", refreshFail: "<fmt:message key='idx.072'/>", selectZone: "<fmt:message key='idx.075'/>",
	nowAvailable: "<fmt:message key='idx.076'/>", goZoneReserve: "<fmt:message key='idx.077'/>", goFinalPay: "<fmt:message key='idx.078'/>",
	reportType: "<fmt:message key='msg.reportType'/>", reportTitle: "<fmt:message key='msg.reportTitle'/>",
	reportContent: "<fmt:message key='msg.reportContent'/>", reportConfirm: "<fmt:message key='rpt.014'/>",
	mem_id: "<fmt:message key='mem.001'/>", mem_idEmpty: "<fmt:message key='mem.002'/>", mem_comm: "<fmt:message key='mem.003'/>",
	mem_idTaken: "<fmt:message key='mem.004'/>", mem_idOk: "<fmt:message key='mem.005'/>", mem_pw: "<fmt:message key='join.009'/>",
	mem_phone: "<fmt:message key='mem.007'/>", mem_email: "<fmt:message key='mem.008'/>", mem_codeSent: "<fmt:message key='mem.009'/>",
	mem_sendError: "<fmt:message key='mem.010'/>", mem_codeEmpty: "<fmt:message key='mem.011'/>",
	rec_invalid: "<fmt:message key='rec.invalid'/>", rec_wait: "<fmt:message key='rec.wait'/>", rec_mail_error: "<fmt:message key='rec.mail_error'/>",
	rec_send_first: "<fmt:message key='rec.send_first'/>", rec_expired: "<fmt:message key='rec.expired'/>", rec_too_many: "<fmt:message key='rec.too_many'/>",
	rec_wrong_code: "<fmt:message key='rec.wrong_code'/>", rec_verify_first: "<fmt:message key='rec.verify_first'/>",
	rec_invalid_password: "<fmt:message key='rec.invalid_password'/>", rec_password_mismatch: "<fmt:message key='rec.password_mismatch'/>",
	rec_not_found: "<fmt:message key='rec.not_found'/>", rec_server_error: "<fmt:message key='rec.server_error'/>",
	rec_sent: "<fmt:message key='rec.sent'/>", rec_code6: "<fmt:message key='rec.code6'/>",
	arrWord: "<fmt:message key='flt.021'/>", fltUnit: "<fmt:message key='flt.022'/>", codeshareNote: "<fmt:message key='flt.023'/>",
	shown: "<fmt:message key='flt.024'/>", cache: "<fmt:message key='flt.025'/>", stale: "<fmt:message key='flt.026'/>",
	exit: "<fmt:message key='flt.027'/>", carousel: "<fmt:message key='flt.028'/>", pick: "<fmt:message key='flt.029'/>",
	actual: "<fmt:message key='flt.030'/>", rmkBefore: "<fmt:message key='flt.031'/>", today: "<fmt:message key='flt.032'/>",
	rmkPlan: "<fmt:message key='flt.007'/>", rmkArr: "<fmt:message key='flt.008'/>", rmkDelay: "<fmt:message key='flt.009'/>",
	rmkCancel: "<fmt:message key='flt.010'/>", rmkLand: "<fmt:message key='flt.033'/>", rmkDivert: "<fmt:message key='flt.034'/>"
};
function jm(key, fallback) { return (window.JSMSG && window.JSMSG[key]) || fallback; }
function typeLabel(t) { return t === "장기주차장" ? jm("longTerm", t) : (t === "단기주차장" ? jm("shortTerm", t) : t); }
function typeShort(t) { return t === "장기주차장" ? jm("longShort", "장기") : (t === "단기주차장" ? jm("shortShort", "단기") : String(t).replace("주차장", "")); }
function statusLabel(s) {
	var m = { "여유": jm("free", s), "보통": jm("normal", s), "혼잡": jm("busy", s), "매우 혼잡": jm("veryBusy", s), "정보 없음": jm("noInfo", s) };
	return m[s] || s;
}
</script>
