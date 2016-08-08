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
    <jsp:useBean id="feedDB" class="org.strokova.rss.database.FeedDbUtils" scope="page" />
    <c:set var="subscriptions" value="${feedDB.getUserSubscriptionsWithFeeds(sessionScope.userId)}" scope="page" />

    <c:if test="${!empty subscriptions}">
    <ul>
        <li><a href="latest.jsp">All</a></li>
        <c:forEach var="subscription" items="${subscriptions}">
            <li><a href="feed.jsp?id=${subscription.feed_link}">${subscription.feed_name}</a></li>
        </c:forEach>
    </ul>
    </c:if>

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