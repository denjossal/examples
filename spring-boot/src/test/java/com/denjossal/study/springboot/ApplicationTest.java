package com.denjossal.study.springboot;

import static org.assertj.core.api.Assertions.assertThat;

import com.denjossal.study.springboot.api.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    void webLayerBeansAreWired() {
        // Smoke test: the controller and exception handler are part of the context.
        assertThat(context.getBean(UserController.class)).isNotNull();
        assertThat(context.containsBean("globalExceptionHandler")).isTrue();
    }
}
