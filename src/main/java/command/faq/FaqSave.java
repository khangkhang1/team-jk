package command.faq;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import dao.FaqDao;
import dto.FaqDto;

// FAQ 등록. Faq?t_gubun=save
public class FaqSave implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		FaqDao dao = new FaqDao();

		// getCheckNull : null 이면 "" 로 (뒤의 replaceAll 이 null 에서 터지지 않게)
		String category = CommonUtil.getCheckNull(request.getParameter("t_category"));
		String question = CommonUtil.getCheckNull(request.getParameter("t_question")).trim();
		String answer   = CommonUtil.getCheckNull(request.getParameter("t_answer")).trim();
		String sortNo   = CommonUtil.getCheckNull(request.getParameter("t_sort_no"));
		String useYn    = CommonUtil.getCheckNull(request.getParameter("t_use_yn"));
		String reg_id   = (String) request.getSession().getAttribute("sessionId");

		// JS 검증을 우회해도 빈 글은 못 들어가게 서버에서 한 번 더
		if (question.equals("") || answer.equals("")) {
			request.setAttribute("t_msg", "질문과 답변을 모두 입력하세요.");
			request.setAttribute("t_url", "Faq?t_gubun=writeForm");
			return;
		}

		// 작은따옴표(')는 SQL 문자열을, 큰따옴표(")는 수정화면의 value="" 를 끊는다 -> HTML 엔티티로 저장
		question = CommonUtil.getDoubleQuot(CommonUtil.getSingleQuot(question));
		answer   = CommonUtil.getDoubleQuot(CommonUtil.getSingleQuot(answer));

		int sort_no = 100;                                   // 비웠거나 숫자가 아니면 DB 기본값과 같은 100
		if (sortNo.matches("[0-9]+")) sort_no = Integer.parseInt(sortNo);
		if (useYn.equals("")) useYn = "Y";
		if (reg_id == null) reg_id = "manager";

		FaqDto dto = new FaqDto();
		dto.setCategory(category);
		dto.setQuestion(question);
		dto.setAnswer(answer);
		dto.setSort_no(sort_no);
		dto.setUse_yn(useYn);
		dto.setReg_id(reg_id);

		int result = dao.faqSave(dto);
		String msg = result == 1 ? "등록되었습니다." : "등록에 실패했습니다.";
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Faq");
	}

}
