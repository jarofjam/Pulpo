package com.example.demo.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.persistence.GenerationType;

@NodeEntity
@NoArgsConstructor
@Data
public class Tag {
    @GraphId
    private Long id;

    private String name;
    private String parent;
    private Integer tier;
}
