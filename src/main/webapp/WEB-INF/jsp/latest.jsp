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
                    <small style="color:gray;" style="display: inline;">${feedItem.formattedDate}</small>
                    <form action="markRead" method="post" accept-charset="UTF-8" style="display: inline;">
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
