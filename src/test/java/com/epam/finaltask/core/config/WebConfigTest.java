package com.epam.finaltask.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    private final WebConfig webConfig = new WebConfig();

    @Test
    void shouldCreateLocaleResolverWithEnglishAsDefaultLocale() {
        LocaleResolver resolver = webConfig.localeResolver();

        assertNotNull(resolver);
        assertInstanceOf(SessionLocaleResolver.class, resolver);

        MockHttpServletRequest request = new MockHttpServletRequest();

        assertEquals(
                Locale.ENGLISH,
                resolver.resolveLocale(request)
        );
    }

    @Test
    void shouldCreateLocaleChangeInterceptorWithLangParameter() {
        LocaleChangeInterceptor interceptor =
                webConfig.localeChangeInterceptor();

        assertNotNull(interceptor);
        assertEquals("lang", interceptor.getParamName());
    }

    @Test
    void shouldCreateMessageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                webConfig.messageSource();

        assertNotNull(messageSource);

        assertTrue(
                messageSource.getBasenameSet().contains("classpath:messages")
        );
    }
}