package in.amalamama.authify.filter;

import in.amalamama.authify.service.CustomUserDetailsService;
import in.amalamama.authify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS= List.of("/login",
            "/register",
            "/send-reset-otp",
            "/reset-password",
            "/logout");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //It extracts the path of the request relative to the application context.
        String path=request.getServletPath(); //means which api is being called.
        //Skipping JWT logic for public endpoints
        if(PUBLIC_URLS.contains(path)){
         filterChain.doFilter(request,response);
         return;
        }
        String jwt=null;
        String email=null;
        //1- check the authorization header
        final String authorizationHeader=request.getHeader("Authorization");
        //we wanna consomme the jwt token from the authorization header.
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
           jwt= authorizationHeader.substring(7);
        }
        //2- if not found (authorizationHeader), check the cookies
        if(jwt==null){
            Cookie[] cookies=request.getCookies();
            if(cookies!=null){
                for(Cookie cookie:cookies){
                    if("jwt".equals(cookie.getName())){
                        jwt=cookie.getValue();
                        break;
                    }
                }
            }
        }
        //3- validate the token and set security context
        if(jwt!=null){
            email=jwtUtil.extractEmail(jwt);
            if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails=customUserDetailsService.loadUserByUsername(email);
                if(jwtUtil.validateToken(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken=
                          new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
