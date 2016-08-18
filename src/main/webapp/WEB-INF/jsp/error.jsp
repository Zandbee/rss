<%@ page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>

<head>
<title>Error</title>
</head>

<body>
<h2>Something went wrong :(</h2>
<p>${pageContext.errorData.throwable.message}</p>
</body>

</html>