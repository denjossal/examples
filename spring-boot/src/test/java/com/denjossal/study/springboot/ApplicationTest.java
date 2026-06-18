package com.denjossal.study.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.junit.jupiter.api.condition.DisabledIfSystemProperty(named = "java.specification.version", matches = "2[2-9]|[3-9]\\d")
class ApplicationTest {

    @Test
    void contextLoads() {
    }
}
