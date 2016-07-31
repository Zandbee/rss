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
    <h2>All</h2>
    <table>
        <c:forEach var="feedItem" items="${feedDAO.getUserFeedItemsLatest(sessionScope.userId)}">
            <tr><td>
                <h3><a href="${feedItem.link}">${feedItem.title}</a></h3>
                <p>${feedItem.description}</p>
            </td></tr>
        </c:forEach>
    </table>
</section>
</body>
</html>
