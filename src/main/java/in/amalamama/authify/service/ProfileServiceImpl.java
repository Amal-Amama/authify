package in.amalamama.authify.service;

import in.amalamama.authify.entity.UserEntity;
import in.amalamama.authify.io.ProfileRequest;
import in.amalamama.authify.io.ProfileResponse;
import in.amalamama.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile=convertToEntity(request);
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User already exists with email " + newProfile.getEmail());
        }
        newProfile= userRepository.save(newProfile);
        return convertToProfileResponse(newProfile);
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException("User not found: "+email));
        return convertToProfileResponse(user);
    }

    @Override
    public void sendResetOtp(String email) {
       UserEntity existingUser= userRepository.findByEmail(email)
               .orElseThrow(()->new UsernameNotFoundException("User not found: "+email));
       //Generate 6 digitOtp
       String otp= String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
       //calculate expiry time (current time+24 hours in milliseconds
       long expiryTime= System.currentTimeMillis()+(15*60*1000);
       //update the profile/user
        existingUser.setResetOtp(otp);
        existingUser.setResetOtpExpireAt(expiryTime);
        //save into db
        userRepository.save(existingUser);
        try {
            //TODO: send the reset otp email
            mailService.sendResetOtpEmail(existingUser.getEmail(),existingUser.getResetOtp());
        }catch(Exception ex){
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingUser=userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found "+email));
        if (existingUser.getResetOtp()==null || !otp.equals(existingUser.getResetOtp())){
            throw new RuntimeException("Invalid OTP");
        }
        if(existingUser.getResetOtpExpireAt()<System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepository.save(existingUser);
    }


    private UserEntity convertToEntity(ProfileRequest request){
       return UserEntity.builder()
               .name(request.getName())
               .password(passwordEncoder.encode(request.getPassword()))
               .email(request.getEmail())
               .userId(UUID.randomUUID().toString())
               .isAccountVerified(false)
               .resetOtp(null)
               .resetOtpExpireAt(0L)
               .verifyOtp(null)
               .verifyOtpExpireAt(0L) //an initialization
               .build();
    }
    private ProfileResponse convertToProfileResponse(UserEntity newProfile){
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .userId(newProfile.getUserId())
                .email(newProfile.getEmail())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }
}

