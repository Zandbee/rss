<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>RSS Reader</title>
</head>
<body>
<jsp:include page="rssList.jsp" />
<jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDbUtils" scope="page" />
<section>
    <c:set var="feedItems" value="${feedDAO.getUserFeedItemsLatest(sessionScope.userId)}" />
    <c:choose>
    <c:when test="${empty feedItems}">
        <b>Add RSS feeds to see the content here</b>
    </c:when>
    <c:otherwise>
        <h2>All</h2>
        <table>
            <c:forEach var="feedItem" items="${feedItems}">
                <tr><td>
                    <h3><a href="${feedItem.link}">${feedItem.title}</a></h3>
                    <small style="color:gray;">${feedItem.formattedDate}</small>
                    <p>${feedItem.description}</p>
                </td></tr>
            </c:forEach>
        </table>
    </c:otherwise>
    </c:choose>

    <jsp:include page="pagination.jspf" />

</section>
</body>
</html>
