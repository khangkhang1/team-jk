/********조인********/
function checkIdFormat() {
	var id = mem.t_id.value;

	// 영문 소문자와 숫자만 허용, 4~20자
	var idPattern = /^(?=.*[a-z])[a-zA-Z0-9!@#$%^&*_.-]{4,20}$/;
	if (!idPattern.test(id)) {
		alert("영문 소문자를 최소 1자 이상 포함하여 4~20자로 입력해주세요.\n 특수문자는 !@#$%^&*_-.만 가능합니다.");
		mem.t_id.focus();
		return false;
	}

	return true;
}
function checkPasswordFormat() {

	var password = mem.t_password.value;

	// 영문 소문자 최소 1자 포함, 8~16자
	var passwordPattern = /^(?=.*[a-z])[A-Za-z0-9!@#$%^&*_+?.-]{6,16}$/;

	if (!passwordPattern.test(password)) {
		alert("6~16자로 입력해주세요.특수문자는 !@#$%^&*_+?.-만 사용할 수 있습니다.");
		mem.t_password.focus();
		return false;
	}

	return true;
}
function checkPhoneFormat() {

	var phone_number = mem.t_phone_number.value;

	// 숫자와 -만 사용, 최소 11자
	var phonePattern = /^010-?\d{4}-?\d{4}$/;

	if (!phonePattern.test(phone_number)) {
		alert("올바른 형식의 전화번호를 입력해주세요");
		mem.t_phone_number.focus();
		return false;
	}

	return true;
}
function checkEmailFormat() {

	var email = mem.t_email.value;

	// 일반적인 이메일 형식 확인
	var emailPattern = /^[a-zA-Z0-9!@#$%^&*_.-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

	if (!emailPattern.test(email)) {
		alert("올바른 이메일 형식을 입력해주세요.");
		mem.t_email.focus();
		return false;
	}

	return true;
}
function goSave() {
	if (checkEmpty(mem.t_id, "ID 입력하세요!")) return;
	if (!checkIdFormat()) return;
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
	if (!checkPasswordFormat()) return;
	if (checkEmpty(mem.t_password_confirm, "비밀번호 확인 입력하세요!")) return;
	if (mem.t_password.value != mem.t_password_confirm.value) {
		alert("비밀번호가 같지않습니다.")
		mem.t_password_confirm.focus();
		return;
	}
	if (checkEmpty(mem.t_name, "성명 입력하세요!")) return;
	if (checkEmpty(mem.t_phone_number, "연락처 입력하세요!")) return;
	if (!checkPhoneFormat()) return;
	if (checkEmpty(mem.t_email, "이메일 입력하세요!")) return;
	if (!checkEmailFormat()) return;
	if (checkEmpty(mem.t_vehicle_number, "차량 번호를 입력하세요!")) return;
	if (checkEmpty(mem.t_vehicle_type, "차량 종류를 선택하세요")) return;

	mem.t_gubun.value = "memberSave";
	mem.method = "post";
	mem.action = "Member";
	mem.submit();
}

function checkId() {
	if (checkEmpty(mem.t_id, "아이디 입력")) return;
	if (!checkIdFormat()) return;
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
