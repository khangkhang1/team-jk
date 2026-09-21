package dto;

/**
 * 공지사항 한 건 (icn_notice). 관리자 콘솔에서 쓰려고 강선구가 만들었습니다.
 *
 * 이용자용 공지 게시판(notice/notice_list.jsp 등)은 정규상 파트입니다.
 * 화면은 따로지만 테이블은 같으므로, 정규상 쪽 DAO 가 생기면 이 DTO 로 합치면 됩니다.
 *
 * 기본키 no 는 시퀀스가 아니라 VARCHAR2(4) 라서 '0001' 처럼 0 을 채운 문자열입니다.
 */
public class NoticeDto {

	private String no;                 // 0001 형식 (NoticeDao.nextNo 가 만든다)
	private String title, content;
	private String important;          // Y 면 목록 맨 위에 고정 + 중요 표시
	private String attach;             // 첨부파일. 관리자 화면에서는 아직 안 씀
	private int hit;
	private String reg_id, reg_date;

	public NoticeDto() {
	}

	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
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
	public String getImportant() {
		return important;
	}
	public void setImportant(String important) {
		this.important = important;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public int getHit() {
		return hit;
	}
	public void setHit(int hit) {
		this.hit = hit;
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

	// ── 아래 두 생성자는 정규상 공지 게시판(notice/*.jsp)에서 쓰는 것. 2026-09-21 병합 때 합침.
	//    NoticeDao.getNoticeList(select,search,start,end) 가 이 8개짜리를 쓴다.
	public NoticeDto(String no, String title, String content,
	                 String important, String attach, int hit,
	                 String reg_id, String reg_date) {
		this.no = no;
		this.title = title;
		this.content = content;
		this.important = important;
		this.attach = attach;
		this.hit = hit;
		this.reg_id = reg_id;
		this.reg_date = reg_date;
	}

	// 인덱스 화면의 공지 요약용 (번호/제목/날짜만)
	public NoticeDto(String no, String title, String reg_date) {
		this.no = no;
		this.title = title;
		this.reg_date = reg_date;
	}
}
