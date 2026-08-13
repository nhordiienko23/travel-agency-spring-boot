package com.epam.finaltask.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SwaggerConfig.class)
class SwaggerConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void swaggerConfigContextLoads() {
        // Проверяем, что контекст успешно поднялся
        assertNotNull(applicationContext, "Application context should not be null");

        // Проверяем, что бин SwaggerConfig зарегистрирован
        assertTrue(applicationContext.containsBean("swaggerConfig"), "SwaggerConfig bean should be present in context");
    }
}