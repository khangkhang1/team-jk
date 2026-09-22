function checkReportForm(form) {
	if (form.t_report_type.value === "") {
		alert("문의 종류를 선택하세요.");
		form.t_report_type.focus();
		return false;
	}
	if (trim(form.t_title.value) === "") {
		alert("제목을 입력하세요.");
		form.t_title.focus();
		return false;
	}
	if (trim(form.t_content.value) === "") {
		alert("내용을 입력하세요.");
		form.t_content.focus();
		return false;
	}
	return confirm("문의를 접수하시겠습니까?");
}

function trim(value) {
	return value.replace(/^\s+|\s+$/g, "");
}
