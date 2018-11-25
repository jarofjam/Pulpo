package com.example.demo.domain;

import lombok.*;
import org.neo4j.ogm.annotation.*;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
@NoArgsConstructor
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = "MEETS")
    private List<Tag> tagsFromMe = new ArrayList<>();

    @Relationship(type = "MEETS", direction = Relationship.INCOMING)
    private List<Tag> tagsToMe = new ArrayList<>();

}
