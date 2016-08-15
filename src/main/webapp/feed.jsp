<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<style>
    div.pagination {
        display: inline;
    }
    .unread{
        color:green;
    }
    .read{
        color:red;
    }
</style>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script>
$(document).ready(function(){
    $("#editBtn").click(function(){
        $("#editBlock").toggle();
   });
});
</script>
<body>
    <jsp:include page="rssList.jsp" />
    <jsp:useBean id="feedDB" class="org.strokova.rss.database.FeedDbUtils" scope="page" />
    <jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDAO" scope="page" />

    <c:set var="uri" value="${pageContext.request.requestURI}"/>

    <section>

        <c:set var="feedLink" value="${param.id}"/>
        <c:set var="feed" value="${feedDB.getSubscriptionWithFeedByFeedLink(feedLink)}"/>

        <h2>${feed.feed_name}</h2>

        <button id="editBtn" style="display: inline;">Edit</button>
        <form action="${uri}" style="display: inline;" method="POST" onsubmit="return confirm('Are you sure you want to delete this Feed?');">
            <input type="text" name="remove" value="${feedLink}" hidden>
            <input type="submit" value="Remove" />
        </form>
        <br><br>
        <c:url var="editUrl" value="${servletPath}">
            <c:param name="id" value="${param.id}"/>
        </c:url>
        <form id="editBlock" action="${editUrl}" style="display: none;" method="POST" >
            <input type="text" name="rename" value="${feedLink}" hidden>
            <input type="text" name="newFeedName" required="required" />
            <input type="submit" value="Update RSS name" />
        </form>
        <br><br>

        <table>
            <c:forEach var="feedItem" items="${feedDAO.getFeedItemsByFeedLinkPage(sessionScope.userId, feedLink, param.page, param.order)}">
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

        <jsp:include page="pagination.jsp" />

    </section>
</body>
</html>
