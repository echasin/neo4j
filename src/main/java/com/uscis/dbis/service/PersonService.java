package com.uscis.dbis.service;

import com.uscis.dbis.domain.Person;
import com.uscis.dbis.repository.PersonRepository;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;
    private final Neo4jClient neo4jClient;

    public PersonService(PersonRepository personRepository, Neo4jClient neo4jClient) {
        this.personRepository = personRepository;
        this.neo4jClient = neo4jClient;
    }

    public Person save(Person person) {
        LOG.debug("Request to save Person : {}", person);
        return personRepository.save(person);
    }

    public Person update(Person person) {
        LOG.debug("Request to update Person : {}", person);
        return personRepository.save(person);
    }

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

    public Page<Person> findAll(Pageable pageable) {
        LOG.debug("Request to get all Persons");
        return personRepository.findAll(pageable);
    }

    public Optional<Person> findOne(String id) {
        LOG.debug("Request to get Person : {}", id);
        return personRepository.findById(id);
    }

    public void delete(String id) {
        LOG.debug("Request to delete Person : {}", id);
        personRepository.deleteById(id);
    }

    public Map<String, Object> getPersonGraph(String id) {
        LOG.debug("Request to get Person graph for id : {}", id);

        String query =
            """
                WITH $startNodeId AS startNodeId
                MATCH path = (start:person)-[*0..2]-(connected)
                WHERE start.id = startNodeId AND (start <> connected OR start = connected)
                WITH
                    COLLECT(DISTINCT start) + COLLECT(DISTINCT connected) AS allNodes,
                    COLLECT(DISTINCT relationships(path)) AS allRels
                UNWIND allNodes AS node
                UNWIND allRels AS rels
                UNWIND rels AS rel
                WITH
                    COLLECT(DISTINCT {
                        id: node.id,
                        label: labels(node)[0],
                        name: COALESCE(node.name, 'N/A'),
                        group: labels(node)[0]
                    }) AS nodes,
                    COLLECT(DISTINCT {
                        source: startNode(rel).id,
                        target: endNode(rel).id,
                        type: type(rel)
                    }) AS links
                RETURN {nodes: nodes, links: links} AS graph
            """;

        try {
            Map<String, Object> result = neo4jClient
                .query(query)
                .bindAll(Map.of("startNodeId", id))
                .fetchAs(Map.class)
                .one()
                .orElseThrow(() -> new RuntimeException("Graph not found for id: " + id));

            LOG.debug("Query result: {}", result);
            return result;
        } catch (Exception e) {
            LOG.error("Error executing graph query", e);
            throw e;
        }
    }
}
