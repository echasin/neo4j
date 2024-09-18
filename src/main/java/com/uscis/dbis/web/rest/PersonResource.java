package com.uscis.dbis.web.rest;

import com.uscis.dbis.domain.Person;
import com.uscis.dbis.service.PersonService;
import java.util.List;
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

/**
 * REST controller for managing {@link com.uscis.dbis.domain.Person}.
 */
@RestController
@RequestMapping("/api/persons")
public class PersonResource {

    private static final Logger LOG = LoggerFactory.getLogger(PersonResource.class);

    private final PersonService personService;

    public PersonResource(PersonService personService) {
        this.personService = personService;
    }

    /**
     * {@code GET  /persons} : get all the persons.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of persons in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Person>> getAllPersons(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Persons");
        Page<Person> page = personService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /persons/:id} : get the "id" person.
     *
     * @param id the id of the person to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the person, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable("id") String id) {
        LOG.debug("REST request to get Person : {}", id);
        Optional<Person> person = personService.findOne(id);
        return ResponseUtil.wrapOrNotFound(person);
    }
}
