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
