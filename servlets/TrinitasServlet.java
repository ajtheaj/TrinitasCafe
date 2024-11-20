package TrinitasWebsite;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.*;
import javax.servlet.http.*;

@WebServlet(name = "TrinitasServlet", urlPatterns = {"/TrinitasServlet"})
public class TrinitasServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/menu.html");
        dispatcher.forward(request, response);
        
        HttpSession session = request.getSession(false); // Retrieve the session if it exists

        if (session == null || session.getAttribute("studentId") == null) {
            // No session or studentId attribute, redirect to login page
            response.sendRedirect("/index.html");
        } else {
            // User is logged in, proceed with displaying protected content
            String studentId = (String) session.getAttribute("studentId");
            response.getWriter().println("Welcome, " + studentId);
        }
        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        // Forward to the menu page
        request.getRequestDispatcher("/menu.html").forward(request, response);
        
    }
}