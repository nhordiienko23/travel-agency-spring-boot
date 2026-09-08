package com.epam.finaltask.log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditContextTest {

    @AfterEach
    void tearDown() {
        AuditContext.clear();
    }

    @Test
    void getDetails_shouldReturnNullInitially() {
        assertNull(AuditContext.getDetails());
    }

    @Test
    void setDetails_shouldStoreDetails() {
        AuditContext.setDetails("price: 100.00 -> 150.00");

        assertEquals(
                "price: 100.00 -> 150.00",
                AuditContext.getDetails()
        );
    }

    @Test
    void clear_shouldRemoveDetails() {
        AuditContext.setDetails("some details");

        AuditContext.clear();

        assertNull(AuditContext.getDetails());
    }

    @Test
    void setDetails_shouldReplacePreviousValue() {
        AuditContext.setDetails("first");
        AuditContext.setDetails("second");

        assertEquals("second", AuditContext.getDetails());
    }
}