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

<!-- page num -->
<c:if test="${fn:containsIgnoreCase(uri, 'latest.jsp')}">
    <c:set var="pageCount" value="${feedDAO.getPageCountInLatest(sessionScope.userId)}"/>
</c:if>
<c:if test="${fn:containsIgnoreCase(uri, 'feed.jsp')}">
    <c:set var="pageCount" value="${feedDAO.getPageCountByFeedLink(param.id, sessionScope.userId)}" />
</c:if>

<!-- url -->
<c:set var="url" value="${uri}"/>

<!-- url id -->
<c:if test="${!empty param.id}">
    <c:set var="url" value="${url.equalsIgnoreCase(uri) ? url.concat('?id=').concat(param.id) : url.concat('&id=').concat(param.id)}" />
</c:if>

<!-- url order -->
<c:if test="${!empty param.order}">
    <c:set var="url" value="${url.equalsIgnoreCase(uri) ? url.concat('?order=').concat(param.order) : url.concat('&order=').concat(param.order)}" />
</c:if>


<!-- pages -->
<c:forEach var="i" begin="1" end="${pageCount}">
    <div class="pagination">
        <a href="${url.equalsIgnoreCase(uri) ? url.concat('?page=').concat(i) : url.concat('&page=').concat(i)}" style="color: gray;">${i}</a>
    </div>
</c:forEach>

</body>
</html>