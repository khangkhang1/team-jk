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

public class NoticeSave implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		NoticeDao dao = new NoticeDao();
		//String attachDir=request.getSession().getServletContext().getRealPath("/")+"attach/notice";
		String attachDir = CommonUtil.getNoticeDir(request);
		File dir = new File(attachDir);
		dir.mkdirs();
		
		
		if(!dir.exists()) {
		    dir.mkdirs();
		}
		
		int maxSize=1024*1024 *10;
		MultipartRequest mpr = null;
		try {
		    mpr = new MultipartRequest(
		        request,
		        attachDir,
		        maxSize,
		        "utf-8",
		        new DefaultFileRenamePolicy()
		    );
		} catch (IOException e) {
		    e.printStackTrace();
		    request.setAttribute("t_msg", "파일 업로드에 실패했습니다.");
		    request.setAttribute("t_url", "Notice");
		    return;
		}
		String no = dao.getNoticeNo();
		String title = mpr.getParameter("t_title");
		title = CommonUtil.getSingleQuot(title);
		String content = mpr.getParameter("t_content");
		content = CommonUtil.getSingleQuot(content);
		String attach = mpr.getFilesystemName("t_attach");
		if(attach == null)attach="";
		attach = CommonUtil.getSingleQuot(attach);
		String important = mpr.getParameter("t_important");
		if(important == null) important = "N";
		String reg_id = (String)request.getSession().getAttribute("sessionId");
		String reg_date = CommonUtil.getTodayTime();
		
		NoticeDto dto = new NoticeDto(no, title, content, important, attach, 0, reg_id, reg_date);
		int result = dao.noticeSave(dto);
		String msg=result==1?"등록되었습니다.":"등록실패!";
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "Notice");
		
		

	}

}
