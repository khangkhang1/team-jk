/********조인********/
function goSave() {
	if (checkEmpty(mem.t_id, "ID 입력하세요!")) return;
	if (mem.t_id_check.value == "") {
		alert("ID 중복 검사 하시오.");
		return;
	}
	if (mem.t_id_check.value == "사용불가") {
		alert("사용불가한 ID입니다.");
		mem.t_id.focus();
		return;
	}
	if (checkEmpty(mem.t_password, "비밀번호 입력하세요!")) return;
	if (checkEmpty(mem.t_password_confirm, "비밀번호 확인 입력하세요!")) return;
	if (mem.t_password.value != mem.t_password_confirm.value) {
		alert("비밀번호가 같지않습니다.")
		mem.t_password_confirm.focus();
		return;
	}
	if (checkEmpty(mem.t_name, "성명 입력하세요!")) return;
	if (checkEmpty(mem.t_phone_number, "연락처 입력하세요!")) return;
	if (checkEmpty(mem.t_email, "이메일 입력하세요!")) return;
	if (checkEmpty(mem.t_vehicle_number, "차량 번호를 입력하세요!")) return;
	if (checkEmpty(mem.t_vehicle_type, "차량 종류를 선택하세요")) return;

	mem.t_gubun.value = "memberSave";
	mem.method = "post";
	mem.action = "Member";
	mem.submit();
}

function checkId() {
	if (checkEmpty(mem.t_id, "아이디 입력")) return;
	var id = mem.t_id.value;
	$.ajax({
		type: "POST",
		url: "MemberCheckId",
		async: false,
		data: "t_id=" + id,
		dataType: "text",
		error: function() {
			alert('통신 실패!!!!!');
		},
		success: function(data) {
			var result = $.trim(data);
			mem.t_id_check.value = result;
			//alert("=="+result+"==")
		}
	});
}
function setEmpty() {
	mem.t_id_check.value = "";
}
/********로그인********/
function goPassword() {
	mem.t_password.focus();
}
function memberLogin() {
	if (checkEmpty(mem.t_id, "ID 입력!")) return;
	if (checkEmpty(mem.t_password, "암호 입력!")) return;
	mem.t_gubun.value = "memberLogin";
	mem.method = "post";
	mem.action = "Member";
	mem.submit();
}
