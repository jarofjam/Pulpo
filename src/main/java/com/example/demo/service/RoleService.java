package com.example.demo.service;

import com.example.demo.domain.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    public String findAll() {
        String roles = "";
        Role[] ar = Role.values();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ar.length; i++) {
            roles = sb.append(ar[i].name()).append("; ").toString();
        }
        return roles;
    }
}
