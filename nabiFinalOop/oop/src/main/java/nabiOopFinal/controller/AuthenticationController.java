package nabiOopFinal.controller;

import nabiOopFinal.dto.*;


import nabiOopFinal.repository.UserRepository;
import nabiOopFinal.security.JwtService;
import nabiOopFinal.service.AuthService;
import lombok.RequiredArgsConstructor;
import nabiOopFinal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthService service;
  private final JwtService jwtService;
  private final UserRepository repository;

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
    try {
      // Check if the email already exists in the database
      if (userService.existsByEmail(request.getEmail())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Email already exists. Please choose a different email.", "DUPLICATE_EMAIL", null));
      }

      RegisterResponse registerResponse = service.register(request);
      ApiResponse<RegisterResponse> apiResponse = new ApiResponse<>("User registered successfully", "REGISTERED", registerResponse);
      return ResponseEntity.ok().body(apiResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new ApiResponse<>("Failed to register user. " + e.getMessage(), "ERROR", null));
    }
  }


  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody AuthRequest request) {
    try {
      AuthResponse authResponse = service.authenticate(request);
      ApiResponse<AuthResponse> apiResponse = new ApiResponse<>("User authenticated successfully", "AUTHENTICATED", authResponse);
      return ResponseEntity.ok().body(apiResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(new ApiResponse<>("Authentication failed. " + e.getMessage(), "UNAUTHORIZED", null));
    }
  }

}
