package command.member;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MemberDao;

/**
 * Servlet implementation class MemberCheckPassword
 */
@WebServlet("/MemberCheckPassword")
public class MemberCheckPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MemberCheckPassword() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberDao dao =MemberDao.getdao();
		String id=request.getParameter("t_id");
		String password=request.getParameter("t_password");
		try {
			password=dao.encryptSHA256(password);
		}catch(Exception e) {
			e.printStackTrace();
		}
		int count=dao.getCheckPassword(id,password);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		if(count==1)out.print("success");	else out.print("fail");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
