package com.example.demo.dto;

import com.example.demo.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TemplateDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime removed;

    private String topic;
    private String text;
    private Boolean remove;

    private List<Map<String, String>> attributes;
    private String author;
    private String department;

    public Boolean getRemove() {
        if (this.remove == null) {
            return false;
        }
        return this.remove;
    }
}
