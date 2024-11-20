package TrinitasWebsite;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;

@WebServlet(name = "CheckSessionServlet", urlPatterns = {"/CheckSessionServlet"})
public class CheckSessionServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        JSONObject jsonResponse = new JSONObject();

        try (PrintWriter out = response.getWriter()) {
            if (session != null && session.getAttribute("studentId") != null) {
                String studentId = (String) session.getAttribute("studentId");
                jsonResponse.put("studentId", studentId);
                jsonResponse.put("isLoggedIn", true);
            } else {
                jsonResponse.put("isLoggedIn", false);
            }
            out.print(jsonResponse.toString());
        }
    }
}