package dto;

/**
 * 신고 한 건 (icn_report). 강선구 담당.
 *
 * 관리자 콘솔의 다른 화면(대시보드·매출)은 여러 테이블을 합쳐 "숫자"만 뽑아서 HashMap 으로 받지만,
 * 신고는 등록·조회·처리가 있는 보통의 CRUD 라 팀의 다른 게시판(FaqDto)과 똑같이 DTO 를 둔다.
 *
 * 라벨(type_label, status_label)과 경과일(elapsed_days)은 DB 에 없는 값으로,
 * SELECT 할 때 DECODE / 날짜 계산으로 만들어 담는다. 화면마다 if 문으로 한글을 붙이지 않기 위해서다.
 */
public class ReportDto {

	private int report_id;
	private int elapsed_days;                                    // 접수 후 지난 날짜 (미처리 방치 확인용)

	private String report_type, type_label;                      // 1 무단점유 / 2 시설 파손·고장 / 3 차량 훼손 / 4 불법 주차 / 5 기타
	private String report_status, status_label;                  // 1 접수 / 2 처리 중 / 3 처리 완료 / 4 반려
	private String title, content;
	private String member_id, member_name, phone_number;
	private String seat_no, lot_id, reservation_id;
	private String reg_date;
	private String answer_content, answer_id, answer_date;

	public ReportDto() {
	}

	public int getReport_id() {
		return report_id;
	}
	public void setReport_id(int report_id) {
		this.report_id = report_id;
	}
	public int getElapsed_days() {
		return elapsed_days;
	}
	public void setElapsed_days(int elapsed_days) {
		this.elapsed_days = elapsed_days;
	}
	public String getReport_type() {
		return report_type;
	}
	public void setReport_type(String report_type) {
		this.report_type = report_type;
	}
	public String getType_label() {
		return type_label;
	}
	public void setType_label(String type_label) {
		this.type_label = type_label;
	}
	public String getReport_status() {
		return report_status;
	}
	public void setReport_status(String report_status) {
		this.report_status = report_status;
	}
	public String getStatus_label() {
		return status_label;
	}
	public void setStatus_label(String status_label) {
		this.status_label = status_label;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getSeat_no() {
		return seat_no;
	}
	public void setSeat_no(String seat_no) {
		this.seat_no = seat_no;
	}
	public String getLot_id() {
		return lot_id;
	}
	public void setLot_id(String lot_id) {
		this.lot_id = lot_id;
	}
	public String getReservation_id() {
		return reservation_id;
	}
	public void setReservation_id(String reservation_id) {
		this.reservation_id = reservation_id;
	}
	public String getReg_date() {
		return reg_date;
	}
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	public String getAnswer_content() {
		return answer_content;
	}
	public void setAnswer_content(String answer_content) {
		this.answer_content = answer_content;
	}
	public String getAnswer_id() {
		return answer_id;
	}
	public void setAnswer_id(String answer_id) {
		this.answer_id = answer_id;
	}
	public String getAnswer_date() {
		return answer_date;
	}
	public void setAnswer_date(String answer_date) {
		this.answer_date = answer_date;
	}

}
