package com.uscis.dbis.domain;

import static com.uscis.dbis.domain.ExampleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.uscis.dbis.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExampleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Example.class);
        Example example1 = getExampleSample1();
        Example example2 = new Example();
        assertThat(example1).isNotEqualTo(example2);

        example2.setId(example1.getId());
        assertThat(example1).isEqualTo(example2);

        example2 = getExampleSample2();
        assertThat(example1).isNotEqualTo(example2);
    }
}
