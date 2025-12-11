package in.amalamama.authify.service;

import in.amalamama.authify.entity.UserEntity;
import in.amalamama.authify.io.ProfileRequest;
import in.amalamama.authify.io.ProfileResponse;
import in.amalamama.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{
    private final UserRepository userRepository;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile=convertToEntity(request);
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User already exists with email " + newProfile.getEmail());
        }
        newProfile= userRepository.save(newProfile);
        return convertToProfileResponse(newProfile);
    }

    private UserEntity convertToEntity(ProfileRequest request){
       return UserEntity.builder()
               .name(request.getName())
               .password(request.getPassword())
               .email(request.getEmail())
               .userId(UUID.randomUUID().toString())
               .isAccountVerified(false)
               .resetOtp(null)
               .resetOtpExpireAt(0L)
               .verifyOtp(null)
               .verifyOtpExpireAt(0L)
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

