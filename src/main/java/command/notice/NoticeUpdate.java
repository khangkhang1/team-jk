package command.notice;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import common.CommonExecute;
import common.CommonUtil;
import dao.NoticeDao;
import dto.NoticeDto;

public class NoticeUpdate implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {

	      NoticeDao dao = new NoticeDao();
	      String attachDir = CommonUtil.getNoticeDir(request);
	      int maxSize = 1024 * 1024 * 10;
	      MultipartRequest mpr =null;
	      
	      try{ mpr = new MultipartRequest(request, attachDir, maxSize, "utf-8", new DefaultFileRenamePolicy());
	      
	      }catch(IOException e) {
	         e.printStackTrace();
	      }
	      String no = mpr.getParameter("t_no");
	   //   String title = mpr.getParameter("t_title");
	      String title = CommonUtil.getSingleQuot( mpr.getParameter("t_title"));
	      
	   //   String content = mpr.getParameter("t_content");
	      String content = CommonUtil.getSingleQuot(mpr.getParameter("t_content"));
	      String important = mpr.getParameter("t_important");
	      String attach = mpr.getFilesystemName("t_attach");	
	      if(attach==null) attach = "";
	      String deleteAttach = mpr.getParameter("t_delete_checkbox");
	      String ori_attach = mpr.getParameter("t_ori_attach");
	      if(ori_attach==null) ori_attach = "";
	    //  String update_id=(String)request.getSession().getAttribute("sessionId");
	     // String update_date=CommonUtil.getTodayTime();
	      
	      
	      
	      String dbAttachName="";
	      
	      //삭제 checkbox
	      if(deleteAttach != null) {
	         File file = new File(attachDir,deleteAttach);
	         boolean tf = file.delete();
	         if(!tf) System.out.println("공지사항 첫번째 첨부파일 삭제 오류");
	      }else {
	         dbAttachName=ori_attach;
	      }
	      
	      //새로운 첨부하면
	      if(!attach.equals("")) {
	         dbAttachName=attach;
	         if(!ori_attach.equals("")) {//삭제할 첨부가 있으면
	            File file = new File(attachDir,ori_attach);
	            boolean tf = file.delete();
	            if(!tf) System.out.println("공지사항 두번째 첨부파일 삭제 오류");
	         }
	      }
	      
	      NoticeDto dto = new NoticeDto(no, title, content, important, dbAttachName, 0, "", "");
	      
	      
//	      NoticeDto dto = new NoticeDto(no, title, content, important, dbAttachName,
//	            "hit", "reg_id", "reg_name", "reg_date",
//	            update_id, "update_name", update_date);
	      
	      
	      int result = dao.noticeUpdate(dto);
	      String msg = result == 1 ? "수정 되었습니다.":"수정 실패!";
	      request.setAttribute("t_msg", msg);
	      request.setAttribute("t_url", "Notice");
	      request.setAttribute("t_gubun", "noticeView");
	      request.setAttribute("t_no", no);
	      
	      
	     // System.out.println("attach: "+attach);
	      //System.out.println("deleteAttach:  "+deleteAttach);

	}

}
