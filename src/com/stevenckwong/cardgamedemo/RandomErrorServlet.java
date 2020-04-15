package com.stevenckwong.cardgamedemo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RandomErrorServlet
 */
@WebServlet("/RandomErrorServlet")
public class RandomErrorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RandomErrorServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// Instructions:
		// use this servlet to turn on/off Random Errors and also set the Percentage of the errors
		//
		// http://url/CardGameDemo/RandomErrorServlet?action=<command>&errorPercent=<newErrorPercent>
		//
		// where <command> could be:
		//
		// 		turnRandomErrorOn
		//		turnRandomErrorOff
		//		setErrorPercent 
		//
		//	setErrorPercent needs a second parameter called errorPercent which will take a number from 1 to 100
		
		
		
		String output = "<h1>RandomErrorManager Settings:</h1><br/><br/> ";
		
		String action = request.getParameter("action");
		if (action==null) {
			// do nothing and just skip to the end 
		} else if (action.contentEquals("turnRandomErrorOn")) {
			RandomErrorManager.turnOnRandomError();
		} else if (action.contentEquals("turnRandomErrorOff")) {
			RandomErrorManager.turnOffRandomError();
		} else if (action.contentEquals("setErrorPercent")) {
			String percent = request.getParameter("errorPercent");
			if (percent==null) {
				percent="5";
			}
			int newPercent = Integer.parseInt(percent);
			
			if (newPercent < 1)
				newPercent = 1;
			else if (newPercent > 100)
				newPercent = 100;
			
			RandomErrorManager.setPercentError(newPercent);
		}
		
		if (RandomErrorManager.randomErrorIsOn()) {
			output += "Random Error: On<br/><br/>";
		} else {
			output += "Random Error: Off<br/><br/>";
		}
		output += "Random Percent: " + RandomErrorManager.getPercentError() + "%<br/><br/>";
		
		response.getWriter().append(output + "Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
