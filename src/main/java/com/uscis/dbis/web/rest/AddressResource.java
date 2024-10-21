package com.uscis.dbis.web.rest;

import com.uscis.dbis.domain.Address;
import com.uscis.dbis.service.AddressService;
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
@RequestMapping("/api/addresss")
public class AddressResource {

    private static final Logger LOG = LoggerFactory.getLogger(AddressResource.class);

    private final AddressService addressService;

    public AddressResource(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("")
    public ResponseEntity<List<Address>> getAllAddresss(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Addresss");
        Page<Address> page = addressService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddress(@PathVariable("id") String id) {
        LOG.debug("REST request to get Address : {}", id);
        Optional<Address> address = addressService.findOne(id);
        return ResponseUtil.wrapOrNotFound(address);
    }

    @GetMapping("/{id}/graph")
    public ResponseEntity<Map<String, Object>> getAddressGraph(@PathVariable("id") String id) {
        LOG.debug("REST request to get Address graph data : {}", id);
        try {
            Map<String, Object> graphData = addressService.getAddressGraph(id);
            return ResponseEntity.ok().body(graphData);
        } catch (Exception e) {
            LOG.error("Error getting address graph", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /addresss/search : Search for addresss based on dynamic properties.
     *
     * @param properties the filtering properties to search addresss by.
     * @return the ResponseEntity with status 200 (OK) and the list of addresss in the body.
     */
    @PostMapping("/addresss/search")
    public ResponseEntity<List<Address>> searchAddresss(@RequestBody Map<String, Object> properties) {
        LOG.debug("REST request to search Addresss by properties: {}", properties);
        List<Address> result = addressService.findByProperties(properties);
        return ResponseEntity.ok(result);
    }
    // Add other CRUD endpoints (POST, PUT, DELETE) as needed
}
