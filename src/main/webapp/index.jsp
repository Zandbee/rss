<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>RSS Reader</title>
</head>
<body>
<h2>Hello World! This is magic</h2>

<jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDbUtils" scope="page">
    <c:set var="myfeed" value="${feedDAO.feed}" />
    <p>${myfeed.title}</p>
</jsp:useBean>

<c:choose>
    <c:when test="${empty sessionScope.user}">
        <p>No user</p>
        <c:redirect url="/login.jsp" />
    </c:when>
    <c:otherwise>
        <table border="1">
            <c:forEach var="feedItem" items="${feedDAO.feedItems}">
                <tr><td>
                    <h3><a href="${feedItem.link}">${feedItem.title}</a></h3>
                    <p>${feedItem.description}</p>
                </td></tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>
