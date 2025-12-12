package in.amalamama.authify.config;

import in.amalamama.authify.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;
@Configuration
@EnableWebSecurity // This tells Spring Boot that this class contains security configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    // This defines the main security rules for HTTP requests
    //This bean controls EVERYTHING about HTTP security
    //we always need it
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with default settings (we customize it later)
                .cors(Customizer.withDefaults())

                // Disable CSRF protection, because we are using stateless JWT tokens
                .csrf(AbstractHttpConfigurer::disable)

                // Define which endpoints are public and which require authentication
                .authorizeHttpRequests(auth -> auth
                        // The following endpoints can be accessed without login
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/send-reset-otp",
                                "/reset-password",
                                "/logout")
                        .permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated())

                // Set the session management to stateless, because we are not using HTTP sessions (JWT tokens instead)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable the default logout endpoint provided by Spring Security
                .logout(AbstractHttpConfigurer::disable);

        // Build and return the security filter chain
        return http.build();
    }

    // Bean to encode passwords using BCrypt (secure hashing)
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Bean to handle CORS requests
    @Bean
    public CorsFilter corsFilter(){
        return new CorsFilter(corsConfigurationSource());
    }

    // Define the CORS configuration
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests only from the frontend running at this address
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // Allow these HTTP methods from the frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allow the frontend to send these headers
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Allow cookies and credentials (not needed for JWT but often used)
        config.setAllowCredentials(true);

        // Register this configuration for all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
    //What it does
    //This bean is responsible for:
    //checking if the email exists
    //verifying the password
    //loading the user from DB
    //triggering authentication providers

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    //DaoAuthenticationProvider is responsible for.
    //Loading the user from your UserDetailsService (CustomUserDetailsService)
    //Comparing passwords
    //Checking account status (enabled/locked/expired)
    //If anything fails, DaoAuthenticationProvider throws a subclass of AuthenticationException.
}
