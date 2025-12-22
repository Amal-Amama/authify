package in.amalamama.authify.service;

import in.amalamama.authify.io.ProfileRequest;
import in.amalamama.authify.io.ProfileResponse;

public interface ProfileService {
     ProfileResponse createProfile(ProfileRequest request);
     ProfileResponse getProfile(String email);
     void sendResetOtp(String email);
     void resetPassword(String email,String otp,String newPassword);

}
