package org.example.internship.controller;

import lombok.RequiredArgsConstructor;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.entity.UserEntity;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.model.request.GenerateTokenRequest;
import org.example.internship.model.response.JwtResponse;
import org.example.internship.service.TokenService;
import org.example.internship.service.audit.AuditService;
import org.example.internship.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuditService auditService;
    private final UserService userService;

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody GenerateTokenRequest tokenRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        tokenRequest.getUsername(),
                        tokenRequest.getPassword()
                )
        );
        String accessToken  = tokenService.generateAccessToken(auth);
        String refreshToken = tokenService.generateRefreshToken(auth);

        UserEntity user = userService.getByUsername(tokenRequest.getUsername());
        auditService.addRecord(AuditEntityType.USER, AuditActionType.GET_TOKEN, user.getId(), user.getUsername(), user.getUsername());

        return new ResponseEntity<>(new JwtResponse(accessToken, refreshToken), HttpStatus.OK);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        String refreshToken = authHeader.substring(7);

        if (!tokenService.validateToken(refreshToken)) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED, ErrorCode.TKN_401.getCode(), "Invalid token");
        }

        String username = tokenService.getUsernameFromToken(refreshToken);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, List.of());

        String newAccessToken  = tokenService.generateAccessToken(auth);
        String newRefreshToken = tokenService.generateRefreshToken(auth);

        UserEntity user = userService.getByUsername(username);
        auditService.addRecord(AuditEntityType.USER, AuditActionType.REFRESH_TOKEN, user.getId(), user.getUsername(), user.getUsername());

        return new ResponseEntity<>(new JwtResponse(newAccessToken, newRefreshToken), HttpStatus.OK);
    }
}

