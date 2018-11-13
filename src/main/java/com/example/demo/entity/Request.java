package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_request")
@Data
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime removed;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

//    @ElementCollection(targetClass = Status.class, fetch = FetchType.EAGER)
    private String status;

    private Boolean isSecure;

    private String topic;
    private String description;
    private String comment;
    @Column(updatable = false)
    private String client;
    private String department;
    private String performer;
    private String cancelInfo;
}
