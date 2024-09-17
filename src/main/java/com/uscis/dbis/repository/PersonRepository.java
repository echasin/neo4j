package com.uscis.dbis.repository;

import com.uscis.dbis.domain.Person;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PersonRepository extends ReactiveNeo4jRepository<Person, Long> {
    @Query("MATCH (p:Person)-[:FOLLOWS]->(f:Person) WHERE p.name = $name RETURN f")
    Flux<Person> findFollowersByName(String name);
}
