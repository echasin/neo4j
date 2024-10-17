package com.uscis.dbis.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class Neo4jService implements AutoCloseable {

    private final Driver driver;
    private final String basePath;

    public Neo4jService(
        @Value("${spring.neo4j.uri}") String uri,
        @Value("${spring.neo4j.authentication.username}") String username,
        @Value("${spring.neo4j.authentication.password}") String password,
        @Value("${spring.csv.base.path}") String basePath
    ) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
        this.basePath = basePath;
    }

    @Override
    public void close() {
        driver.close();
    }

    // Method to load the CSV data into Neo4j using LOAD CSV with a relative path
    public String loadCSV(String fileName) {
        try (Session session = driver.session()) {
            String result = session.executeWrite(tx -> {
                String absoluteFilePath = getAbsoluteFilePath(fileName);
                String cypherQuery =
                    "LOAD CSV WITH HEADERS FROM 'file:///" +
                    absoluteFilePath.replace("\\", "/") +
                    "' AS row " +
                    "CREATE (p:Person {name: row.name, age: toInteger(row.age), email: row.email})";

                Result queryResult = tx.run(cypherQuery);
                return "CSV data loaded successfully. Affected rows: " + queryResult.consume().counters().nodesCreated();
            });
            return result;
        } catch (Neo4jException e) {
            e.printStackTrace();
            return "Failed to load CSV data: " + e.getMessage();
        }
    }

    private String getAbsoluteFilePath(String fileName) {
        try {
            Path relativePath = Paths.get(basePath, fileName);
            Resource resource = new ClassPathResource(relativePath.toString());
            return resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to locate the CSV file: " + fileName + " in base path: " + basePath, e);
        }
    }
}
