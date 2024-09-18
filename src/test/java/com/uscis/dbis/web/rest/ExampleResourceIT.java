package com.uscis.dbis.web.rest;

import static com.uscis.dbis.domain.ExampleAsserts.*;
import static com.uscis.dbis.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uscis.dbis.IntegrationTest;
import com.uscis.dbis.domain.Example;
import com.uscis.dbis.repository.ExampleRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link ExampleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExampleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/examples";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExampleRepository exampleRepository;

    @Autowired
    private MockMvc restExampleMockMvc;

    private Example example;

    private Example insertedExample;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Example createEntity() {
        return new Example().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Example createUpdatedEntity() {
        return new Example().name(UPDATED_NAME);
    }

    @BeforeEach
    public void initTest() {
        example = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedExample != null) {
            exampleRepository.delete(insertedExample);
            insertedExample = null;
        }
    }

    @Test
    void createExample() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Example
        var returnedExample = om.readValue(
            restExampleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(example)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Example.class
        );

        // Validate the Example in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertExampleUpdatableFieldsEquals(returnedExample, getPersistedExample(returnedExample));

        insertedExample = returnedExample;
    }

    @Test
    void createExampleWithExistingId() throws Exception {
        // Create the Example with an existing ID
        example.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExampleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(example)))
            .andExpect(status().isBadRequest());

        // Validate the Example in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        example.setName(null);

        // Create the Example, which fails.

        restExampleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(example)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllExamples() throws Exception {
        // Initialize the database
        insertedExample = exampleRepository.save(example);

        // Get all the exampleList
        restExampleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    void getExample() throws Exception {
        // Initialize the database
        insertedExample = exampleRepository.save(example);

        // Get the example
        restExampleMockMvc
            .perform(get(ENTITY_API_URL_ID, example.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    void getNonExistingExample() throws Exception {
        // Get the example
        restExampleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingExample() throws Exception {
        // Initialize the database
        insertedExample = exampleRepository.save(example);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the example
        Example updatedExample = exampleRepository.findById(example.getId()).orElseThrow();
        updatedExample.name(UPDATED_NAME);

        restExampleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExample.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedExample))
            )
            .andExpect(status().isOk());

        // Validate the Example in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExampleToMatchAllProperties(updatedExample);
    }

    @Test
    void putNonExistingExample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        example.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExampleMockMvc
            .perform(put(ENTITY_API_URL_ID, example.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(example)))
            .andExpect(status().isBadRequest());

        // Validate the Example in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchExample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        example.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExampleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(example))
            )
            .andExpect(status().isBadRequest());

        // Validate the Example in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamExample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        example.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExampleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(example)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Example in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateExampleWithPatch() throws Exception {
        // Initialize the database
        insertedExample = exampleRepository.save(example);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the example using partial update
        Example partialUpdatedExample = new Example();
        partialUpdatedExample.setId(example.getId());

        partialUpdatedExample.name(UPDATED_NAME);

        restExampleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExample.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExample))
            )
            .andExpect(status().isOk());

        // Validate the Example in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExampleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedExample, example), getPersistedExample(example));
    }

    @Test
    void fullUpdateExampleWithPatch() throws Exception {
        // Initialize the database
        insertedExample = exampleRepository.save(example);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the example using partial update
        Example partialUpdatedExample = new Example();
        partialUpdatedExample.setId(example.getId());

        partialUpdatedExample.name(UPDATED_NAME);

        restExampleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExample.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExample))
            )
            .andExpect(status().isOk());

        // Validate the Example in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExampleUpdatableFieldsEquals(partialUpdatedExample, getPersistedExample(partialUpdatedExample));
    }

    @Test
    void patchNonExistingExample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        example.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExampleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, example.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(example))
            )
            .andExpect(status().isBadRequest());

        // Validate the Example in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchExample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        example.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExampleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(example))
            )
            .andExpect(status().isBadRequest());

        // Validate the Example in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamExample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        example.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExampleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(example)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Example in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteExample() throws Exception {
        // Initialize the database
        insertedExample = exampleRepository.save(example);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the example
        restExampleMockMvc
            .perform(delete(ENTITY_API_URL_ID, example.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return exampleRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Example getPersistedExample(Example example) {
        return exampleRepository.findById(example.getId()).orElseThrow();
    }

    protected void assertPersistedExampleToMatchAllProperties(Example expectedExample) {
        assertExampleAllPropertiesEquals(expectedExample, getPersistedExample(expectedExample));
    }

    protected void assertPersistedExampleToMatchUpdatableProperties(Example expectedExample) {
        assertExampleAllUpdatablePropertiesEquals(expectedExample, getPersistedExample(expectedExample));
    }
}
