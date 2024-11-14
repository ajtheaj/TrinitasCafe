
package TrinitasWebsite;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
@WebServlet(name = "SignupServlet", urlPatterns = {"/SignupServlet"})
public class SignupServlet extends HttpServlet {
    private static final String SHEETDB_API_URL = "https://sheetdb.io/api/v1/mvx16mkj2oczv/search?Student%20ID=";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String registrationDate = request.getParameter("registrationDate");

        // Server-side validation: Ensure all fields are provided and Student ID is 10 digits
        if (studentId == null || !studentId.matches("\\d{10}") ||
            name == null || name.isEmpty() ||
            password == null || password.isEmpty() ||
            registrationDate == null || registrationDate.isEmpty()) {
            response.sendRedirect("signup.html?error=missing_fields");
            return;
        }
        
         // Prepare JSON data for the POST request to SheetDB
        JSONObject signUpData = new JSONObject();
        JSONObject studentData = new JSONObject();
        studentData.put("Student ID", studentId);
        studentData.put("Name", name);
        studentData.put("Password", password); // Remember to hash in production
        studentData.put("Registration Date", registrationDate);
        signUpData.put("data", new JSONObject[] { studentData });

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
        
        // Check if data was successfully added
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) { // HTTP 201 Created
            response.sendRedirect("index.html");
        } else {
            response.sendRedirect("signup.html?error=signup_failed");
        }
    }
}
        
        
      