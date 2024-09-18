package com.uscis.dbis.service;

import com.uscis.dbis.domain.Person;
import com.uscis.dbis.repository.PersonRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.uscis.dbis.domain.Person}.
 */
@Service
public class PersonService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Save a person.
     *
     * @param person the entity to save.
     * @return the persisted entity.
     */
    public Person save(Person person) {
        LOG.debug("Request to save Person : {}", person);
        return personRepository.save(person);
    }

    /**
     * Update a person.
     *
     * @param person the entity to save.
     * @return the persisted entity.
     */
    public Person update(Person person) {
        LOG.debug("Request to update Person : {}", person);
        return personRepository.save(person);
    }

    /**
     * Partially update a person.
     *
     * @param person the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Person> partialUpdate(Person person) {
        LOG.debug("Request to partially update Person : {}", person);

        return personRepository
            .findById(person.getId())
            .map(existingPerson -> {
                if (person.getName() != null) {
                    existingPerson.setName(person.getName());
                }

                return existingPerson;
            })
            .map(personRepository::save);
    }

    /**
     * Get all the persons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Person> findAll(Pageable pageable) {
        LOG.debug("Request to get all Persons");
        return personRepository.findAll(pageable);
    }

    /**
     * Get one person by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Person> findOne(String id) {
        LOG.debug("Request to get Person : {}", id);
        return personRepository.findById(id);
    }

    /**
     * Delete the person by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Person : {}", id);
        personRepository.deleteById(id);
    }
}
