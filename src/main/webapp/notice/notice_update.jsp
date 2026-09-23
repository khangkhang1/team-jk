<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="ko">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>공지사항 수정 | 인천공항 주차예약</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notice/notice_update.css">
    
    <script type="text/javascript">
   function goUpdate(){
      if(checkEmpty(noti.t_title,"제목 입력!")) return;
      if(checkEmpty(noti.t_content,"내용 입력!")) return;
      
      // 1.확장자 검사
      /*      var fileName = noti.t_attach.value;
            if(fileName != ""){ //  C:\fakepath\img_1.png
               var pathFileName = fileName.lastIndexOf(".")+1;    //확장자 제외한 경로+파일명
               var extension = (fileName.substr(pathFileName)).toLowerCase();   //확장자명
               //파일명.확장자
//               if(extension != "pdf" && extension != "hwp" && extension != "png"){
               if(extension != "pdf"{
                  alert(extension +" 형식 파일은 업로드 안됩니다. 한글, PDF, PNG 파일만 가능!");
                  return;
               }      
            }
      */         
            // 2.첨부 용량 체크   
            var file = noti.t_attach;
            var fileMaxSize  = 10; // 첨부 최대 용량 설정
            if(file.value !=""){
               // 사이즈체크
               var maxSize  = 1024 * 1024 * fileMaxSize;
               var fileSize = 0;
               // 브라우저 확인
               var browser=navigator.appName;
               // 익스플로러일 경우
               if (browser=="Microsoft Internet Explorer"){
                  var oas = new ActiveXObject("Scripting.FileSystemObject");
                  fileSize = oas.getFile(file.value).size;
               }else {
               // 익스플로러가 아닐경우
                  fileSize = file.files[0].size;
               }

               if(fileSize > maxSize){
                  alert(" 첨부파일 사이즈는 "+fileMaxSize+"MB 이내로 등록 가능합니다. ");
                  return;
               }   
            }      
      
      noti.method = "post";
      noti.action = "Notice?t_gubun=noticeUpdate";
//      noti.action="NoticeSaveServlet";
      noti.submit();
   }   
</script>
    
    
</head>

<body>

    <!-- 공통 헤더 -->
	<%@include file="/common_header.jsp" %>
    <!-- 메인 영역 -->
    <main class="noticeUpdate">

        <!-- 제목 영역 -->
        <div class="noticeTitle">

            <h1>공지사항 수정</h1>

            <p>
                기존 공지사항의 내용을 수정할 수 있습니다.
            </p>

        </div>

        <!-- 수정 폼 -->
        <form name="noti" enctype="multipart/form-data">
		<input type="hidden" name="t_gubun">
		<input type="hidden" name="t_no" value="${dto.getNo()}">
		<input type="hidden" name="t_ori_attach" value="${dto.getAttach()}">
		
		
            <div class="writeTable">

                <!-- 제목 -->
                <div class="writeRow">

                    <label for="noticeTitle">제목</label>
                    <input type="text" id="noticeTitle" name="t_title"
						value="${dto.getTitle()}" placeholder="공지사항 제목을 입력하세요." required>

				</div>

                <!-- 작성자 -->
                <div class="writeRow">

					<label for="writer">작성자</label>
					<input type="text" id="writer"
						name="t_reg_id" value="${dto.getReg_id()}" readonly>

				</div>

                <!-- 중요공지 -->
                <div class="writeRow">

                    <label for="important">중요공지</label>

                    <div class="checkArea">

						<input type="hidden" id="important" name="t_important" value="N">
						<input type="checkbox" id="important" name="t_important" value="Y" <c:if test="${dto.getImportant() eq 'Y'}">checked</c:if>>
							
						<label for="important" class="checkLabel">
                            중요공지로 등록
                        </label>

                    </div>

                </div>

                <!-- 내용 -->
                <div class="writeRow contentRow">

                    <label for="noticeContent">내용</label>

					<textarea id="noticeContent" name="t_content"
						placeholder="공지사항 내용을 입력하세요." required>${dto.getContent()}</textarea>

				</div>

                <!-- 기존 첨부파일 -->
                <div class="writeRow">

                    <label>기존 첨부파일</label>

                    <div class="fileArea">

                        <div class="oldFile">
                            <span>${dto.getAttach()}</span>

<!--                        <button type="button" name="t_delete_checkbox" class="deleteFileBtn">
                                삭제
                            </button>
-->                        
                            </div>

                    </div>

                </div>

                <!-- 새 첨부파일 -->
                <div class="writeRow">

                    <label for="attachFile">첨부파일 변경</label>

                    <div class="fileArea">

						<input type="file" id="attachFile" name="t_attach"
							accept=".pdf,.hwp,.hwpx,.png,.jpg,.jpeg">

						<p class="fileInfo">
                            새 파일을 선택하면 기존 첨부파일을 변경할 수 있습니다.
                            (최대 10MB)
                        </p>

                    </div>

                </div>

            </div>
        </form>

            <!-- 버튼 영역 -->
            <div class="writeBtn">

                <a href="#" class="cancelBtn" onclick="history.back();">
                    취소
                </a>

                <button type="button" onclick="goUpdate()" class="submitBtn">
                    수정 완료
                </button>

            </div>


    </main>

    <!-- 공통 푸터 -->
    <%@include file="/common_footer.jsp" %>

    

</body>
</html>