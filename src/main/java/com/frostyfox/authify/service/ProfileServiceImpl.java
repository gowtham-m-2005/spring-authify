package com.frostyfox.authify.service;

import com.frostyfox.authify.dao.UserDao;
import com.frostyfox.authify.io.ProfileRequest;
import com.frostyfox.authify.io.ProfileResponse;
import com.frostyfox.authify.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSenderImpl mailSender;
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request){
        UserEntity newProfile = convertToUserEntity(request);
        if(!userDao.existsByEmail(request.getEmail())){
            newProfile = userDao.save(newProfile);
            return convertToProfileResponse(newProfile);
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    @Override
    public ProfileResponse getProfile(String email){
        UserEntity existingUser = userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found : "+ email));
        return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity userEntity = userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));

        //Generate otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(1000000,10000000));

        //calculate expiry time(current time + 15 mins in ms)
        long expiryTime = System.currentTimeMillis() + (15 *60 * 1000);

        //update the profile/user
        userEntity.setResetOtp(otp);
        userEntity.setResetOtpExpireAt(expiryTime);

        //save into db
        userDao.save(userEntity);

        try{
            emailService.sendResetOtpEmail(userEntity.getEmail(), otp);
        } catch (Exception e){
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity userEntity = userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));

        if(userEntity.getResetOtp() == null || !userEntity.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(userEntity.getResetOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userEntity.setResetOtp(null);
        userEntity.setResetOtpExpireAt(null);

        userDao.save(userEntity);
    }

    @Override
    public void sendOtp(String email) {
        UserEntity userEntity = userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));

        if(userEntity.getIsAccountVerified() != null && userEntity.getIsAccountVerified()){
            return;
        }

        //generate otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(1000000,10000000));
        long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        userEntity.setVerifyOtp(otp);
        userEntity.setVerifyOtpExpireAt(expiryTime);

        userDao.save(userEntity);

        try {
            emailService.sendOtpEmail(userEntity.getEmail(), otp);
        }catch (Exception e){
            throw  new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity userEntity = userDao.findByEmail(email)
                .orElseThrow(() -> new  UsernameNotFoundException("User not found : " + email));

        if(userEntity.getVerifyOtp() == null || !userEntity.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(userEntity.getVerifyOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        userEntity.setIsAccountVerified(true);
        userEntity.setVerifyOtp(null);
        userEntity.setVerifyOtpExpireAt(0L);

        userDao.save(userEntity);
    }

    @Override
    public String getLoggedInUserId(String email) {
        UserEntity userEntity =userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));
        return userEntity.getUserId();
    }

    private UserEntity convertToUserEntity(ProfileRequest request){
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile){
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }
}
