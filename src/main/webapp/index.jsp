<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>

<html>
<body>
<h2>Hello World! This is magic</h2>

<jsp:useBean id="feedMng" class="org.strokova.rss.database.FeedDAO" scope="page">
    <c:set var="myfeed" value="${feedMng.feed}" />
    <p>${myfeed.name}</p>
</jsp:useBean>



</body>
</html>
