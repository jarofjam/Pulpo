package com.example.demo.domain;

import com.example.demo.view.UserViews;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appUser")
@Getter
@Setter
@ToString(of = {"id", "created", "removed", "login", "password", "realName", "role", "department"})
@EqualsAndHashCode(of = {"id"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(UserViews.fullInfo.class)
    private Long id;
    @Column(updatable =false)
    @JsonView(UserViews.fullInfo.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @Column(updatable =false)
    @JsonView(UserViews.fullInfo.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime removed;
    @JsonView(UserViews.personInfo.class)
    private Integer role;

    @JsonView(UserViews.userInfo.class)
    private String login;
    @JsonView(UserViews.userInfo.class)
    private String password;

    @JsonView(UserViews.personInfo.class)
    private String realName;
    @JsonView(UserViews.personInfo.class)
    private String department;
}
