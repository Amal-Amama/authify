package in.amalamama.authify.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//this is a global exception
//Tells Spring Security:
//“If authentication fails (user not logged in), use CustomAuthEntryPoint to send the response.”
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    //AuthenticationEntryPoint = Spring Security interface
    //=>Called whenever a request tries to access a secured endpoint without valid authentication.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //request → incoming HTTP request
        //response → HTTP response you can write to
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        //Returns a JSON message to the client explaining that the user is not authenticated
        response.getWriter().write("{\"authenticated\":false, \"message\": \"User is not authenticated\"}");
    }
}
