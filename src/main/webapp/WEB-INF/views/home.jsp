<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Login Page
</h1>

<P>  
	<form name="loginForm" action="/milonga/login" method="post">
  		id: <input type="text" name="id">
  		password: <input type="password" name="password">
  		<input type="submit" name="submitButton" value="login">
	</form> 
</P>
</body>
</html>
