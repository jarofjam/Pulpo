package com.example.demo.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "request_template")
@Getter
@Setter
@NoArgsConstructor
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime removed;

    private String topic;
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_template")
    private User templateAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id_template")
    private Department templateDepartment;

    @OneToMany(mappedBy = "attributeTemplate")
    private List<Attribute> attributes;
}
