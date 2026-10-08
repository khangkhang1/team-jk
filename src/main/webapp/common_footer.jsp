<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link href="${pageContext.request.contextPath}/css/common/common_footer.css" rel="stylesheet">


<footer class="footer">

    <div class="footer_inner">

        <div class="footer_top">

            <div class="footer_logo">
                <fmt:message key="hdr.001"/>

                <small>
                    INCHEON AIRPORT PARKING
                </small>
            </div>

            <div class="footer_links">
                <a href="#"><fmt:message key="ftr.001"/></a>
                <a href="#"><fmt:message key="ftr.002"/></a>
                <a href="#"><fmt:message key="ftr.003"/></a>
            </div>

        </div>

        <div class="footer_info">

            <p>
                <fmt:message key="ftr.004"/>
            </p>

            <p>
                <fmt:message key="ftr.005"/>
            </p>

            <p class="copyright">
                Copyright © Parking Reservation Project. All rights reserved.
            </p>

        </div>

    </div>

</footer>