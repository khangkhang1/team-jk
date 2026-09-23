package command.manager;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.NoticeDao;
import dto.NoticeDto;

// 공지사항 수정 (관리자 콘솔). Manager?t_gubun=noticeUpdate
public class NoticeUpdate implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String no        = CommonUtil.getCheckNull(request.getParameter("t_no")).trim();
		String title     = CommonUtil.getCheckNull(request.getParameter("t_title")).trim();
		String content   = CommonUtil.getCheckNull(request.getParameter("t_content")).trim();
		String important = CommonUtil.getCheckNull(request.getParameter("t_important"));

		if (no.equals("")) {
			request.setAttribute("t_msg", "잘못된 공지 번호입니다.");
			request.setAttribute("t_url", "Manager?t_gubun=notice");
			return;
		}
		if (title.equals("") || content.equals("")) {
			request.setAttribute("t_msg", "제목과 내용을 모두 입력하세요.");
			request.setAttribute("t_url", "Manager?t_gubun=noticeForm&t_no=" + no);
			return;
		}

		NoticeDto dto = new NoticeDto();
		dto.setNo(no);
		dto.setTitle(title);
		dto.setContent(content);
		dto.setImportant(important.equals("Y") ? "Y" : "N");

		int result = new NoticeDao().noticeUpdate(dto);
		request.setAttribute("t_msg", result == 1 ? "공지사항을 수정했습니다." : "수정에 실패했습니다.");
		request.setAttribute("t_url", "Manager?t_gubun=notice");
	}

}
