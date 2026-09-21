package command.notice;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import common.CommonExecute;
import common.CommonUtil;
import dao.NoticeDao;
import dto.NoticeDto;

/**
 * 공지사항 등록 (이용자 게시판). Notice?t_gubun=save
 *
 * [왜 이 파일이 여기 있나] 2026-09-21 병합
 *   controller/Notice.java 가 command.notice.NoticeSave 를 import 하고 있는데
 *   정작 클래스가 어느 브랜치에도 없어서 프로젝트 전체가 컴파일되지 않았습니다.
 *   병합을 끝내려면 채워야 해서, 관리자 콘솔의 command.manager.NoticeSave 를
 *   본떠 최소한으로 만들었습니다. 화면 흐름(목록으로 복귀)만 이용자 쪽에 맞췄습니다.
 *   정규상이 원래 의도한 동작이 다르면 이 파일만 고치면 됩니다.
 *
 * [cos.jar 를 쓰는 이유]
 *   notice_write.jsp 의 폼이 enctype="multipart/form-data" 라서
 *   request.getParameter() 로는 값이 안 읽힙니다. 파일이 섞인 요청은
 *   MultipartRequest 로 감싸야 일반 입력값도 같이 꺼낼 수 있습니다.
 *   (개인 프로젝트에서 쓰던 방식과 동일 - cos.jar + DefaultFileRenamePolicy)
 */
public class NoticeSave implements CommonExecute {

	// 첨부 최대 10MB. 개인 프로젝트에서 쓰던 값과 맞춤.
	private static final int MAX_SIZE = 10 * 1024 * 1024;

	@Override
	public void execute(HttpServletRequest request) {

		String saveDir = CommonUtil.getNoticeDir(request);
		File dir = new File(saveDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			// 같은 이름의 파일이 올라오면 DefaultFileRenamePolicy 가 뒤에 숫자를 붙여준다.
			MultipartRequest multi = new MultipartRequest(
					request, saveDir, MAX_SIZE, "UTF-8", new DefaultFileRenamePolicy());

			String title     = CommonUtil.getCheckNull(multi.getParameter("t_title")).trim();
			String content   = CommonUtil.getCheckNull(multi.getParameter("t_content")).trim();
			String important = CommonUtil.getCheckNull(multi.getParameter("t_important"));
			String attach    = multi.getFilesystemName("t_attach");   // 첨부 없으면 null

			String reg_id = (String) request.getSession().getAttribute("sessionId");
			if (reg_id == null) {
				reg_id = CommonUtil.getCheckNull(multi.getParameter("t_reg_id"));
			}

			// 화면 JS 를 우회해도 빈 글이 들어가지 않게 서버에서 한 번 더 확인한다.
			if (title.equals("") || content.equals("")) {
				request.setAttribute("t_msg", "제목과 내용을 모두 입력하세요.");
				request.setAttribute("t_url", "Notice?t_gubun=noticeWriteForm");
				return;
			}

			NoticeDto dto = new NoticeDto();
			dto.setTitle(title);
			dto.setContent(content);
			dto.setImportant("Y".equals(important) ? "Y" : "N");
			dto.setAttach(attach);
			dto.setReg_id(reg_id);

			// 번호(N001 형식)는 NoticeDao.noticeSave 가 안에서 만들어 넣는다.
			int result = new NoticeDao().noticeSave(dto);

			request.setAttribute("t_msg", result == 1 ? "공지사항을 등록했습니다." : "등록에 실패했습니다.");
			request.setAttribute("t_url", "Notice");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("t_msg", "등록 중 오류가 발생했습니다.");
			request.setAttribute("t_url", "Notice");
		}
	}
}
