<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="com.stevenckwong.cardgamedemo.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="css/app.css">
<title>Three Card Game</title>
</head>
<body>
<div class="divClassOne">
	<%
		ThreeCardGame game = (ThreeCardGame)request.getSession().getAttribute("ThreeCardGame");
		if (game==null){
			game = new ThreeCardGame();
			request.getSession().setAttribute("ThreeCardGame", game);
		}
	%>
	<div class="panelTitle">Dealer</div>
	<div class="divClassTwo">
		<table class="center">
			<tr>
				<td> </td><td class="panelTitle">Card 1</td><td class="panelTitle">Card 2</td><td class="panelTitle">Card 3</td>
			</tr>
			<tr>
				<td class=cardNoBorder>==></td>
				<% for (int i=1; i<=game.getCardsDrawn(); i++) {%>
				<td class="cardNoBorder"><%= game.getDealerCard(i) %></td>
				<%} %>
			</tr>
		</table>
	</div>
	<hr>
	<div class="divClassTwo">
		<table class="center">
			<tr>
				<td> </td><td class="panelTitle">Card 1</td><td class="panelTitle">Card 2</td><td class="panelTitle">Card 3</td>
			</tr>
			<tr>
				<td class=cardNoBorder>==></td>
				<% for (int i=1; i<=game.getCardsDrawn(); i++) {%>
				<td class="cardNoBorder"><%= game.getPlayerCard(i) %></td>
				<%} %>
			</tr>
		</table>
	</div>
	<span class="panelTitle">Player</span>
	<hr>
	<div class="divClassTwo">
	<% if (game.getCardsDrawn() < 3) {%>
	<form name="betForm" action="ThreeCardServlet" method="POST">
		<br/><br/>
		Bet Amount: <input type="text" name="betAmount" value="10" width="70%"/><br/>
		<br/><br/>
		<input type="submit" value="Bet and Draw" />
		
	</form>
	<% } else {%>
		
			<span class="panelTitle">Conclusion:</span> <br/><br/>
			<span class="colData">Winner:</span><span class="colTitle"><%= game.getWinner() %></span><br/>
			<span class="colData">Bet Pool:</span><span class="colTitle"><%= game.getBetPool() %></span><br/>
			<span class="colData">Commission Charged:</span><span class="colTitle"><%= game.getCommissionCharged() %></span>
			<br/><br/>
			<div class="divClassThree">
				<a href="ThreeCardServlet?action=Reset">Play another game</a>
			</div>
			<div class="divClassThree">
				<a href="main.jsp">Back to Main</a>
			</div>
		
	<%} %>
	</div>
	
	</form>
</div>
</body>
</html>