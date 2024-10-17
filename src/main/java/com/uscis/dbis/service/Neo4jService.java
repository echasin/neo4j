package com.uscis.dbis.service;

import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Neo4jService implements AutoCloseable {

    private final Driver driver;
    private static final String S3_BASE_URL = "https://neo4j-mock-data.s3.amazonaws.com";

    public Neo4jService(
        @Value("${spring.neo4j.uri}") String uri,
        @Value("${spring.neo4j.authentication.username}") String username,
        @Value("${spring.neo4j.authentication.password}") String password
    ) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    @Override
    public void close() {
        driver.close();
    }

    public String loadCSV(String fileName) {
        try (Session session = driver.session()) {
            String fileUrl = S3_BASE_URL + "/" + fileName;
            String cypher = String.format(
                "LOAD CSV WITH HEADERS FROM '%s' AS row " + "MERGE (p:Person {id: row.id}) " + "SET p.name = row.name",
                fileUrl
            );

            Result result = session.run(cypher);
            return String.format(
                "CSV import successful. Nodes created: %d, Properties set: %d",
                result.consume().counters().nodesCreated(),
                result.consume().counters().propertiesSet()
            );
        } catch (Exception e) {
            return "Error during CSV import: " + e.getMessage();
        }
    }
}
