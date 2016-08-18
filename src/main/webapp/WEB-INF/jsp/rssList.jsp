<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<style>
#menu {
    float:left;
    width:18%;
}
section {
    margin-left: 18%;
    padding: 1em;
    border-left:1px solid gray;
}
hr {
    display: block;
    margin-top: 2em;
    margin-bottom: 2em;
    margin-left: 0.5em;
    margin-right: 0.5em;
    border-style: inset;
    border-width: 1px;
}
</style>
</head>
<body>
    <nav id="menu">
    <c:if test="${!empty subscriptions}">
    <ul>
        <form action="updateUserFeedItems" method="post" accept-charset="UTF-8">
            <input type="submit" value="Update all" />
        </form>
        <br>

        <li><a href="latest">All</a></li>

        <c:forEach var="subscription" items="${subscriptions}">
            <c:url var="feedEncodedUrl" value="/feed">
                 <c:param name="id" value="${subscription.encodedFeedLink}"/>
            </c:url>
            <li><a href="${feedEncodedUrl}">${subscription.feed_name}</a></li>
        </c:forEach>
    </ul>
    </c:if>
    <hr>

    <c:set var="order" value="${param.order}" />
    <form action="${uri}" accept-charset="UTF-8">
        <c:if test="${!empty param.id}"><input type="text" name="id" value="${param.id}" hidden></c:if>
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
    <hr>

    <form action="addRss" method="post" accept-charset="UTF-8">
        <b>Add new RSS</b> <br>
        Link:<br> <input type="text" name="rss_link" required="required" /><br>
        Name:<br> <input type="text" name="rss_name" required="required" /><br><br>
        <input type="submit" value="Add" />
    </form>
    </nav>
</body>
</html>