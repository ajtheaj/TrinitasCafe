package TrinitasWebsite;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import org.json.*;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    private static final String SHEETDB_API_URL = "https://sheetdb.io/api/v1/mvx16mkj2oczv/search?Student%20ID=";
    
    // Handle login (POST request)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        String password = request.getParameter("password");
        
        // Authenticate user
        if (authenticateUser(studentId, password)) {
            // Invalidate any existing session, then create a new one
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            session = request.getSession(true);
            session.setAttribute("studentId", studentId);

            response.sendRedirect("/menu.html");
        } else {
            response.sendRedirect("/index.html?error=invalid_credentials");
        }
        
        if (studentId == null || password == null) {
            response.sendRedirect("index.html?error=missing_credentials");
            return;
        }

        if (!studentId.matches("\\d{10}")) {
            response.sendRedirect("/index.html?error=invalid_id");
            return;
        }
        
    }

    // Check if the user is logged in (GET request)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Get existing session without creating a new one

        // Prevent caching of this page to avoid seeing stale data after logout
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (session == null || session.getAttribute("studentId") == null) {
            // No valid session, redirect to login page
            response.sendRedirect("/index.html");
        } else {
            // User is logged in, show a welcome message or forward to a protected page
            response.getWriter().println("Welcome, " + session.getAttribute("studentId"));
        }
    }

    // Helper method for authentication
    private boolean authenticateUser(String studentId, String password) {
        try {
            URL url = new URL(SHEETDB_API_URL + studentId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // Successful API call
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                JSONArray jsonArray = new JSONArray(content.toString());
                if (jsonArray.length() > 0) {
                    JSONObject user = jsonArray.getJSONObject(0);
                    String storedPassword = user.getString("password");

                    // Compare stored password with entered password
                    return password.equals(storedPassword); // True if password matches
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // False if authentication fails or an exception occurs
    }
}