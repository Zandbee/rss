<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<style>
    div.pagination {
        display: inline;
    }
    .itemName.0{
        color:green;
    }
    .itemName.1{
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
    <section>
        <c:set var="feedLink" value="${param.id}" />
        <c:set var="feed" value="${feedDB.getSubscriptionWithFeedByFeedLink(feedLink)}"/>
        <h2>${feed.feed_name}</h2>

        <button id="editBtn" style="display: inline;">Edit</button>
        <form action="feed.jsp?remove=${feedLink}" style="display: inline;" method="POST" onsubmit="return confirm('Are you sure you want to delete?');">
            <input type="submit" value="Remove" />
        </form>
        <br><br>
        <form action="latest.jsp?page=${param.page}" method="post" accept-charset="UTF-8">
            <input type="text" name="markRead" value="${feedItem.guid}" style="display: none;" />
            <input type="submit" value="Mark as read" />
        </form>
        <br><br>

        <table>
            <c:forEach var="feedItem" items="${feedDAO.getFeedItemsByFeedLinkPage(feedLink, param.page)}">
                <tr><td>
                    <h3><a href="${feedItem.link}" style="display: inline;" >${feedItem.title}</a></h3>
                    <form action="latest.jsp?markRead=${feedItem.guid}" method="post" accept-charset="UTF-8" style="display: inline;">
                        <input type="submit" value="Mark as read" />
                    </form>
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
