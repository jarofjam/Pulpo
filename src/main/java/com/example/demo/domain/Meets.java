package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RelationshipEntity(type = "MEETS")
@Getter
@Setter
@NoArgsConstructor
public class Meets{
    @Id
    @GeneratedValue
    private Long id;

    private List<Tag> tags = new ArrayList<>();

    @StartNode
    private Tag a;
    @EndNode
    private Tag b;
}
