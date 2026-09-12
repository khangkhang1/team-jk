package dto;

public class PaymentDto {
	
/*
	* payment_id	: 결제 id(pk)
	* reservation_id: 예약 id(reservation fk)
	* estimate_amount: 예상 금액
	* deposit_amount: 예약금
	* payment_method: 결제 수단(카카오페이, 네이버페이 등)
	* payment_date	: 결제 일자(시간 포함)
	* payment_type	: 결제 유형(1: 초기 예약(+), 2: 출차 결제(+), 3: 취소(-))
	
	--------------모두 not null--------------
*/
	private int payment_estimate_amount, payment_deposit_amount;
	private String payment_id, reservation_id, payment_method, payment_date, payment_type;
	
	//결제 시 생성자
	public PaymentDto(String payment_id, String reservation_id, int payment_deposit_amount, String payment_method,
				String payment_type, String payment_date) {
		this.payment_id 	= payment_id;
		this.reservation_id = reservation_id;
		this.payment_deposit_amount = payment_deposit_amount;
		this.payment_method = payment_method;
		this.payment_type 	= payment_type;
		this.payment_date 	= payment_date;
	}

	public String getPayment_id() {
		return payment_id;
	}

	public String getReservation_id() {
		return reservation_id;
	}

	public int getPayment_stimate_amount() {
		return payment_estimate_amount;
	}
	
	public int getPayment_deposit_amount() {
		return payment_deposit_amount;
	}

	public String getPayment_method() {
		return payment_method;
	}

	public String getPayment_type() {
		return payment_type;
	}
	
	public String getPayment_date() {
		return payment_date;
	}
	
}
