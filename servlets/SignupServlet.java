package TrinitasWebsite;

import java.io.*;
import java.net.*;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;

@WebServlet(name = "SignupServlet", urlPatterns = {"/SignupServlet"})
public class SignupServlet extends HttpServlet {
    private static final String SHEETDB_API_URL = "https://sheetdb.io/api/v1/mvx16mkj2oczv";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect("/index.html");
        } else {
            String studentId = (String) session.getAttribute("studentId");
            response.getWriter().println("Welcome, " + studentId);
        }

        // Prevent caching to ensure no outdated information is shown
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String registrationDate = request.getParameter("registrationDate");

        // Validate required fields and student ID format
        if (studentId == null || !studentId.matches("\\d{10}") ||
            name == null || name.isEmpty() ||
            password == null || password.isEmpty() ||
            registrationDate == null || registrationDate.isEmpty()) {
            response.sendRedirect("/signup.html?error=missing_fields");
            return;
        }

        // Prepare JSON data for the POST request to SheetDB
        JSONObject studentData = new JSONObject();
        studentData.put("Student ID", studentId);
        studentData.put("Name", name);
        studentData.put("Password", password); // Note: Passwords should ideally be hashed.
        studentData.put("Registration Date", registrationDate);

        JSONObject signUpData = new JSONObject();
        signUpData.put("data", new JSONObject[]{ studentData });

        // Send POST request to SheetDB
        URL url = new URL(SHEETDB_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(signUpData.toString().getBytes("UTF-8"));
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            response.sendRedirect("/index.html");
        } else {
            response.sendRedirect("/signup.html?error=signup_failed");
        }
    }
}
        
        
      