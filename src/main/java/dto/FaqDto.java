package dto;

public class FaqDto {
	private int faq_id,sort_no,hit;
	private String category,question,answer,use_yn,reg_id,reg_date;
	// 기본 생성자. 자바는 생성자를 하나라도 선언하면 기본 생성자를 자동으로 안 만들어준다.
	// DAO에서 new FaqDto() 후 setter로 채우는 방식을 쓰려면 이게 있어야 한다.
	public FaqDto() {
	}

	public FaqDto(int faq_id, int sort_no, int hit, String category, String question, String answer, String use_yn,
			String reg_id, String reg_date) {
		super();
		this.faq_id = faq_id;
		this.sort_no = sort_no;
		this.hit = hit;
		this.category = category;
		this.question = question;
		this.answer = answer;
		this.use_yn = use_yn;
		this.reg_id = reg_id;
		this.reg_date = reg_date;
	}
	public int getFaq_id() {
		return faq_id;
	}
	public void setFaq_id(int faq_id) {
		this.faq_id = faq_id;
	}
	public int getSort_no() {
		return sort_no;
	}
	public void setSort_no(int sort_no) {
		this.sort_no = sort_no;
	}
	public int getHit() {
		return hit;
	}
	public void setHit(int hit) {
		this.hit = hit;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getUse_yn() {
		return use_yn;
	}
	public void setUse_yn(String use_yn) {
		this.use_yn = use_yn;
	}
	public String getReg_id() {
		return reg_id;
	}
	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}
	public String getReg_date() {
		return reg_date;
	}
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	
	
}
