package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime removed;
    private boolean active;

    //key parameter
    //authorization does not work without second parameter
    //and here comes this little buddy
    private Integer a;

    private String username;
    private String password;

    private String realName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID_USER")
    private Department userDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID_MANAGER")
    private Department managerOfDepartment;

    @OneToMany(mappedBy = "requestAuthor")
    private List<Request> requestsAuthor;

    @OneToMany(mappedBy = "requestPerformer")
    private List<Request> requestsPerformer;
}
