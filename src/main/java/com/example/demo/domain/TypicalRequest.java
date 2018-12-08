package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests_from_template")
@Getter
@Setter
@NoArgsConstructor
public class TypicalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime removed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finished;

    private String comment;
    private String cancelInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id_request")
    private Template requestTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_author")
    private User typicalRequestAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_performer")
    private User typicalRequestPerformer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_moderator")
    private User typicalRequestModerator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status typicalRequestStatus;

    @OneToMany(mappedBy = "valueTypicalRequest")
    private List<Value> values;
}
