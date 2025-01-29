package rs.raf.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.raf.backend.dto.UserDTO;
import rs.raf.backend.model.User;
import rs.raf.backend.requests.LoginRequest;
import rs.raf.backend.responses.LoginResponse;
import rs.raf.backend.services.UserService;
import rs.raf.backend.utils.JwtUtil;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
//import rs.edu.raf.spring_project.model.AuthReq;
//import rs.edu.raf.spring_project.model.AuthRes;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                String token = jwtUtil.generateToken(loginRequest.getEmail());
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        try {
            // Dobijamo authentication objekat iz security context-a
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                // getName() vraća email/username iz authentication objekta
                String email = authentication.getName();

                User user = userService.findByEmail(email);

                // Možemo napraviti DTO da ne šaljemo osetljive informacije
                UserDTO userDTO = new UserDTO(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                null,  // ne šaljemo password
                        user.getPermissions()
                );

                return ResponseEntity.ok(userDTO);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/permissions/{userId}")
    public ResponseEntity<Set<String>> getUserPermissions(@PathVariable Long userId) {
        try {
            Set<String> permissions = userService.getUserPermissions(userId);
            return ResponseEntity.ok(permissions);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
