package command.manager;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.NoticeDao;
import dto.NoticeDto;

// 공지사항 등록 (관리자 콘솔). Manager?t_gubun=noticeSave
public class NoticeSave implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String title     = CommonUtil.getCheckNull(request.getParameter("t_title")).trim();
		String content   = CommonUtil.getCheckNull(request.getParameter("t_content")).trim();
		String important = CommonUtil.getCheckNull(request.getParameter("t_important"));
		String reg_id    = (String) request.getSession().getAttribute("sessionId");
		if (reg_id == null) reg_id = "manager";

		// 화면 JS 를 우회해도 빈 글이 들어가지 않게 서버에서 한 번 더 본다
		if (title.equals("") || content.equals("")) {
			request.setAttribute("t_msg", "제목과 내용을 모두 입력하세요.");
			request.setAttribute("t_url", "Manager?t_gubun=noticeForm");
			return;
		}

		NoticeDto dto = new NoticeDto();
		dto.setTitle(title);
		dto.setContent(content);
		dto.setImportant(important.equals("Y") ? "Y" : "N");
		dto.setReg_id(reg_id);

		int result = new NoticeDao().noticeSave(dto);
		request.setAttribute("t_msg", result == 1 ? "공지사항을 등록했습니다." : "등록에 실패했습니다.");
		request.setAttribute("t_url", "Manager?t_gubun=notice");
	}

}
