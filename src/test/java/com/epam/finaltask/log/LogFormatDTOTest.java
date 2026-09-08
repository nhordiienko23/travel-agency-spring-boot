package com.epam.finaltask.log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogFormatDTOTest {

    @Test
    void formatMessage_shouldReturnEmptyStringWhenAllValuesAreNull() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .level("INFO")
                .method("test.method")
                .build();

        assertEquals("", dto.formatMessage());
    }

    @Test
    void formatMessage_shouldReturnEmptyStringWhenAllValuesAreBlank() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .args("   ")
                .result("")
                .errorMessage("   ")
                .cause("   ")
                .build();

        assertEquals("", dto.formatMessage());
    }

    @Test
    void formatMessage_shouldFormatAllFields() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .args("arg1")
                .result("Result")
                .errorMessage("Something went wrong")
                .cause("Database error")
                .build();

        assertEquals(
                "arg1 | Result | Error='Something went wrong' | Cause='Database error'",
                dto.formatMessage()
        );
    }

    @Test
    void formatMessage_shouldFormatArgsOnly() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .args("arg1")
                .build();

        assertEquals("arg1", dto.formatMessage());
    }

    @Test
    void formatMessage_shouldFormatResultOnly() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .result("Result")
                .build();

        assertEquals("Result", dto.formatMessage());
    }

    @Test
    void formatMessage_shouldFormatErrorOnly() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .errorMessage("Failure")
                .build();

        assertEquals(
                "Error='Failure'",
                dto.formatMessage()
        );
    }

    @Test
    void formatMessage_shouldIgnoreNullCause() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .result("Result")
                .cause(null)
                .build();

        assertEquals("Result", dto.formatMessage());
    }

    @Test
    void formatMessage_shouldIgnoreBlankCause() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .result("Result")
                .cause("   ")
                .build();

        assertEquals("Result", dto.formatMessage());
    }

    @Test
    void formatMessage_shouldIgnoreNullLiteralCause() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .result("Result")
                .cause("NULL")
                .build();

        assertEquals("Result", dto.formatMessage());
    }

    @Test
    void formatMessage_shouldIgnoreLowerCaseNullLiteralCause() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .result("Result")
                .cause("null")
                .build();

        assertEquals("Result", dto.formatMessage());
    }

    @Test
    void formatMessage_shouldTrimValues() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .args("  args  ")
                .result("  result  ")
                .errorMessage("  error  ")
                .cause("  cause  ")
                .build();

        assertEquals(
                "args | result | Error='error' | Cause='cause'",
                dto.formatMessage()
        );
    }


    @Test
    void builder_shouldSetAllFields() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .level("INFO")
                .method("test.method")
                .args("args")
                .result("result")
                .errorMessage("error")
                .cause("cause")
                .build();

        assertEquals("INFO", dto.level());
        assertEquals("test.method", dto.method());
        assertEquals("args", dto.args());
        assertEquals("result", dto.result());
        assertEquals("error", dto.errorMessage());
        assertEquals("cause", dto.cause());
    }


    @Test
    void builder_toString_shouldReturnString() {
        LogFormatDTO.LogFormatDTOBuilder builder = LogFormatDTO.builder()
                .level("INFO")
                .method("test.method")
                .args("args")
                .result("result")
                .errorMessage("error")
                .cause("cause");

        assertNotNull(builder.toString());
    }

    @Test
    void formatMessage_shouldFormatCauseOnly() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .cause("Database error")
                .build();

        assertEquals(
                "Cause='Database error'",
                dto.formatMessage()
        );
    }





}