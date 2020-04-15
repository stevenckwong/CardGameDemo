<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="com.stevenckwong.cardgamedemo.User" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="css/app.css">
<title>Welcome Back</title>
</head>
<body>
<%
	User user = (User)request.getSession().getAttribute("signedInUser");
%>
<div class="divClassOne">
<h1>Welcome back <%=user.getFirstName()%></h1>
<br/>
<div class="divClassTwo">
	<br/><br/>
	<span class="panelMainTitle">Your Account</span>
	<br/><br/>
	<span class="panelTitle"><%=user.getFirstName() %> <%=user.getLastName() %></span><br/><br/>
	<span class="colTitle">Your account balance is: <span class="colData">$<%=user.getAccountBalance() %></span></span>
	<br/><br/>
	<div class="divClassThree">
		<a href="ThreeCardServlet?action=Reset">Start a 3 card game</a>
	</div>
	<div class="divClassThree">
		<a href="SignOutServlet">Sign Out</a>
	</div>
	<br/><br/>
	<br/><br/>
</div>
<br/>
</div>
</body>
</html>