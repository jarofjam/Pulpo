package com.example.demo.domain;

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
    private LocalDateTime finished;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime deadline;

    private String topic;
    private String description;
    private String comment;
    private String cancelInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    private Department requestDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID_AUTHOR")
    private User requestAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID_PERFORMER")
    private User requestPerformer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID_MODERATOR")
    private User requestModerator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    private Status requestStatus;
}
