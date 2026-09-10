package command.member;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.MemberDao;
import dto.MemberDto;
import mail.SendMail;

public class MemberSendPassword implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		// 앱 비밀번호  cpsc rpah tosd fmmh
		
		MemberDao dao = MemberDao.getdao();
		String id = request.getParameter("t_id");
		String mobile_1 = request.getParameter("t_mobile_1");
		String mobile_2 = request.getParameter("t_mobile_2");
		String mobile_3 = request.getParameter("t_mobile_3");
		
		MemberDto dto = dao.getMemberEmail(id,mobile_1,mobile_2,mobile_3);
		String msg="", url="Member", gubun="";
		if(dto == null) {
			msg ="ID나 연락처 정보가 정확하지 않습니다.";
			gubun ="findpassword";
		} else {
			String fromUserEmail = "ct09md@gmail.com"; // 보내는 사람 주소
		     String fromUserPassword = "gubi wpfw zhty evjr"; // 구글 계정 앱 비밀번호
			
		    int newPasswordLength=4;
			String newPassword = dao.getNewPassword(newPasswordLength);
		    String toUserEmail = dto.getEmail_1()+"@"+dto.getEmail_2();
	        String mailTitle = dto.getName()+"님 임시 비밀번호를 발송합니다.";
	        String mailContent = "test: 새로운 비밀번호는 "+newPassword+" 입니다.";
	        
	        SendMail sm = new SendMail(fromUserEmail, fromUserPassword);
			boolean tf = sm.sendPassword(toUserEmail, mailTitle, mailContent);
			
			if(tf) {
				try {
					newPassword=dao.encryptSHA256(newPassword);
				}catch(Exception e) {
					e.printStackTrace();
				}
				int result=dao.memberPasswordUpdate(id, newPassword,Integer.toString(newPasswordLength));
				if(result==1) {
					msg=dto.getName()+"님 임시 비밀번호를 발송했습니다.";
					
				}else {
					msg="임시 비밀번호 변경 실패! 관리자에게 문의 바랍니다.";
					System.out.println("임시 비밀번호 메일 발송 후 업데이트 오류");
				}
			}
			gubun ="login";
		}
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", url);
		request.setAttribute("t_gubun", gubun);
		
		
	}

}



