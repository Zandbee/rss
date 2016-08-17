<%@ page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<title>Error</title>
</head>
<h3>ServerError</h3>
<p>${pageContext.errorData.throwable.cause}</p>
</body>
</html>