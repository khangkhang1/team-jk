package common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class CommonUtil {
	
	//작은따옴표 변환
	public static String getSingleQuot(String str) {
		str = str.replaceAll("'", "&#39;");
		return str;
	}
	
	// 큰따옴표 변환
	public static String getDoubleQuot(String str){
		str = str.replaceAll("\"", "&quot;");
		return str;
	}
	
	//공지사항 첨부파일 경로
	public static String getNoticeDir(HttpServletRequest request){
		String dir = request.getSession().getServletContext().getRealPath("/")+"attach/notice/";
		return dir;
	}
	//제품 첨부파일 경로
	public static String getProductDir(HttpServletRequest request){
		String dir = request.getSession().getServletContext().getRealPath("/")+"attach/product/";
		return dir;
	}
	
	// null을 공백으로
	public static String getCheckNull(String str) {
		String result ="";
		if(str != null) result = str;
		return result;
	}	
	
	// 오늘날짜  yyyy-MM-dd
	public static String getToday(){
		Date date = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		
		String today = sd.format(date);
		return today;
	}

	// 오늘날짜  yy-MM
	public static String getTodayYYMM(){
		Date date = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("yy-MM");
		
		String today = sd.format(date);
		return today;
	}	
	
	// 오늘날짜 시분초 yyyy-MM-dd HH:mm:ss
	public static String getTodayTime(){
		Date date = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String today = sd.format(date);
		return today;
	}	
	
	// 페이지 설정
	public static String getPageSetting(int current_page,int total_page, int pageNumber_count){
		int pagenumber;    //화면에 보여질 페이지 인덱스수
		int startpage;     //화면에 보여질 시작 페이지 번호
		int endpage;       //화면에 보여질 마지막 페이지 번호
		int curpage;       //이동하고자 하는 페이지 번호
		
		String strList=""; //리턴될 페이지 인덱스 리스트

		pagenumber = pageNumber_count;   //한 화면의 페이지 인덱스수
		
		//시작 페이지 번호 구하기
		startpage = ((current_page - 1)/ pagenumber) * pagenumber + 1;
		//마지막 페이지 번호 구하기
		endpage = (((startpage -1) + pagenumber) / pagenumber)*pagenumber;
		//총페이지수가 계산된 마지막 페이지 번호보다 작을 경우
		//총페이지수가 마지막 페이지 번호가 됨
		
		if(total_page <= endpage)  endpage = total_page;
					
		//첫번째 페이지 인덱스 화면이 아닌경우
		if(current_page > pagenumber){
			curpage = startpage -1;  //시작페이지 번호보다 1적은 페이지로 이동
			strList = strList +"<a href=javascript:goListPage('"+curpage+"') ><i class='fa fa-angle-double-left'></i></a>";
		}
						
		//시작페이지 번호부터 마지막 페이지 번호까지 화면에 표시
		curpage = startpage;
		while(curpage <= endpage){
			if(curpage == current_page){
				strList = strList +"<a class='active'>"+current_page+"</a>";
			} else {
				strList = strList +"<a href=javascript:goListPage('"+curpage+"')>"+curpage+"</a>";
			}
			curpage++;
		}
		//뒤에 페이지가 더 있는 경우
		if(total_page > endpage){
			curpage = endpage+1;
			strList = strList + "<a href=javascript:goListPage('"+curpage+"') ><i class='fa fa-angle-double-right'></i></a>";
		}
		return strList;
	}

	  public static String getMakeDate(String day,String gubun, int val) {
	       LocalDate paraDate = LocalDate.parse(day);
	      // LocalDate today = LocalDate.now();
	       LocalDate date = null;
	       
	       if(gubun.equals("+")) date = paraDate.plusDays(val); // 5일 후
	       else date = paraDate.minusDays(val);
	       
	       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	       String resultDate = date.format(formatter); // 예: "20260607"
	      return resultDate; 
	          
	   }
	}			
	
	





