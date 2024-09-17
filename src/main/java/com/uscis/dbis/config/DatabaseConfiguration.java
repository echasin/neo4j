package com.uscis.dbis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration
@EnableNeo4jRepositories("com.uscis.dbis.repository")
public class DatabaseConfiguration {}
