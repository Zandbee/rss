<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<link rel="stylesheet" type="text/css" href="pagination.css">
<body>

<c:forEach var="i" begin="1" end="${pageCount}">
    <c:url var="paginationUrl" value="${servletPattern}">
        <c:if test="${!empty param.id}"><c:param name="id" value="${param.id}"/></c:if>
        <c:if test="${!empty param.order}"><c:param name="order" value="${param.order}"/></c:if>
        <c:param name="page" value="${i}"/>
    </c:url>
    <div class="pagination">
        <a href="${paginationUrl}" style="color: gray;">${i}</a>
    </div>
</c:forEach>

</body>
</html>