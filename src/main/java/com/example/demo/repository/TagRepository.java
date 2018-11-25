package com.example.demo.repository;

import com.example.demo.domain.Tag;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface TagRepository extends Neo4jRepository<Tag, Long> {
    @Query(
            "MERGE (tag:Tag {name: {name}}) " +
            "RETURN tag"
    )
    Tag createTag(@Param("name") String name);

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
            "MATCH (tag {name: {name}}) " +
                    "RETURN tag"
    )
    List<Tag> findAllByName(@Param("name") String name);

    @Query(
            "MATCH (tag {name: {oldName}}) " +
            "SET tag.name = {newName} " +
            "RETURN tag"
    )
    Tag updateTagName(@Param("oldName") String oldName, @Param("newName") String newName);

    @Query(
            "MATCH ({name: {name}})-[r:MEETS]-() " +
            "DELETE r"
    )
    void deleteRibsByTag(@Param("name") String name);

    @Query(
            "MATCH (tag {name: {name}}) " +
            "DELETE tag"
    )
    void deleteTagByName(@Param("name") String name);

    @Query(
            "MATCH (a {name: {name1}})-[r]-(b {name: {name2}}) " +
            "DELETE r"
    )
    void deleteRibsBetweenTags(@Param("name1") String name1, @Param("name2") String name2);


    @Query(
            "MATCH (a {name: {name}})-[:MEETS]-(b) " +
            "RETURN b.name"
    )
    List<String> findCloseFriends(@Param("name") String name);

    @Query(
            "MATCH p = (a {name: {name}})-[:MEETS]-(b) " +
            "WHERE LENGHT(p) = 3 " +
            "RETURN a.name + '$' + b.name"
    )
    List<String> findRibsByDistanceFromTag(@Param("name") String name, @Param("pupa") String range);
//
//    @Query(
//            "MATCH (a {name: {name_1}})-[r:MEETS]-(b {name: {name_2}}) " +
//            "CALL apoc.do.when(" +
//                "a.tier > b.tier, " +
//                "'MERGE (a)<-[:GROUPED]-(b)', " +
//                "'MERGE (a)-[:GROUPED]->(b)', " +
//                "{a:a, b:b}" +
//            ") YIELD value " +
//            "RETURN NULL"
//    )
//    void unite(@Param("name_1") String name_1, @Param("name_2") String name_2);

//    @Query(
//            "MATCH (a {name: {name}})-[:GROUPED]-(b) " +
//                "RETURN b.name " +
//            "UNION " +
//            "MATCH (a {name: {name}})<-[:PARENT_OF*]-(b) " +
//                "WHERE (a)<-[:GROUPED*]-(b) " +
//                "RETURN b.name " +
//            "UNION " +
//            "MATCH (a {name: {name}})-[:PARENT_OF*]->(b) " +
//                "WHERE (a)-[:GROUPED*]->(b) " +
//                "RETURN b.name"
//    )
//    List<String> findFriends(@Param("name") String name);

}