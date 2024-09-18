package com.uscis.dbis.repository;

import com.uscis.dbis.domain.Example;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Neo4j repository for the Example entity.
 */
@Repository
public interface ExampleRepository extends Neo4jRepository<Example, String> {}
