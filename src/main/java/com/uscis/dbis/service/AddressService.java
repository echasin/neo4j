// package com.uscis.dbis.service;

// public class AddressService {

// }

package com.uscis.dbis.service;

import com.uscis.dbis.domain.Address;
import com.uscis.dbis.repository.AddressRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List; // Correct import for List
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private static final Logger LOG = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;
    private final Neo4jClient neo4jClient;

    public AddressService(AddressRepository addressRepository, Neo4jClient neo4jClient) {
        this.addressRepository = addressRepository;
        this.neo4jClient = neo4jClient;
    }

    public Address save(Address address) {
        LOG.debug("Request to save Address : {}", address);
        return addressRepository.save(address);
    }

    public Address update(Address address) {
        LOG.debug("Request to update Address : {}", address);
        return addressRepository.save(address);
    }

    public Optional<Address> partialUpdate(Address address) {
        LOG.debug("Request to partially update Address : {}", address);

        return addressRepository
            .findById(address.getId())
            .map(existingAddress -> {
                if (address.getAddressfull() != null) {
                    existingAddress.setAddressfull(address.getAddressfull());
                }
                return existingAddress;
            })
            .map(addressRepository::save);
    }

    public Page<Address> findAll(Pageable pageable) {
        LOG.debug("Request to get all Addresss");
        return addressRepository.findAll(pageable);
    }

    public Optional<Address> findOne(String id) {
        LOG.debug("Request to get Address : {}", id);
        return addressRepository.findById(id);
    }

    public void delete(String id) {
        LOG.debug("Request to delete Address : {}", id);
        addressRepository.deleteById(id);
    }

    public Map<String, Object> getAddressGraph(String id) {
        LOG.debug("Request to get Address graph for id : {}", id);

        String query =
            """
                WITH $startNodeId AS startNodeId
                MATCH path = (start:address)-[*0..2]-(connected)
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

    public List<Address> findByProperties(Map<String, Object> properties) {
        LOG.debug("Request to find Address by properties: {}", properties);

        if (properties.isEmpty()) {
            return addressRepository.findAll();
        }

        // Build the query
        String filters = properties
            .entrySet()
            .stream()
            .map(entry -> "p." + entry.getKey() + " = $" + entry.getKey())
            .collect(Collectors.joining(" AND "));

        String query = "MATCH (p:address) WHERE " + filters + " RETURN p";

        try {
            Collection<Address> result = neo4jClient
                .query(query)
                .bindAll(properties)
                .fetchAs(Address.class)
                .mappedBy((typeSystem, record) -> {
                    Node node = record.get("p").asNode();
                    Address address = new Address();
                    //address.setId(node.elementId()); // Use elementId() instead of id()
                    address.setId(node.get("id").asString(null));
                    address.setAddressfull(node.get("name").asString(null));
                    // Map other properties as needed
                    return address;
                })
                .all();

            LOG.debug("Query result: {}", result);
            return new ArrayList<>(result);
        } catch (Exception e) {
            LOG.error("Error executing query", e);
            throw e;
        }
    }
}
