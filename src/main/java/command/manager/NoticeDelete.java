package command.manager;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.NoticeDao;

// 공지사항 삭제 (관리자 콘솔). Manager?t_gubun=noticeDelete
public class NoticeDelete implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String no = CommonUtil.getCheckNull(request.getParameter("t_no")).trim();

		String msg;
		if (no.equals("")) {
			msg = "잘못된 공지 번호입니다.";
		} else {
			int result = new NoticeDao().noticeDelete(no);
			msg = result == 1 ? "공지사항을 삭제했습니다." : "삭제에 실패했습니다. 이미 지워진 글일 수 있습니다.";
		}
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Manager?t_gubun=notice");
	}

}
