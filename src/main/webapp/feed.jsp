<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<body>
    <jsp:include page="rssList.jsp" />
    <jsp:useBean id="feedDB" class="org.strokova.rss.database.FeedDbUtils" scope="page" />
    <section>
        <c:set var="feedLink" value="${param.id}" />
        <c:set var="feed" value="${feedDB.getSubscriptionWithFeedByFeedLink(feedLink)}"/>
        <h2>${feed.feed_name}</h2>
        <table>
            <c:forEach var="feedItem" items="${feedDB.getFeedItemsByFeedLink(feedLink)}">
                <tr><td>
                    <h3><a href="${feedItem.link}">${feedItem.title}</a></h3>
                    <small style="color:gray;">${feedItem.formattedDate}</small>
                    <p>${feedItem.description}</p>
                </td></tr>
            </c:forEach>
        </table>
    </section>
</body>
</html>
