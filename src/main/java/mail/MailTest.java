package mail;


import common.SecretConfig;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MailTest
 */
@WebServlet("/MailTest")
public class MailTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MailTest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 String fromUserEmail = SecretConfig.get("mail.from"); // 보내는 사람 주소 - secret.properties
	     String fromUserPassword = SecretConfig.get("mail.appPassword"); // 구글 앱 비밀번호 - secret.properties
		
		
	    String toUserEmail = "ct09md@naver.com";
        String mailTitle = "1234";
        String mailContent = "test: 새로운 비밀번호는 1234 입니다.";
        
        SendMail sm = new SendMail(fromUserEmail, fromUserPassword);
		boolean tf = sm.sendPassword(toUserEmail, mailTitle, mailContent);
		System.out.println(" mail 성공여부 :"+tf);
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
