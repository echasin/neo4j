package com.uscis.dbis.service;

import com.uscis.dbis.domain.Example;
import com.uscis.dbis.repository.ExampleRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.uscis.dbis.domain.Example}.
 */
@Service
public class ExampleService {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleService.class);

    private final ExampleRepository exampleRepository;

    public ExampleService(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    /**
     * Save a example.
     *
     * @param example the entity to save.
     * @return the persisted entity.
     */
    public Example save(Example example) {
        LOG.debug("Request to save Example : {}", example);
        return exampleRepository.save(example);
    }

    /**
     * Update a example.
     *
     * @param example the entity to save.
     * @return the persisted entity.
     */
    public Example update(Example example) {
        LOG.debug("Request to update Example : {}", example);
        return exampleRepository.save(example);
    }

    /**
     * Partially update a example.
     *
     * @param example the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Example> partialUpdate(Example example) {
        LOG.debug("Request to partially update Example : {}", example);

        return exampleRepository
            .findById(example.getId())
            .map(existingExample -> {
                if (example.getName() != null) {
                    existingExample.setName(example.getName());
                }

                return existingExample;
            })
            .map(exampleRepository::save);
    }

    /**
     * Get all the examples.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Example> findAll(Pageable pageable) {
        LOG.debug("Request to get all Examples");
        return exampleRepository.findAll(pageable);
    }

    /**
     * Get one example by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Example> findOne(String id) {
        LOG.debug("Request to get Example : {}", id);
        return exampleRepository.findById(id);
    }

    /**
     * Delete the example by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Example : {}", id);
        exampleRepository.deleteById(id);
    }
}
