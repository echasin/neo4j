package com.uscis.dbis.web.rest;

import com.uscis.dbis.domain.Person;
import com.uscis.dbis.service.PersonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class PersonResource {

    private final PersonService personService;

    public PersonResource(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/followers/{name}")
    public Flux<Person> getFollowers(@PathVariable String name) {
        return personService.findFollowers(name);
    }
}
