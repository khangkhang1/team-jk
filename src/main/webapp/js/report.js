function checkReportForm(form) {
	if (form.t_report_type.value === "") {
		alert(jm("reportType", "문의 종류를 선택하세요."));
		form.t_report_type.focus();
		return false;
	}
	if (trim(form.t_title.value) === "") {
		alert(jm("reportTitle", "제목을 입력하세요."));
		form.t_title.focus();
		return false;
	}
	if (trim(form.t_content.value) === "") {
		alert(jm("reportContent", "내용을 입력하세요."));
		form.t_content.focus();
		return false;
	}
	return confirm(jm("reportConfirm", "문의를 접수하시겠습니까?"));
}

function trim(value) {
	return value.replace(/^\s+|\s+$/g, "");
}
