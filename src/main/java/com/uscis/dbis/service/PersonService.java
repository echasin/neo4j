package com.uscis.dbis.service;

import com.uscis.dbis.domain.Person;
import com.uscis.dbis.repository.PersonRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Flux<Person> findFollowers(String name) {
        return personRepository.findFollowersByName(name);
    }
}
