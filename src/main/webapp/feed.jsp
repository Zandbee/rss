<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<style>
    div.pagination {
        display: inline;
    }
</style>
<body>
    <jsp:include page="rssList.jsp" />
    <jsp:useBean id="feedDB" class="org.strokova.rss.database.FeedDbUtils" scope="page" />
    <jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDAO" scope="page" />
    <section>
        <c:set var="feedLink" value="${param.id}" />
        <c:set var="feed" value="${feedDB.getSubscriptionWithFeedByFeedLink(feedLink)}"/>
        <h2>${feed.feed_name}</h2>

        <form action="" style="display: inline;">
            <input type="submit" value="Edit" />
        </form>
        <form action="feed.jsp?remove=${feedLink}" style="display: inline;" method="POST">
            <input type="submit" value="Remove" />
        </form>
        <br><br>

        <table>
            <c:forEach var="feedItem" items="${feedDAO.getFeedItemsByFeedLinkPage(feedLink, param.page)}">
                <tr><td>
                    <h3><a href="${feedItem.link}">${feedItem.title}</a></h3>
                    <small style="color:gray;">${feedItem.formattedDate}</small>
                    <p>${feedItem.description}</p>
                </td></tr>
            </c:forEach>
        </table>

        <c:set var="pageCount" value="${feedDAO.getPageCountByFeedLink(feedLink)}" />
        <c:forEach var="i" begin="1" end="${pageCount}">
            <div class="pagination">
                <a href="feed.jsp?id=${feedLink}&page=${i}">${i}</a>
            </div>
        </c:forEach>

    </section>
</body>
</html>
