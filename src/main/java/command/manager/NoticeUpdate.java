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

		NoticeDao dao = new NoticeDao();
		NoticeDto old = dao.noticeView(no);
		if (old == null) {
			request.setAttribute("t_msg", "없는 공지 번호입니다. 이미 삭제된 글일 수 있습니다.");
			request.setAttribute("t_url", "Manager?t_gubun=notice");
			return;
		}

		// NoticeDao 는 값을 SQL 문자열에 바로 붙이므로 따옴표는 HTML 엔티티로 바꿔 저장한다 (FAQ 와 같은 방식)
		// 첨부파일은 이 화면에서 다루지 않으므로 정규상 공지 화면에서 올린 값을 그대로 둔다
		NoticeDto dto = new NoticeDto();
		dto.setNo(no);
		dto.setTitle(CommonUtil.getDoubleQuot(CommonUtil.getSingleQuot(title)));
		dto.setContent(CommonUtil.getDoubleQuot(CommonUtil.getSingleQuot(content)));
		dto.setImportant(important.equals("Y") ? "Y" : "N");
		dto.setAttach(old.getAttach() == null ? "" : old.getAttach());

		int result = dao.noticeUpdate(dto);
		request.setAttribute("t_msg", result == 1 ? "공지사항을 수정했습니다." : "수정에 실패했습니다.");
		request.setAttribute("t_url", "Manager?t_gubun=notice");
	}

}
