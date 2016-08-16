<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Registration</title>
</head>
<body>
    <c:if test="${!empty error}"><p style="color:red">${error}</p></c:if>

    <form action="registration" method="post" accept-charset="UTF-8">
        Name:<br> <input type="text" name="username" required="required" /><br>
        Password:<br> <input type="password" name="userpass" required="required" /><br><br>
        <input type="submit" value="Register" /><br><br>
        <a href="login">Already have account?</a>
    </form>

</body>
</html>