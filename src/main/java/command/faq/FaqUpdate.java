package command.faq;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.CommonUtil;
import controller.Faq;
import dao.FaqDao;
import dto.FaqDto;

// FAQ 수정. Faq?t_gubun=update  (등록자/등록일은 건드리지 않는다)
public class FaqUpdate implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		FaqDao dao = new FaqDao();

		int    faq_id   = Faq.parseId(request.getParameter("t_faq_id"));
		String category = CommonUtil.getCheckNull(request.getParameter("t_category"));
		String question = CommonUtil.getCheckNull(request.getParameter("t_question")).trim();
		String answer   = CommonUtil.getCheckNull(request.getParameter("t_answer")).trim();
		String sortNo   = CommonUtil.getCheckNull(request.getParameter("t_sort_no"));
		String useYn    = CommonUtil.getCheckNull(request.getParameter("t_use_yn"));

		if (question.equals("") || answer.equals("")) {
			request.setAttribute("t_msg", "질문과 답변을 모두 입력하세요.");
			request.setAttribute("t_url", "Faq?t_gubun=updateForm&t_faq_id=" + faq_id);
			return;
		}

		question = CommonUtil.getDoubleQuot(CommonUtil.getSingleQuot(question));
		answer   = CommonUtil.getDoubleQuot(CommonUtil.getSingleQuot(answer));

		int sort_no = 100;
		if (sortNo.matches("[0-9]+")) sort_no = Integer.parseInt(sortNo);
		if (useYn.equals("")) useYn = "Y";

		FaqDto dto = new FaqDto();
		dto.setFaq_id(faq_id);
		dto.setCategory(category);
		dto.setQuestion(question);
		dto.setAnswer(answer);
		dto.setSort_no(sort_no);
		dto.setUse_yn(useYn);

		int result = dao.faqUpdate(dto);
		String msg = result == 1 ? "수정되었습니다." : "수정에 실패했습니다.";
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Faq");
	}

}
