<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>공지사항 작성 | 인천공항 주차예약</title>


    <!-- 공지사항 CSS -->
    <link rel="stylesheet" href="../css/notice/notice_write.css">
    <link rel="stylesheet" href="../css/notice/notice_write.css">
</head>
<body>

    <!-- 헤더 영역 -->
    <%@include file="../common_header.jsp" %>

    <!-- 메인 영역 -->
    <main class="noticeWrite">

        <div class="noticeTitle">
            <h1>공지사항 작성</h1>
            <p>새로운 공지사항을 작성할 수 있습니다.</p>
        </div>

        <form action="#" method="post">

            <div class="writeTable">

                <!-- 제목 -->
                <div class="writeRow">
                    <label for="noticeTitle">제목</label>
                    <input
                        type="text"
                        id="noticeTitle"
                        name="noticeTitle"
                        placeholder="공지사항 제목을 입력하세요."
                        required>
                </div>

                <!-- 작성자 -->
                <div class="writeRow">
                    <label for="writer">작성자</label>
                    <input
                        type="text"
                        id="writer"
                        name="writer"
                        value="관리자"
                        readonly>
                </div>

                <!-- 내용 -->
                <div class="writeRow contentRow">
                    <label for="noticeContent">내용</label>
                    <textarea
                        id="noticeContent"
                        name="noticeContent"
                        placeholder="공지사항 내용을 입력하세요."
                        required></textarea>
                </div>

            </div>

            <!-- 버튼 영역 -->
            <div class="writeBtn">

                <a href="notice_list.html" class="cancelBtn">
                    취소
                </a>

                <button type="submit" class="submitBtn">
                    등록
                </button>

            </div>

        </form>

    </main>

    <!-- 푸터 영역 -->
    <footer>
        <div class="footerWrap">
            <div class="copyright">
                <p>인천공항 주차예약</p>
                <p>편리한 주차, 더 나은 여행</p>
                <p>Copyright © Incheon Airport Parking Reservation. All rights reserved.</p>
            </div>
        </div>
    </footer>

</body>
</html>