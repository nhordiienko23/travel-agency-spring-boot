
package com.epam.finaltask.log;

public final class AuditContext {

    private static final ThreadLocal<String> DETAILS = new ThreadLocal<>();

    private AuditContext() {
    }

    public static void setDetails(String details) {
        DETAILS.set(details);
    }

    public static String getDetails() {
        return DETAILS.get();
    }

    public static void clear() {
        DETAILS.remove();
    }
}

