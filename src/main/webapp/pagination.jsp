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

<c:set var="uri" value="${pageContext.request.requestURI}"/>
<c:set var="context" value="${pageContext.request.contextPath}"/>
<c:set var="pathInfo" value="${pageContext.request.pathInfo}"/>

<!-- page num -->
<c:if test="${fn:containsIgnoreCase(uri, 'latest.jsp')}">
    <c:set var="pageCount" value="${feedDAO.getPageCountInLatest(sessionScope.userId)}"/>
</c:if>
<c:if test="${fn:containsIgnoreCase(uri, 'feed.jsp')}">
    <c:set var="pageCount" value="${feedDAO.getPageCountByFeedLink(param.id, sessionScope.userId)}" />
</c:if>

<!-- url -->
<c:url var="url" value="${uri}">
    <c:if test="${!empty param.id}"><c:param name="id" value="${param.id}"/></c:if>
    <c:if test="${!empty param.order}"><c:param name="order" value="${param.order}"/></c:if>
</c:url>


<p>${url}</p>
<p>${uri}</p>
<p>${context}</p>
<p>${pathInfo}</p>
<!-- pages -->
<c:forEach var="i" begin="1" end="${pageCount}">
    <div class="pagination">
        <a href="${url.equalsIgnoreCase(uri) ? url.concat('?page=').concat(i) : url.concat('&page=').concat(i)}" style="color: gray;">${i}</a>
    </div>
</c:forEach>

</body>
</html>