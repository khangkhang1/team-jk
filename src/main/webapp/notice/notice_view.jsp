<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${empty sessionScope.lang ? 'ko' : sessionScope.lang}">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title><fmt:message key="ntcv.001"/></title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notice/notice_view.css">
    
    
    <script>
	function goUpdateForm(gubun){
		noti.t_gubun.value = gubun;
		noti.method = "post";
		noti.action = "Notice";
		noti.submit();
		
	}
	
	function goDelete(){
		if(confirm("삭제 하시겠습니까?")){
			noti.t_gubun.value="noticeDelete";
			noti.method="post";
			noti.action="Notice";
			noti.submit();
	   }
	}
	
	function goView(no){
	      noti.t_gubun.value="noticeView";
	      noti.t_no.value=no;
	      noti.method="post";
	      noti.action="Notice";
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

            <h1><fmt:message key="idx.057"/></h1>

            <p>
                <fmt:message key="ntcv.002"/>
            </p>

        </div>

        <!-- 공지사항 상세 내용 -->
        <div class="viewBox">

            <!-- 게시글 제목 -->
            <div class="viewHeader">

                <div class="titleArea">
                
                	<c:if test="${dto.getImportant() eq 'Y'}">
                    	<span class="importantBadge"><fmt:message key="ntcv.003"/></span>
					</c:if>
                    <h2>
                        ${dto.getTitle()}
                    </h2>

                </div>

                <!-- 게시글 정보 -->
                <div class="viewInfo">

                    <span>
                        <fmt:message key="ntcv.004"><fmt:param value="${dto.getReg_id()}"/></fmt:message>
                    </span>

                    <span>
                        <fmt:message key="ntcv.005"><fmt:param value="${dto.getReg_date()}"/></fmt:message>
                    </span>

                    <span>
                        <fmt:message key="ntcv.006"><fmt:param value="${dto.getHit()}"/></fmt:message>
                    </span>

                </div>

            </div>

            <!-- 첨부파일 -->
            <div class="fileArea">

                <span class="fileLabel">
                    <fmt:message key="ntcv.007"/>
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
			<c:if test="${not empty preDto.getNo()}">
                <span class="navLabel">
                    <fmt:message key="ntcv.008"/>
                </span>

                <a href="javascript:goView('${preDto.getNo()}')" class="navTitle">
                    <c:choose>
                        <c:when test="${fn:length(preDto.getTitle()) > 10}">
                           ${fn:substring(preDto.getTitle(),0,10)}...
                        </c:when>
                        <c:otherwise>
                           ${preDto.getTitle()}
                        </c:otherwise>
                     </c:choose>
                </a>
			</c:if>
            </div>

            <div class="navRow">
			<c:if test="${not empty nextDto.getNo()}">
                <span class="navLabel">
                    <fmt:message key="ntcv.009"/>
                </span>

                <a href="javascript:goView('${nextDto.getNo()}')" class="navTitle">
                    <c:choose>
                        <c:when test="${fn:length(nextDto.getTitle()) > 10}">
                           ${fn:substring(nextDto.getTitle(),0,10)}...
                        </c:when>
                        <c:otherwise>
                           ${nextDto.getTitle()}
                        </c:otherwise>
                     </c:choose>
                </a>
			</c:if>
            </div>

        </div>

        <!-- 버튼 영역 -->
        <div class="viewBtn">

            <a href="Notice" class="listBtn">
                <fmt:message key="ntcv.010"/>
            </a>
			<c:if  test="${sessionLevel eq 'top'}">
	            <a href="javascript:goUpdateForm('noticeUpdateForm')" class="editBtn">
	                <fmt:message key="ntcv.011"/>
	            </a>
	            <a href="javascript:goDelete('noticeDelete')" class="deleteBtn">
	                <fmt:message key="ntcv.012"/>
	            </a>
			</c:if>
        </div>

    </main>

    <!-- 공통 푸터 -->
	<%@include file="/common_footer.jsp" %>
    

</body>
</html>