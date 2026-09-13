// FAQ 화면 공통 스크립트 (강선구)
// faq_list.jsp / faq_write.jsp / faq_update.jsp 가 같이 쓴다.
// 세 화면 모두 <form name="faq" action="Faq"> 를 갖고 있어서, t_gubun 만 채우고 submit 하면
// Faq 서블릿이 분기한다 (팀 공통 common.js 의 movePage 와 같은 방식).

// 빈 값 검사. 비어 있으면 alert 띄우고 그 칸에 커서를 옮긴 뒤 true 를 돌려준다.
function isEmpty(obj, msg) {
	if (obj.value.trim() == "") {
		alert(msg);
		obj.focus();
		return true;
	}
	return false;
}

// 목록 → 수정 화면
function goUpdateForm(faq_id) {
	var form = document.faq;
	form.t_gubun.value = "updateForm";
	form.t_faq_id.value = faq_id;
	form.submit();
}

// 목록 → 삭제. 되돌릴 수 없으니 confirm 을 한 번 거친다
function goDelete(faq_id) {
	if (!confirm("정말 삭제하시겠습니까?\n삭제한 글은 되돌릴 수 없습니다.\n(잠시 감추기만 하려면 [수정] → 노출여부 '숨김')")) return;
	var form = document.faq;
	form.t_gubun.value = "delete";
	form.t_faq_id.value = faq_id;
	form.submit();
}

// 등록 화면 [저장]
function goSave() {
	var form = document.faq;
	if (isEmpty(form.t_question, "질문을 입력하세요.")) return;
	if (isEmpty(form.t_answer,   "답변을 입력하세요.")) return;
	form.t_gubun.value = "save";
	form.submit();
}

// 수정 화면 [수정완료]
function goUpdate() {
	var form = document.faq;
	if (isEmpty(form.t_question, "질문을 입력하세요.")) return;
	if (isEmpty(form.t_answer,   "답변을 입력하세요.")) return;
	form.t_gubun.value = "update";
	form.submit();
}
