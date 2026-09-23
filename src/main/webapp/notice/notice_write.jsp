<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>공지사항 작성 | 인천공항 주차예약</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/notice/notice_write.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common.js"></script>

<script>
	function goSave(gubun) {
		noti.t_gubun.value = gubun;

		if (checkEmpty(noti.t_title, "제목 입력!")) return;
		if (checkEmpty(noti.t_content, "내용 입력!")) return;

		// 1.확장자 검사
		/*      var fileName = noti.t_attach.value;
		      if(fileName != ""){ //  C:\fakepath\img_1.png
		         var pathFileName = fileName.lastIndexOf(".")+1;    //확장자 제외한 경로+파일명
		         var extension = (fileName.substr(pathFileName)).toLowerCase();   //확장자명
		         //파일명.확장자
		//             if(extension != "pdf" && extension != "hwp" && extension != "png"){
		         if(extension != "pdf"{
		            alert(extension +" 형식 파일은 업로드 안됩니다. 한글, PDF, PNG 파일만 가능!");
		            return;
		         }      
		      }
		 */
		// 2.첨부 용량 체크   
		var file = noti.t_attach;
		var fileMaxSize = 10; // 첨부 최대 용량 설정
		if (file.value != "") {
			// 사이즈체크
			var maxSize = 1024 * 1024 * fileMaxSize;
			var fileSize = 0;
			// 브라우저 확인
			var browser = navigator.appName;
			// 익스플로러일 경우
			if (browser == "Microsoft Internet Explorer") {
				var oas = new ActiveXObject("Scripting.FileSystemObject");
				fileSize = oas.getFile(file.value).size;
			} else {
				// 익스플로러가 아닐경우
				fileSize = file.files[0].size;
			}

			if (fileSize > maxSize) {
				alert(" 첨부파일 사이즈는 " + fileMaxSize + "MB 이내로 등록 가능합니다. ");
				return;
			}
		}

		noti.method = "post";
		noti.action = "Notice?t_gubun=" + gubun;
		noti.submit();
	}
</script>



</head>

<body>

	<!-- 공통 헤더 -->
	<%@include file="../common_header.jsp"%>
	<!-- 메인 영역 -->
	<main class="noticeWrite">

		<div class="noticeTitle">
			<h1>공지사항 작성</h1>
			<p>새로운 공지사항을 작성할 수 있습니다.</p>
		</div>

		<form name="noti" enctype="multipart/form-data">
			<input type="hidden" name="t_gubun">
			<div class="writeTable">

				<!-- 제목 -->
				<div class="writeRow">
					<label for="noticeTitle">제목</label>
					<input type="text" id="noticeTitle" name="t_title" placeholder="공지사항 제목을 입력하세요." required>
				</div>

				<!-- 작성자 -->
				<div class="writeRow">
					<label for="writer">작성자</label>
					<input type="text" id="writer" name="t_reg_id" value="관리자" readonly>
				</div>

				<!-- 중요공지 -->
				<div class="writeRow">
					<label for="important">중요공지</label>

					<div class="checkArea">
						<input type="checkbox" id="important" name="t_important" value="Y">

						<label for="important" class="checkLabel"> 중요공지로 등록 </label>
					</div>
				</div>

				<!-- 내용 -->
				<div class="writeRow contentRow">
					<label for="noticeContent">내용</label>

					<textarea id="noticeContent" name="t_content" placeholder="공지사항 내용을 입력하세요." required></textarea>
				</div>

				<!-- 첨부파일 -->
				<div class="writeRow">
					<label for="attachFile">첨부파일</label>

					<div class="fileArea">
						<input type="file" id="attachFile" name="t_attach"
							accept=".pdf,.hwp,.hwpx,.png,.jpg,.jpeg">

						<p class="fileInfo">PDF, HWP, HWPX, PNG, JPG, JPEG 파일만 등록
							가능합니다. (최대 10MB)</p>
					</div>
				</div>

			</div>
		</form>

			<!-- 버튼 영역 -->
			<div class="writeBtn">

				<a href="Notice" class="cancelBtn"> 목록 </a>
				<input type="button" value="등록" onclick="goSave('save')" class="submitBtn">
				<!--<button type="button" onclick="goSave('save')" class="submitBtn">등록</button>-->

			</div>


	</main>

	<!-- 공통 푸터 -->
	<%@include file="/common_footer.jsp"%>
	<!-- 공통 헤더 및 푸터 불러오기 -->


</body>
</html>