package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.notice.NoticeDelete;
import command.notice.NoticeList;
import command.notice.NoticeSave;
import command.notice.NoticeView;
import common.CommonExecute;
import common.CommonUtil;

/**
 * Servlet implementation class Notice
 */
@WebServlet("/Notice")
public class Notice extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Notice() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String gubun = request.getParameter("t_gubun");
		//System.out.println("Notice t_gubun = [" + gubun + "]");
		String viewPage ="";
		
		if(gubun == null) gubun="notice";
		//목록
		if(gubun.equals("notice")) {
			CommonExecute noti = new NoticeList();
			noti.execute(request);
			viewPage = "notice/notice_list.jsp";
		//공지사항 등록 폼
		} else if(gubun.equals("noticeWriteForm")) {
			request.setAttribute("toDay", CommonUtil.getToday());
			viewPage = "notice/notice_write.jsp";
			
		//공지 등록 저장
		} else if(gubun.equals("save")) {
			CommonExecute noti = new NoticeSave();
			noti.execute(request);
			viewPage ="common_alert.jsp";
		
		} else if(gubun.equals("noticeView")) {
			CommonExecute noti = new NoticeView();
			noti.execute(request);
			viewPage = "notice/notice_view.jsp";
		} else if(gubun.equals("noticeUpdateForm")) {
			
			viewPage = "notice/notice_update.jsp";
		//삭제
		} else if(gubun.equals("noticeDelete")) {
			CommonExecute noti = new NoticeDelete();
			noti.execute(request);
			viewPage = "common_alert.jsp";
		}
		
		
		RequestDispatcher rd = request.getRequestDispatcher(viewPage);
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
