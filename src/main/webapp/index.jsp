<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>

<html>
<body>
<h2>Hello World! This is magic</h2>

<jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDAO" scope="page">
    <c:set var="myfeed" value="${feedDAO.feed}" />
    <p>${myfeed.title}</p>
</jsp:useBean>

<table border="1">
<c:forEach var="feedItem" items="${feedDAO.feedItems}">
    <tr><td>
        <h4>${feedItem.title}</h4>
        <p>${feedItem.description}</p>
        <a href="">${feedItem.link}</a>
    </td></tr>
</c:forEach>
</table>

</body>
</html>
