<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>RSS Reader</title>
    <style>
    .unread{
        color:green;
    }
    .read{
        color:red;
    }
    </style>

</head>
<body>
<jsp:include page="rssList.jsp" />
<jsp:useBean id="feedDB" class="org.strokova.rss.database.FeedDbUtils" scope="page" />
<jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDAO" scope="page" />
<section>

    <c:set var="uri" value="${pageContext.request.requestURI}"/>

    <!-- HOW TO GET 'latest.jsp' FROM URL programmatically??? -->
    <!-- MOVE ORDER TO RSS LIST -->

    <c:set var="order" value="${param.order}" />
    <form action="${uri}">
    <c:choose>
    <c:when test="${!empty order}">
        <input type="checkbox" name="order" value="asc" checked> Oldest first
    </c:when>
    <c:otherwise>
        <input type="checkbox" name="order" value="asc"> Oldest first
    </c:otherwise>
    </c:choose>
        <input type="submit" value="Apply" />
    </form>

    <c:set var="feedItems" value="${feedDAO.getUserFeedItemsLatestPage(sessionScope.userId, param.page, param.order)}" />

    <c:choose>
    <c:when test="${empty feedItems}">
        <b>Add RSS feeds to see the content here</b>
    </c:when>
    <c:otherwise>
        <h2>All</h2>
        <table>
            <c:forEach var="feedItem" items="${feedItems}">
                <tr><td>
                    <h3><a href="${feedItem.link}" class="${feedItem.readStatusAsString}">${feedItem.title}</a></h3>
                    <small style="color:gray;" style="display: inline;">${feedItem.formattedDate}</small>
                    <form action="${uri}?page=${param.page}" method="post" accept-charset="UTF-8" style="display: inline;">
                        <input type="text" name="markRead" value="${feedItem.guid}" style="display: none;" />
                        <input type="submit" value="Mark as read" />
                    </form>
                    <p>${feedItem.description}</p>
                </td></tr>
            </c:forEach>
        </table>
    </c:otherwise>
    </c:choose>

    <jsp:include page="pagination.jsp" />

</section>
</body>
</html>
