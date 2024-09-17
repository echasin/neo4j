package com.uscis.dbis.repository;

import com.uscis.dbis.domain.Authority;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Neo4j repository for the Authority entity.
 */
@Repository
public interface AuthorityRepository extends Neo4jRepository<Authority, String> {}
