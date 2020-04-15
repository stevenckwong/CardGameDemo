package com.stevenckwong.cardgamedemo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.newrelic.api.agent.*;

/**
 * Servlet implementation class SignInServlet
 */
@WebServlet("/SignInServlet")
public class SignInServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignInServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		
		NewRelic.setTransactionName("Custom", "/Sign_In");
		
		try {
			RandomErrorManager.generateRandomError();
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		String email = request.getParameter("emailAddress");
		DBService mongoDBService = new DBService();
		User user = mongoDBService.lookupUser(new User("","",email,"",0));
		if (user==null) {
			String errMessage = "This user with email address " + email + " cannot be found. Please register from main screen to play";
			request.setAttribute("errorMessage", errMessage);
			request.getRequestDispatcher("error.jsp").forward(request, response);
		} else {
			// request.setAttribute("signedInUser", user);
			HttpSession session = request.getSession(true);
			session.setAttribute("signedInUser", user);
			request.getRequestDispatcher("main.jsp").forward(request,response);
		}
			
	}

}
