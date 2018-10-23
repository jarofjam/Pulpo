package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
@ToString(of = {"id", "login", "password", "realName", "email", "joined"})
@EqualsAndHashCode(of = {"id"})
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.fullInfo.class)
    private Long id;

    @Column(updatable =false)
    @JsonView(Views.fullInfo.class)
    private LocalDate joined;

    @Column(updatable = false)
    @JsonView(Views.userInfo.class)
    private String login;
    @JsonView(Views.userInfo.class)
    private String password;

    @JsonView(Views.personInfo.class)
    private String email;
    @JsonView(Views.personInfo.class)
    private String realName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public LocalDate getJoined() {
        return joined;
    }

    public void setJoined(LocalDate joined) {
        this.joined = joined;
    }
}
