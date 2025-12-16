package in.amalamama.authify.controller;

import in.amalamama.authify.io.ProfileRequest;
import in.amalamama.authify.io.ProfileResponse;
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

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){
        log.info("✅ /register endpoint was called! Request: {}", request);
        System.out.println(">>> /register endpoint hit!");
      ProfileResponse response=profileService.createProfile(request);
      return response;
    }
//it's gonna throw an exception
    //==>create global exception handler.
    @GetMapping("/profile")
    public ProfileResponse getProfile(
            //Automatically extracts the logged-in user’s email
            @CurrentSecurityContext(expression="authentication?.name") String email
    ){
       return profileService.getProfile(email);
    }




    @GetMapping("/test")
    public String getString(){
        return "Authify is working fine";
    }
}
