package com.stevenckwong.cardgamedemo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.newrelic.api.agent.NewRelic;

/**
 * Servlet implementation class SignUpServlet
 */
@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUpServlet() {
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
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("emailAddress");
		User newUser = new User(firstName, lastName, email, "nopassword", 100);
		
		NewRelic.setTransactionName("Custom", "/RegisterUser");
		
		DBService mongoDBService = new DBService();
		try {
			mongoDBService.saveUser(newUser);
			String successMessage = "User with email " + email + " have been successfully registered";
			request.setAttribute("successMessage", successMessage);
			request.getRequestDispatcher("success.jsp").forward(request,response);
		} catch (com.mongodb.DuplicateKeyException e) {
			String errMessage = "This user with email address " + email + " already exists. Please register with another email address";
			request.setAttribute("errorMessage", errMessage);
			request.getRequestDispatcher("error.jsp").forward(request, response);
		}
	}

}
