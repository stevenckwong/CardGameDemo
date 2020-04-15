package com.stevenckwong.cardgamedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.newrelic.api.agent.DistributedTracePayload;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Trace;

/**
 * Servlet implementation class ThreeCardServlet
 */
@WebServlet("/ThreeCardServlet")
public class ThreeCardServlet extends HttpServlet {

	
	/*
	 * To Do: Make this URL changeable. Defaults to localhost:8888 but changeable
	 */
	static private String lookupServiceURL = "http://localhost:8888/lookup?";
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ThreeCardServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String action = request.getParameter("action");
		if (action.contentEquals("Reset")) {
			HttpSession session = request.getSession();
			session.setAttribute("ThreeCardGame", new ThreeCardGame());
			request.getRequestDispatcher("ThreeCard.jsp").forward(request, response);
		} else {
			response.getWriter().append("Served at: ").append(request.getContextPath());
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		
		NewRelic.setTransactionName("Custom", "/BetAndDrawCard");
		
		try {
			RandomErrorManager.generateRandomError();
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		HttpSession session = request.getSession();
		if (session==null) {
			String errmessage = "User needs to be signed in to play. Please register and/or sign in first";
			request.setAttribute("errMessage",errmessage);
			request.getRequestDispatcher("error.jsp").forward(request, response);
		} else {
			ThreeCardGame currGame = (ThreeCardGame)session.getAttribute("ThreeCardGame");
			String strBetAmount = request.getParameter("betAmount");
			if (strBetAmount==null) {
				strBetAmount = "0";
			}
			int betAmount = Integer.parseInt(strBetAmount);
			currGame.betPool+=betAmount;
			if (currGame.drawNext()==true) {
				// 3 cards drawn and game is done
				int betPool = currGame.getBetPool();
				User currUser = (User)session.getAttribute("signedInUser");
				if (currGame.getWinner().contentEquals(ThreeCardGame.DEALER)) {
					currUser.subtractFromAccount(betPool);
				} else if (currGame.getWinner().contentEquals(ThreeCardGame.DRAW)) {
					currUser.addToAccount(0);
				} else {
					currUser.addToAccount(betPool);
				}
				NewRelic.addCustomParameter("user", currUser.getEmail());
				Segment segment = NewRelic.getAgent().getTransaction().startSegment("/BetAndDrawCard/Segment/LookupGameCommCalcService");
				// Get commission from external service
				String commServiceURL = this.getCommissionServiceURL();
				segment.end();
				
				segment = NewRelic.getAgent().getTransaction().startSegment("/BetAndDrawCard/Segment/GetCommissionFromService");
				int comm = this.getCommission(commServiceURL, betPool);
				segment.end();
				
				NewRelic.addCustomParameter("commission", comm);
								
				currGame.setCommissionCharged(comm);
				// currUser.subtractFromAccount(comm);
								
				segment = NewRelic.getAgent().getTransaction().startSegment("/BetAndDrawCard/Segment/UpdateUserInDB");
				// Update user in DB
				DBService db = new DBService();
				db.saveUser(currUser);
				segment.end();
				
				segment = NewRelic.getAgent().getTransaction().startSegment("/BetAndDrawCard/Segment/LogGameInDB");
				// log game in DB
				db.logGame(currGame);
				segment.end();
				session.setAttribute("signedInUser", currUser);
			}
			session.setAttribute("ThreeCardGame", currGame);
			request.getRequestDispatcher("ThreeCard.jsp").forward(request, response);
		}
		
	}

	@Trace
	private String getCommissionServiceURL() throws IOException {
		
		DistributedTracePayload payload = NewRelic.getAgent().getTransaction().createDistributedTracePayload();
		// Set USER_AGENT
		String USER_AGENT = "Mozilla/5.0";
		
		
		// Make HTTP Request to a web service to get URL and then make another request to get the commission
		String serviceToLookup = "gameCommCalc";
		String url = ThreeCardServlet.lookupServiceURL + serviceToLookup;
		
		if (url.length()==0 || url==null) {
			// default to service running on localhost
			url = "http://localhost:8888/lookup?gameCommCalc";
		}
				
		Segment segment = NewRelic.getAgent().getTransaction().startSegment("/BetAndDrawCard/Segment/External/LookupCommServiceURL");
		
		URL obj = new URL(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		try {
			RandomErrorManager.generateRandomError();
		} catch (Exception e) {
			con.disconnect();
			throw new IOException(e);
		}

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("NR-Payload", payload.httpSafe());
		
		
		int responseCode = con.getResponseCode();
		// System.out.println("\nSending 'GET' request to URL : " + url + " with header NR-Payload + " + payload.httpSafe() + ", User-Agent: " + USER_AGENT);
		System.out.println("LookupCommServiceURL - Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		segment.end();
		
		//print result
		String commServiceURL = response.toString();
		
		return commServiceURL;
	}
	
	
	@Trace
	private int getCommission(String commServiceURL, int winnings) throws IOException {
		
		DistributedTracePayload payload = NewRelic.getAgent().getTransaction().createDistributedTracePayload();
		
		int comm = 0;
		
		// Set USER_AGENT
		String USER_AGENT = "Mozilla/5.0";
		
		
		// Make HTTP Request to a web service to get URL and then make another request to get the commission
		//String url = "http://localhost:9090?"+winnings;
		String url = commServiceURL+"?"+winnings;
		
		Segment segment = NewRelic.getAgent().getTransaction().startSegment("/BetAndDrawCard/Segment/External/GetCommAmtFromCommService");
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		try {
			RandomErrorManager.generateRandomError();
		} catch (Exception e) {
			con.disconnect();
			throw new IOException(e);
		}

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("NR-Payload", payload.httpSafe());

		int responseCode = con.getResponseCode();
				
		// System.out.println("\nSending 'GET' request to URL : " + url + " with NR-Payload: " + payload.httpSafe());
		System.out.println("GetCommAmtFromCommService - Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		segment.end();

		//print result
		comm = Integer.parseInt(response.toString());
		
		return comm;
	}
	
}
