package dto;

public class ReservationInfoDto {
/*
	* reservation_id		: 예약 번호(pk)
	* flight_id				: 항공기 번호(pk)
	* member_id				: 예약 회원 id(member fk)
	* seat_no				: 좌석 번호(seat fk)
	* reservation_status	: 예약 상태(not null / 1: 예약완료(주차 이전), 2: 주차 도중, 3: 출차 완료, 4: 예약 취소)
	* reservation_start_time: 예약 시 선택한 주차 시작 시각(not null)
	* reservation_end_time	: 예약 시 주차 종료 시각(단기 주차의 경우에만 등록)
	* reservation_out_time	: 실제 출차 시각
	* reservation_type		: 예약 주차 유형(not null / 1: 예약형 or 2: 자유출차형)
*/
	private String reservation_id, flight_no, reservation_status, reservation_start_date, reservation_start_time,
					reservation_end_date, reservation_end_time,
					reservation_out_time, reservation_type, member_id, seat_no;
	
	
	//예약 생성자(왕복 고려X / 2유형 장기주차)
	public ReservationInfoDto(String reservation_id, String reservation_status, String reservation_start_date,
			String reservation_start_time, String reservation_type,
			String member_id, String seat_no) {
		this.reservation_id = reservation_id;
		this.reservation_status = reservation_status;
		this.reservation_start_date = reservation_start_date;
		this.reservation_start_time = reservation_start_time;
		this.reservation_type = reservation_type;
		this.member_id = member_id;
		this.seat_no = seat_no;
	}
	
	//예약 생성자(왕복 고려O / 1유형 단기주차)
	public ReservationInfoDto(String reservation_id, String flight_no, String reservation_status, String reservation_start_date,
			String reservation_start_time, String reservation_end_date, String reservation_end_time,
			String reservation_type, String member_id, String seat_no) {
		this.reservation_id = reservation_id;
		this.flight_no = flight_no;
		this.reservation_status = reservation_status;
		this.reservation_start_date = reservation_start_date;
		this.reservation_start_time = reservation_start_time;
		this.reservation_end_date = reservation_end_date;
		this.reservation_end_time = reservation_end_time;
		this.reservation_type = reservation_type;
		this.member_id = member_id;
		this.seat_no = seat_no;
	}
	
	public String getReservation_id() {
		return reservation_id;
	}
	public String getFlight_no() {
		return flight_no;
	}
	public String getReservation_status() {
		return reservation_status;
	}
	public String getReservation_start_time() {
		return reservation_start_time;
	}
	public String getReservation_end_time() {
		return reservation_end_time;
	}
	public String getReservation_out_time() {
		return reservation_out_time;
	}
	public String getReservation_type() {
		return reservation_type;
	}
	public String getMember_id() {
		return member_id;
	}
	public String getSeat_no() {
		return seat_no;
	}

	public String getReservation_start_date() {
		return reservation_start_date;
	}

	public String getReservation_end_date() {
		return reservation_end_date;
	}
	
	
	
}
