package com.example.demo.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, MODERATOR, PERFORMER, APPLICANT;

    @Override
    public String getAuthority() {
        return name();
    }
}