package in.amalamama.authify.controller;

import in.amalamama.authify.io.ProfileRequest;
import in.amalamama.authify.io.ProfileResponse;
import in.amalamama.authify.service.MailService;
import in.amalamama.authify.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
//@RequestMapping("/api/v1.0") // we got replaced this in the application.properties file (check)
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final MailService mailService;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    //@Valid = “Check that this object is valid according to the annotations before executing the method.”
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){
        //log.info("✅ /register endpoint was called! Request: {}", request);
       // System.out.println(">>> /register endpoint hit!");
      ProfileResponse response=profileService.createProfile(request);
      mailService.sendWelcomeEmail(response.getEmail(),response.getName());
      return response;
    }
    //it's gonna throw an exception
    //==>create global exception handler.
    @GetMapping("/profile")
    public ProfileResponse getProfile(
            //“Inject the email of the currently logged-in user from the Spring Security context.”
            @CurrentSecurityContext(expression="authentication?.name") String email
           // authentication → the current Authentication object stored by Spring Security.
            //@CurrentSecurityContext is a Spring Security annotation that lets you inject information from the current security context directly into a controller method parameter.
            //The security context stores authentication information about the logged-in user, like:
            //Username / email
            //Roles
            //Granted authorities
            //Authentication token details
    ){
       return profileService.getProfile(email);
    }



    @GetMapping("/test")
    public String getString(){
        return "Authify is working fine";
    }
}
