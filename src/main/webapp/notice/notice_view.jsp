<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>공지사항 상세보기 | 인천공항 주차예약</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notice/notice_view.css">
    
    
    <script>
	function goUpdateForm(gubun){
		noti.t_gubun.value = gubun;
		noti.method = "post";
		noti.action = "Notice";
		noti.submit();
		
	}
    
    </script>
    
    
    
</head>
	<form name ="noti">
		<input type="hidden" name="t_gubun">
		<input type="hidden" name="t_no" value="${dto.getNo()}">
		<input type="hidden" name="t_ori_attach" value="${dto.getAttach()}">
	</form>

<body>

    <!-- 공통 헤더 -->
	<%@include file="/common_header.jsp" %>
    <!-- 메인 영역 -->
    <main class="noticeView">

        <!-- 제목 영역 -->
        <div class="noticeTitle">

            <h1>공지사항</h1>

            <p>
                인천공항 주차예약의 새로운 소식을 확인하세요.
            </p>

        </div>

        <!-- 공지사항 상세 내용 -->
        <div class="viewBox">

            <!-- 게시글 제목 -->
            <div class="viewHeader">

                <div class="titleArea">
                
                	<c:if test="${dto.getImportant() eq 'Y'}">
                    	<span class="importantBadge">중요공지</span>
					</c:if>
                    <h2>
                        ${dto.getTitle()}
                    </h2>

                </div>

                <!-- 게시글 정보 -->
                <div class="viewInfo">

                    <span>
                        작성자 : ${dto.getReg_id()}
                    </span>

                    <span>
                        작성일 : ${dto.getReg_date()}
                    </span>

                    <span>
                        조회수 : ${dto.getHit()}
                    </span>

                </div>

            </div>

            <!-- 첨부파일 -->
            <div class="fileArea">

                <span class="fileLabel">
                    첨부파일
                </span>

                <a href="FileDownServlet?t_fileDir=notice&t_fileName=${dto.getAttach()}" class="fileName">
                    ${dto.getAttach()}
                </a>

            </div>

            <!-- 본문 -->
            <div class="viewContent"> ${dto.getContent()}

<!--
                <p>
                    안녕하세요. 인천공항 주차예약 서비스입니다.
                </p>

                <p>
                    인천공항 제1여객터미널 주차예약 서비스를
                    이용해 주셔서 감사합니다.
                </p>

                <p>
                    주차장 이용 전 실시간 주차 현황과
                    예약 가능 여부를 확인해 주시기 바랍니다.
                </p>

                <p>
                    더욱 편리하고 안전한 주차 서비스를 제공할 수 있도록
                    최선을 다하겠습니다.
                </p>

                <p>
                    감사합니다.
                </p>
-->
            </div>

        </div>

        <!-- 이전글 / 다음글 -->
        <div class="noticeNavigation">

            <div class="navRow">

                <span class="navLabel">
                    이전글
                </span>

                <a href="#" class="navTitle">
                    주차예약 시스템 점검 안내
                </a>

            </div>

            <div class="navRow">

                <span class="navLabel">
                    다음글
                </span>

                <a href="#" class="navTitle">
                    주차장 이용 요금 안내
                </a>

            </div>

        </div>

        <!-- 버튼 영역 -->
        <div class="viewBtn">

            <a href="Notice" class="listBtn">
                목록
            </a>

            <a href="javascript:goUpdateForm('noticeUpdateForm')" class="editBtn">
                수정
            </a>

        </div>

    </main>

    <!-- 공통 푸터 -->
	<%@include file="/common_footer.jsp" %>
    

</body>
</html>