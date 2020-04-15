<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="css/app.css">
<title>Register New User</title>
</head>
<body>
<div class="divClassOne">
<h1>Sign Up to be a Player</h1>
<form name="NewUserRegistrationForm" action="SignUpServlet" method="POST">
	<div class="divClassTwo">
	<br/>
	First Name: <input type="text" name="firstName" size=30 /><br/>
	Last Name: <input type="text" name="lastName" size=30 /><br/>
	Email Addr: <input type="text" name="emailAddress" size=30 /><br/>
	<br/>
	<input type="submit" name="submitBtn" value="Sign Me Up!" />
	<br/><br/>
	</div>
</form>
</div>
</body>
</html>