package com.uscis.dbis.web.rest;

import com.uscis.dbis.domain.Person;
import com.uscis.dbis.service.PersonService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/persons")
public class PersonResource {

    private static final Logger LOG = LoggerFactory.getLogger(PersonResource.class);

    private final PersonService personService;

    public PersonResource(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("")
    public ResponseEntity<List<Person>> getAllPersons(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Persons");
        Page<Person> page = personService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable("id") String id) {
        LOG.debug("REST request to get Person : {}", id);
        Optional<Person> person = personService.findOne(id);
        return ResponseUtil.wrapOrNotFound(person);
    }

    @GetMapping("/{id}/graph")
    public ResponseEntity<Map<String, Object>> getPersonGraph(@PathVariable("id") String id) {
        LOG.debug("REST request to get Person graph data : {}", id);
        try {
            Map<String, Object> graphData = personService.getPersonGraph(id);
            return ResponseEntity.ok().body(graphData);
        } catch (Exception e) {
            LOG.error("Error getting person graph", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /persons/search : Search for persons based on dynamic properties.
     *
     * @param properties the filtering properties to search persons by.
     * @return the ResponseEntity with status 200 (OK) and the list of persons in the body.
     */
    @PostMapping("/persons/search")
    public ResponseEntity<List<Person>> searchPersons(@RequestBody Map<String, Object> properties) {
        LOG.debug("REST request to search Persons by properties: {}", properties);
        List<Person> result = personService.findByProperties(properties);
        return ResponseEntity.ok(result);
    }
    // Add other CRUD endpoints (POST, PUT, DELETE) as needed
}
