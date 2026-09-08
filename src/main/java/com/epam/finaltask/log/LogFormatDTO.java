package com.epam.finaltask.log;

import lombok.Builder;

@Builder
public record LogFormatDTO(
        String level,
        String method,
        String args,
        String result,
        String errorMessage,
        String cause
) {

    public String formatMessage() {

        StringBuilder sb = new StringBuilder();

        if (args != null && !args.isBlank()) {
            sb.append(args.trim());
        }

        if (result != null && !result.isBlank()) {

            if (!sb.isEmpty()) {
                sb.append(" | ");
            }

            sb.append(result.trim());
        }

        if (errorMessage != null && !errorMessage.isBlank()) {

            if (!sb.isEmpty()) {
                sb.append(" | ");
            }

            sb.append("Error='")
                    .append(errorMessage.trim())
                    .append("'");
        }

        if (cause != null
                && !cause.isBlank()
                && !"NULL".equalsIgnoreCase(cause)) {

            if (!sb.isEmpty()) {
                sb.append(" | ");
            }

            sb.append("Cause='")
                    .append(cause.trim())
                    .append("'");
        }

        return sb.toString();
    }
}