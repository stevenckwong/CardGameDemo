<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="com.stevenckwong.cardgamedemo.User" %>
    <%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="css/app.css">
<title>User Administration</title>
</head>
<body>
<%
	ArrayList<User> users = (ArrayList<User>)request.getAttribute("Users");
%>
<div class="divClassOne">
<h1>User Administration</h1>
<br/>
<div class="divClassTwo">
	<br/><br/>
	<span class="panelMainTitle">All Users on the System</span>
	<br/><br/>
	<table class="center">
		<tr>
			<td class="colTitle">First Name</td>
			<td class="colTitle">Last Name</td>
			<td class="colTitle">Email</td>
			<td class="colTitle">Balance</td>
			<td>Delete</td>
		</tr>
		<%
			for (int i=0;i<users.size();i++) {
		%>
			<tr>
				<td class="colData"><%= users.get(i).getFirstName() %></td>
				<td class="colData"><%= users.get(i).getLastName() %></td>
				<td class="colData"><%= users.get(i).getEmail() %></td>
				<td class="colData"><%= users.get(i).getAccountBalance() %></td>
				<td class="colData"><a href="deleteUserServlet?email=<%= users.get(i).getEmail() %>">Delete</a></td>
			</tr>
		<% 
			}
		%>
	</table>
	<br/><br/>
	<div class="divClassThree"><a href="index.html">Back to Main Screen</a></div>
</div>
<br/>
</div>
</body>
</html>