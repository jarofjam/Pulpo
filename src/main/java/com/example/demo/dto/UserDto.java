package com.example.demo.dto;

import com.example.demo.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime removed;

    private String username;
    private String password;

    private String realName;

    private String department;
    private String managerOfDepartment;
    private Boolean remove;
    private List<String> roles;

    public Boolean getRemove() {
        if (this.remove == null) {
            return false;
        }
        return this.remove;
    }
}
