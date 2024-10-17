package com.uscis.dbis.web.rest;

import com.uscis.dbis.service.Neo4jService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CSVLoaderResource {

    private final Neo4jService neo4jService;

    public CSVLoaderResource(Neo4jService neo4jService) {
        this.neo4jService = neo4jService;
    }

    @PostMapping("/load-csv")
    public ResponseEntity<String> loadCSV(@RequestParam String fileName) {
        try {
            String result = neo4jService.loadCSV(fileName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading CSV: " + e.getMessage());
        }
    }
}
