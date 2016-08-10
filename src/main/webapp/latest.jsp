<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>RSS Reader</title>
    <style>
    div.pagination {
        display: inline;
    }
    </style>


    <!--script src="http://code.jquery.com/jquery-latest.min.js"></script>
            <script>
                $(document).on("submit", "#markReadForm", function() {
                    $.post();
                });
    </script-->


</head>
<body>
<jsp:include page="rssList.jsp" />
<jsp:useBean id="feedDB" class="org.strokova.rss.database.FeedDbUtils" scope="page" />
<jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDAO" scope="page" />
<section>
    <c:set var="feedItems" value="${feedDAO.getUserFeedItemsLatestPage(sessionScope.userId, param.page)}" />
    <c:choose>
    <c:when test="${empty feedItems}">
        <b>Add RSS feeds to see the content here</b>
    </c:when>
    <c:otherwise>
        <h2>All</h2>
        <table>
            <c:forEach var="feedItem" items="${feedItems}">
                <tr><td>
                    <h3><a href="${feedItem.link}">${feedItem.title}</a></h3>
                    <form action="latest.jsp?page=${param.page}" method="post" accept-charset="UTF-8">
                        <input type="text" name="markRead" value="${feedItem.guid}" style="display: none;" />
                        <input type="submit" value="Mark as read" />
                    </form>
                    <small style="color:gray;">${feedItem.formattedDate}</small>
                    <p>${feedItem.description}</p>
                </td></tr>
            </c:forEach>
        </table>
    </c:otherwise>
    </c:choose>

    <c:set var="pageCount" value="${feedDAO.getPageCountInLatest(sessionScope.userId)}" />
    <c:forEach var="i" begin="1" end="${pageCount}">
        <div class="pagination">
            <a href="latest.jsp?page=${i}">${i}</a>
        </div>
    </c:forEach>

</section>
</body>
</html>
