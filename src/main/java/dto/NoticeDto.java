package dto;

public class NoticeDto {

    private String no, title, content, important, attach, reg_id, reg_date;
    private int hit;


    // 기본 생성자
    public NoticeDto() {
    }


    // 전체 데이터 생성자
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

    
    
    
    
    
    
    
//인덱스 공지
    public NoticeDto(String no, String title, String reg_date) {
		this.no = no;
		this.title = title;
		this.reg_date = reg_date;
	}

//이전글 다음글
	public NoticeDto(String no, String title) {
		this.no = no;
		this.title = title;
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
}