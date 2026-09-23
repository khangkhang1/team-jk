// 관리자 대시보드 그래프 (Chart.js). 데이터 배열(dailyLabels / dailyResv / dailyPay)은 dashboard.jsp 가 서버 값으로 만든다.
// Chart.js 를 CDN 에서 못 받으면(오프라인) 그래프 대신 숨겨둔 표를 보여준다.
(function () {
	var canvas = document.getElementById("dailyChart");
	if (!canvas) return;

	if (typeof Chart === "undefined") {
		canvas.parentNode.hidden = true;
		var fb = document.querySelector(".chart_fallback");
		if (fb) fb.hidden = false;
		return;
	}

	new Chart(canvas, {
		data: {
			labels: dailyLabels,
			datasets: [
				{
					type: "bar",
					label: "예약 건수",
					data: dailyResv,
					backgroundColor: "rgba(23,105,210,.75)",
					borderRadius: 4,
					yAxisID: "yCnt"
				},
				{
					type: "line",
					label: "입금액(원)",
					data: dailyPay,
					borderColor: "#e8a033",
					backgroundColor: "#e8a033",
					tension: .3,
					pointRadius: 3,
					yAxisID: "yAmt"
				}
			]
		},
		options: {
			responsive: true,
			interaction: { mode: "index", intersect: false },
			plugins: { legend: { position: "bottom", labels: { boxWidth: 12, font: { size: 11 } } } },
			scales: {
				yCnt: { position: "left",  beginAtZero: true, ticks: { precision: 0 }, grid: { color: "#eef1f5" } },
				yAmt: { position: "right", beginAtZero: true, grid: { drawOnChartArea: false },
				        ticks: { callback: function (v) { return v.toLocaleString(); } } },
				x: { grid: { display: false }, ticks: { font: { size: 11 } } }
			}
		}
	});
})();

// 신고 처리(report_view.jsp) 저장 전 확인.
// "처리 완료 / 반려" 로 바꿀 때는 처리 내용을 반드시 적게 한다. 나중에 근거를 대야 하기 때문.
// (서버 command.manager.ReportAnswer 에서도 같은 검사를 한다 - 화면 검사만으로는 우회할 수 있어서)
function checkReportAnswer(form) {
	var status = form.t_report_status.value;
	var answer = form.t_answer_content.value.replace(/^\s+|\s+$/g, "");

	if (status === "3" || status === "4") {
		if (answer === "") {
			alert("처리 완료·반려로 바꿀 때는 처리 내용을 남겨야 합니다.");
			form.t_answer_content.focus();
			return false;
		}
	}
	return confirm("이 내용으로 저장하시겠습니까?");
}

// FAQ 등록/수정 폼 검사 (faq_form.jsp). 서버 command.faq.FaqSave / FaqUpdate 에서도 같은 검사를 한다.
function checkFaqForm(form) {
	if (isBlank(form.t_question.value)) {
		alert("질문을 입력하세요.");
		form.t_question.focus();
		return false;
	}
	if (isBlank(form.t_answer.value)) {
		alert("답변을 입력하세요.");
		form.t_answer.focus();
		return false;
	}
	return true;
}

// 공지사항 등록/수정 폼 검사 (notice_form.jsp)
function checkNoticeForm(form) {
	if (isBlank(form.t_title.value)) {
		alert("제목을 입력하세요.");
		form.t_title.focus();
		return false;
	}
	if (isBlank(form.t_content.value)) {
		alert("내용을 입력하세요.");
		form.t_content.focus();
		return false;
	}
	return true;
}

// 공백만 있는지
function isBlank(value) {
	return value.replace(/^\s+|\s+$/g, "") === "";
}
