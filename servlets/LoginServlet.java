package TrinitasWebsite;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import org.json.*;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    private static final String SHEETDB_API_URL = "https://sheetdb.io/api/v1/a3v9fypr110rc/search?Student%20ID=";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String studentId = request.getParameter("studentId");
        String password = request.getParameter("password");
        JSONObject jsonResponse = new JSONObject();

        // Validate input
        if (studentId == null || password == null || studentId.isEmpty() || password.isEmpty()) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "missing_credentials");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        if (!studentId.matches("\\d{10}")) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "invalid_id");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        // Construct API URL
        URL url = new URL(SHEETDB_API_URL + studentId);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                JSONArray jsonArray = new JSONArray(content.toString());
                if (jsonArray.length() > 0) {
                    JSONObject user = jsonArray.getJSONObject(0);
                    String storedPassword = user.optString("Password", "");

                    if (password.equals(storedPassword)) {
                        // Successful login
                        HttpSession session = request.getSession(true);
                        session.setAttribute("studentId", studentId);
                        session.setMaxInactiveInterval(15 * 60);

                        jsonResponse.put("status", "success");
                        jsonResponse.put("redirect", "menu.html");
                    } else {
                        jsonResponse.put("status", "error");
                        jsonResponse.put("message", "invalid_credentials");
                    }
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "invalid_id");
                }
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "server_error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "server_error");
        } finally {
            conn.disconnect();
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
