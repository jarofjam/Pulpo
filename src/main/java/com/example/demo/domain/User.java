package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime removed;
    private boolean active;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Column(unique = true)
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

    @OneToMany(mappedBy = "templateAuthor")
    private List<Template> templatesAuthor;

    @OneToMany(mappedBy = "typicalRequestAuthor")
    private List<TypicalRequest> typicalRequestsAuthor;

    @OneToMany(mappedBy = "requestPerformer")
    private List<Request> requestsPerformer;

    @OneToMany(mappedBy = "typicalRequestPerformer")
    private List<TypicalRequest> typicalRequestsPerformer;

    @OneToMany(mappedBy = "requestModerator")
    private List<Request> requestsModerator;

    @OneToMany(mappedBy = "typicalRequestModerator")
    private List<TypicalRequest> typicalRequestsModerator;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }
}
