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

    <ul>
        <li><a href="latest.jsp">All</a></li>
        <c:forEach var="feed" items="${feedDAO.getUserFeeds(sessionScope.userId)}">
            <li><a href="feed.jsp?id=${feed.feed_link}">${feed.feed_name}</a></li>
        </c:forEach>
    </ul>

    <br>
    <form action="addRss" method="post" accept-charset="UTF-8">
        <b>Add new RSS</b> <br>
        Link:<br> <input type="text" name="rss_link" required="required" /><br>
        Name:<br> <input type="text" name="rss_name" required="required" /><br><br>
        <input type="submit" value="Add" />
    </form>
    </nav>
</body>
</html>