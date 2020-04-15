<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Error Encountered</title>
</head>
<body>
<% 
	String msg = (String)request.getAttribute("successMessage");
	out.println("<h1>" + msg + "</h1>");
%>
<br/><br/>
<a href="/CardGameDemo">Back to main page</a>


</body>
</html>