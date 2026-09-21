<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>



<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>공지사항 | 인천공항 주차예약</title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notice/notice_list.css">


<script>
	function goView(gubun){
		noti.t_gubun.value = gubun;
		noti.method="post";
		noti.action="Notice";
		noti.submit();
	}
	function goWrite(gubun){
		noti.t_gubun.value = gubun;
		noti.method="post";
		noti.action="Notice";
		noti.submit();
	}
	function goSearch(){
		search.method="post";
		search.action="Notice";
		search.submit();
		
	}
	
	

</script>





</head>

<body>

	<form name="view">
		<input type="hidden" name="t_gubun">
		<input type="hidden" name="t_no">
	</form>	

	<form name="noti">
		<input type="hidden" name="t_gubun">
	</form>
	
	
    <!-- 기존 팀프로젝트 공통 헤더 삽입 위치 -->
    <%@include file="/common_header.jsp" %>
    <!-- 공통 헤더는 별도 파일에서 불러올 예정 -->



    <main class="noticeMain">

        <section class="noticeSection">

            <div class="noticeTitle">
            	<i class="fa-solid fa-bullhorn"></i>
                <h1>공지사항</h1>
                <p>인천공항 주차예약 서비스의 새로운 소식을 알려드립니다.</p>
            </div>

            <div class="noticeContent">
            
			<form name="search">
			<input type="hidden" name="t_nowPage">


                <!-- 검색 영역 및 글쓰기 버튼 -->
                <div class="noticeTop">

                    <div class="noticeCount">
                        전체 게시글 <strong>:${totalCount}</strong>개
                    </div>

                    <div class="noticeActions">

                        <div class="noticeSearch">

                            <select name="t_select" id="searchType">
                                <option value="title" <c:if test="${select eq 'title'}"> selected </c:if> >제목</option>
                                <option value="content"<c:if test="${select eq 'content'}"> selected</c:if> >내용</option>
                            </select>

                            <input type="text" name="t_search" value="${search}" id="searchKeyword" placeholder="검색어를 입력해주세요.">

                            <button type="button" onclick="goSearch()" id="searchBtn">
                                검색
                            </button>
                        </div>
                    </div>
                </div>
				</form>
                <!-- 공지사항 목록 -->
                <table class="noticeTable">

                    <thead>
                        <tr>
                            <th class="noticeNum">번호</th>
                            <th class="noticeSubject">제목</th>
                            <th class="noticeDate">작성일</th>
                            <th class="noticeViews">조회수</th>
                        </tr>
                    </thead>

                    <tbody>
						<c:set var="seq" value="${order}"> </c:set>
						<c:forEach items="${t_dtos}" var="dto">
                        <!-- 공지사항 상단 고정 예시 -->
                        
                        <c:choose>
                        
                        <c:when test="${dto.getImportant() eq 'Y'}">
                        
                        <tr class="noticeFixed">

                            <td>
                               <span class="noticeBadge">공지</span>
                            </td>
                        </c:when>
                        
                        
                        <c:otherwise>
                        <tr>

                            <td>${seq}</td>
							<c:set var="seq" value="${seq - 1}"></c:set>
							
                        </c:otherwise>
                            
                            
                        </c:choose>

                            <td class="noticeSubjectText">
                                <a href="javascript:goView('noticeView')">
                                    ${dto.getTitle()}
                                </a>
                            </td>

                            <td>${dto.getReg_date()}</td>

                            <td>${dto.getHit()}</td>

                        </tr>
                        </c:forEach>
						<!-- 
                        <tr class="noticeFixed">

                            <td>
                                <span class="noticeBadge">공지</span>
                            </td>

                            <td class="noticeSubjectText">
                                <a href="notice_detail.html">
                                    주차예약 시스템 이용 시 유의사항 안내
                                </a>
                            </td>

                            <td>2026.09.13</td>

                            <td>12</td>

                        </tr>

                        <!-- 일반 게시글
                        <tr>

                            <td>3</td>

                            <td class="noticeSubjectText">
                                <a href="notice_detail.html">
                                    주차예약 시스템 점검 안내
                                </a>
                            </td>

                            <td>2026.09.12</td>

                            <td>24</td>

                        </tr>

                        <tr>

                            <td>2</td>

                            <td class="noticeSubjectText">
                                <a href="notice_detail.html">
                                    주차예약 서비스 오픈 안내
                                </a>
                            </td>

                            <td>2026.09.11</td>

                            <td>35</td>

                        </tr>

                        <tr>

                            <td>1</td>

                            <td class="noticeSubjectText">
                                <a href="notice_detail.html">
                                    인천공항 주차장 이용 안내
                                </a>
                            </td>

                            <td>2026.09.10</td>

                            <td>41</td>

                        </tr>
							  -->
                    </tbody>

                </table>
                
                <!-- 하단 버튼 영역 -->
			<div class="noticeBottom">
			
			    <!-- 페이지네이션 -->
			    <div class="noticePagination">
			
			
					<!-- 
			        <a href="#" class="active">1</a>
			        <a href="#">2</a>
			        <a href="#">3</a>
			        <a href="#">4</a>
			        <a href="#">5</a>
			        <a href="#" class="next">다음</a>
					 -->
					 ${pageDisplay}
			    </div>
			
			    <!-- 글쓰기 버튼 -->
			    <a href="javascript:goWrite('noticeWriteForm')" class="writeBtn">
			        글쓰기
			    </a>
			
			</div>

        </section>

    </main>

    <%@include file="/common_footer.jsp" %>

</body>
</html>