<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<style>
    div.pagination {
        display: inline;
    }
</style>
<body>

<jsp:useBean id="feedDAO" class="org.strokova.rss.database.FeedDAO" scope="page" />

<c:set var="servletPath" value="${pageContext.request.servletPath}"/>

<!-- page num -->
<c:if test="${fn:containsIgnoreCase(servletPath, 'latest.jsp')}">
    <c:set var="pageCount" value="${feedDAO.getPageCountInLatest(sessionScope.userId)}"/>
</c:if>
<c:if test="${fn:containsIgnoreCase(servletPath, 'feed.jsp')}">
    <c:set var="pageCount" value="${feedDAO.getPageCountByFeedLink(param.id, sessionScope.userId)}" />
</c:if>

<!-- pages -->
<c:forEach var="i" begin="1" end="${pageCount}">
    <c:url var="url" value="${servletPath}">
        <c:if test="${!empty param.id}"><c:param name="id" value="${param.id}"/></c:if>
        <c:if test="${!empty param.order}"><c:param name="order" value="${param.order}"/></c:if>
        <c:param name="page" value="${i}"/>
    </c:url>
    <div class="pagination">
        <a href="${url}" style="color: gray;">${i}</a>
    </div>
</c:forEach>

</body>
</html>