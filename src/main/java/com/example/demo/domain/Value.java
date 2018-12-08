package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "values")
@Getter
@Setter
@NoArgsConstructor
public class Value {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typical_request_id_value")
    private TypicalRequest valueTypicalRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id_value")
    private Attribute valueAttribute;
}
