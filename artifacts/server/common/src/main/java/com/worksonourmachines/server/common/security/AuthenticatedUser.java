package com.worksonourmachines.server.common.security;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthenticatedUser {

    public UUID id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)
                || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authenticated JWT principal is required.");
        }

        String subject = jwtAuthenticationToken.getToken().getSubject();
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            throw new AccessDeniedException("JWT subject must be a UUID.", exception);
        }
    }

    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authenticated JWT principal is required.");
        }

        String username = jwtAuthenticationToken.getToken().getClaimAsString("name");
        if (username.isBlank()) {
            throw new AccessDeniedException("Username cannot be blank.");
        }
        return username;
    }
}
