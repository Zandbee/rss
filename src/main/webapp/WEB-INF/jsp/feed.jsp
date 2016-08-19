<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script>
$(document).ready(function(){
    $("#edit_btn").click(function(){
        $("#edit_block").toggle();
   });
});
</script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/feed.css">
</head>
<body>
    <jsp:include page="rssList.jsp" />

    <section>
        <c:set var="feedLink" value="${param.id}"/>

        <h2>${feed.feed_name}</h2>

        <button id="edit_btn" >Edit</button>
        <form id="remove_form" action="feed" method="POST" onsubmit="return confirm('Are you sure you want to delete this Feed?');">
            <input type="text" name="remove" value="${feedLink}" hidden>
            <input type="submit" value="Remove" />
        </form>
        <br><br>
        <form id="edit_block" action="feed" method="POST" accept-charset="UTF-8">
            <input type="text" name="id" value="${feedLink}" hidden>
            <input type="text" name="rename" value="${feedLink}" hidden>
            <input type="text" name="newFeedName" maxlength="${feedNameMaxLength}" required="required" />
            <input type="submit" value="Update RSS name" />
        </form>
        <br><br>

        <table>
            <c:forEach var="feedItem" items="${feedItems}">
                <tr><td>
                    <h3><a href="${feedItem.link}" class="${feedItem.readStatusAsString}">${feedItem.title}</a></h3>
                    <small id="item_date">${feedItem.formattedDate}</small>
                    <form id="mark_read_form" action="markRead" method="post" accept-charset="UTF-8">
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
