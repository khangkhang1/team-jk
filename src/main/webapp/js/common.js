function movePage(servlet, gubun) {
	go.t_gubun.value = gubun;
	go.method = "post";
	go.action = servlet;
	go.submit();
}
function checkEmpty(obj, msg) {
	if (obj.value == "") {
		alert(msg);
		obj.focus();
		return true;
	} else {
		return false;
	}
}