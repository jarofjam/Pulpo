package com.example.demo.repository;

import com.example.demo.domain.Tag;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends Neo4jRepository<Tag, Long> {
    @Query(
            "MATCH (parent:Tag {name: {parent_name}}) " +
            "MERGE (child:Tag {name: {child_name}, parent: {parent_name}, tier: parent.tier + 1}) " +
            "MERGE (child)<-[:PARENT_OF]-(parent) " +
            "RETURN child"
    )
    Tag createTag(@Param("child_name") String name, @Param("parent_name") String parent);

    @Query(
            "MATCH (a {name: {name_1}}), (b {name: {name_2}}) " +
                "CALL apoc.do.when(" +
                    "EXISTS( (a)-[:MEETS]-(b) ), " +
                    "'MATCH (a)-[r:MEETS]-(b) SET r.times=r.times+1 RETURN r.times AS times', " +
                    "'CREATE (a)-[:MEETS {times: 1}]->(b) RETURN 1 AS times', " +
                    "{a:a, b:b}" +
                ") YIELD value " +
            "RETURN value.times"
    )
    Integer createRib(@Param("name_1") String name_1, @Param("name_2") String name_2);

    @Query(
            "MATCH (a {name: {name_1}})-[r:MEETS]-(b {name: {name_2}}) " +
            "CALL apoc.do.when(" +
                "a.tier > b.tier, " +
                "'MERGE (a)<-[:GROUPED]-(b)', " +
                "'MERGE (a)-[:GROUPED]->(b)', " +
                "{a:a, b:b}" +
            ") YIELD value " +
            "RETURN NULL"
    )
    void unite(@Param("name_1") String name_1, @Param("name_2") String name_2);

    @Query(
            "MATCH (a {name: {name}})-[:GROUPED]-(b) " +
                "RETURN b.name " +
            "UNION " +
            "MATCH (a {name: {name}})<-[:PARENT_OF*]-(b) " +
                "WHERE (a)<-[:GROUPED*]-(b) " +
                "RETURN b.name " +
            "UNION " +
            "MATCH (a {name: {name}})-[:PARENT_OF*]->(b) " +
                "WHERE (a)-[:GROUPED*]->(b) " +
                "RETURN b.name"
    )
    List<String> findFriends(@Param("name") String name);

}