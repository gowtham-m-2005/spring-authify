package com.frostyfox.authify.service;

import com.frostyfox.authify.io.ProfileRequest;
import com.frostyfox.authify.io.ProfileResponse;

public interface ProfileService {

   ProfileResponse createProfile(ProfileRequest request);
   ProfileResponse getProfile(String email);

   void sendResetOtp(String email);

   void resetPassword(String email, String otp, String newPassword);

   void sendOtp(String email);

   void verifyOtp(String email, String otp);

   String getLoggedInUserId(String email);
}
