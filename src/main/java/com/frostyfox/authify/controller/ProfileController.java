package com.frostyfox.authify.controller;

import com.frostyfox.authify.io.ProfileRequest;
import com.frostyfox.authify.io.ProfileResponse;
import com.frostyfox.authify.service.EmailService;
import com.frostyfox.authify.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final EmailService emailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){
        ProfileResponse response = profileService.createProfile(request);
        emailService.sendWelcomeMail(response.getEmail(), response.getName());
        return response;
    }

    @GetMapping("/test")
    public String test(){
        return "Auth is working!";
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return profileService.getProfile(email);
    }
}
