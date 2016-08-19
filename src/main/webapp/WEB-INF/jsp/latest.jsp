<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>RSS Reader</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/latest.css">
</head>
<body>
<jsp:include page="rssList.jsp" />
<section id="feed_list">

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
                    <small id="item_date">${feedItem.formattedDate}</small>
                    <form action="markRead" method="post" id="mark_read_form" accept-charset="UTF-8">
                        <input type="text" name="markRead" value="${feedItem.guid}" hidden>
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
