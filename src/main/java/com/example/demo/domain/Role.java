package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;

public enum Role implements GrantedAuthority {
    ADMIN, MODERATOR, PERFORMER, USER;

    @Override
    public String getAuthority() {
        return name();
    }
}


//@Entity
//@Table(name = "role")
//@Data
//@NoArgsConstructor
//public class Role {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//    @Column(updatable =false)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime created;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime removed;
//
//    private String name;
//    private String description;
//}