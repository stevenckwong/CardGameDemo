<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="css/app.css">
<title>Error Encountered</title>
</head>
<body>
<div class="divClassOne">
<% 
	String msg = (String)request.getAttribute("errorMessage");
	out.println("<h1>" + msg + "</h1>");
%>
<br/><br/>
<div class="divClassThree">
	<a href="/CardGameDemo">Back to main page</a>
</div>
</div>

</body>
</html>