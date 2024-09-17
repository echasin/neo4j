package com.uscis.dbis;

import com.uscis.dbis.config.AsyncSyncConfiguration;
import com.uscis.dbis.config.EmbeddedNeo4j;
import com.uscis.dbis.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { GdsApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedNeo4j
public @interface IntegrationTest {
}
