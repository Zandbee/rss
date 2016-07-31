<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<style>
nav {
    float:left;
    width:18%;
}
section {
    margin-left: 18%;
    padding: 1em;
    border-left:1px solid gray;
}
</style>
<body>
    <nav>
    <jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDbUtils" scope="page" />

    <table>
        <c:forEach var="feed" items="${feedDAO.getUserFeeds(sessionScope.userId)}">
            <tr><td>
                <a href="${pageContext.request.contextPath}/feed?id=${feed.feed_link}">${feed.feed_name}</a>
            </td></tr>
        </c:forEach>
    </table>
    </nav>
</body>
</html>