package com.uscis.dbis.domain;

import java.util.List;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = "FOLLOWS")
    private List<Person> follows;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
