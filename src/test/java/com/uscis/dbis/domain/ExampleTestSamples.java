package com.uscis.dbis.domain;

import java.util.UUID;

public class ExampleTestSamples {

    public static Example getExampleSample1() {
        return new Example().id("id1").name("name1");
    }

    public static Example getExampleSample2() {
        return new Example().id("id2").name("name2");
    }

    public static Example getExampleRandomSampleGenerator() {
        return new Example().id(UUID.randomUUID().toString()).name(UUID.randomUUID().toString());
    }
}
