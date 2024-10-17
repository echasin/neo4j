package com.uscis.dbis.web.rest;

import com.uscis.dbis.service.Neo4jBulkLoadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CSVLoaderResource {

    private final Neo4jBulkLoadService neo4jBulkLoadService;

    public CSVLoaderResource(Neo4jBulkLoadService neo4jBulkLoadService) {
        this.neo4jBulkLoadService = neo4jBulkLoadService;
    }

    @PostMapping("/load-csv")
    public ResponseEntity<String> loadCSV(@RequestParam String fileName) {
        try {
            String result = neo4jBulkLoadService.loadCSV(fileName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error loading CSV: " + e.getMessage() + "\nStack trace: " + getStackTrace(e));
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
