
package TrinitasWebsite;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    private static final String SHEETDB_API_URL = "https://sheetdb.io/api/v1/mvx16mkj2oczv/search?Student%20ID=";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        String password = request.getParameter("password");
        
        if (studentId == null || password == null) {
            response.sendRedirect("login.html?error=missing_credentials");
            return;
        }
        
        if (!studentId.matches("\\d{10}")) {
            // Redirect back to login page with an error message
            response.sendRedirect("index.html?error=invalid_id");
            return;
        }
        
        URL url = new URL(SHEETDB_API_URL + studentId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONArray jsonArray = new JSONArray(content.toString());
            if (jsonArray.length() > 0) {
                JSONObject user = jsonArray.getJSONObject(0);
                String storedPassword = user.getString("password");

                // Compare stored password with the one entered
                if (password.equals(storedPassword)) {
                    // Successful login
                    response.sendRedirect("menu.html");
                } else {
                    // Invalid password
                    response.sendRedirect("index.html?error=invalid_credentials");
                }
            } else {
                // Student ID not found
                response.sendRedirect("signup.html?error=invalid_id");
            }
        } else {
            // Error handling if SheetDB API call fails
            response.sendRedirect("index.html?error=server_error");
        }
    }
}