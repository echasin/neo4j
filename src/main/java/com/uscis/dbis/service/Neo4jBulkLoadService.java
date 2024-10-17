package com.uscis.dbis.service;

import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Neo4jBulkLoadService implements AutoCloseable {

    private final Driver driver;
    private final String s3BaseUrl;

    public Neo4jBulkLoadService(
        @Value("${spring.neo4j.uri}") String uri,
        @Value("${spring.neo4j.authentication.username}") String username,
        @Value("${spring.neo4j.authentication.password}") String password,
        @Value("${spring.csv.s3.base-url}") String s3BaseUrl
    ) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
        this.s3BaseUrl = s3BaseUrl;
    }

    @Override
    public void close() {
        driver.close();
    }

    public String loadCSV(String fileName) {
        try (Session session = driver.session()) {
            String fileUrl = s3BaseUrl + "/" + fileName;
            String cypher = String.format(
                "LOAD CSV WITH HEADERS FROM '%s' AS row " + "MERGE (p:person {id: row.id}) " + "SET p.name = row.name",
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
