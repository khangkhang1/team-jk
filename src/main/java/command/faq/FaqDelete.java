package command.faq;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import controller.Faq;
import dao.FaqDao;

// FAQ 삭제. Faq?t_gubun=delete  (행을 실제로 지운다. 잠깐 감추기만 할 거면 수정 화면에서 노출여부 "숨김")
public class FaqDelete implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		FaqDao dao = new FaqDao();
		int faq_id = Faq.parseId(request.getParameter("t_faq_id"));

		int result = dao.faqDelete(faq_id);
		String msg = result == 1 ? "삭제되었습니다." : "삭제에 실패했습니다.";
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Faq");
	}

}
