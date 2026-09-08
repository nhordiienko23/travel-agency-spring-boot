package com.epam.finaltask.core.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void noArgsConstructor_shouldCreateObject() {
        ApiResponse<String> response = new ApiResponse<>();

        assertNull(response.getStatusCode());
        assertNull(response.getStatusMessage());
        assertNull(response.getResults());
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        ApiResponse<String> response =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertEquals("OK", response.getStatusCode());
        assertEquals("Success", response.getStatusMessage());
        assertEquals("data", response.getResults());
    }

    @Test
    void gettersAndSetters_shouldWork() {
        ApiResponse<String> response = new ApiResponse<>();

        response.setStatusCode("200");
        response.setStatusMessage("Success");
        response.setResults("data");

        assertEquals("200", response.getStatusCode());
        assertEquals("Success", response.getStatusMessage());
        assertEquals("data", response.getResults());
    }

    @Test
    void ok_shouldCreateResponseWithResults() {
        ApiResponse<String> response =
                ApiResponse.ok(
                        "Operation successful",
                        "result"
                );

        assertEquals("OK", response.getStatusCode());
        assertEquals(
                "Operation successful",
                response.getStatusMessage()
        );
        assertEquals("result", response.getResults());
    }

    @Test
    void okWithoutResults_shouldCreateResponseWithoutResults() {
        ApiResponse<String> response =
                ApiResponse.ok("Operation successful");

        assertEquals("OK", response.getStatusCode());
        assertEquals(
                "Operation successful",
                response.getStatusMessage()
        );
        assertNull(response.getResults());
    }

    @Test
    void builder_shouldSetAllFields() {
        ApiResponse<String> response =
                ApiResponse.<String>builder()
                        .statusCode("201")
                        .statusMessage("Created")
                        .results("created")
                        .build();

        assertEquals("201", response.getStatusCode());
        assertEquals("Created", response.getStatusMessage());
        assertEquals("created", response.getResults());
    }

    @Test
    void builder_toString_shouldReturnString() {
        ApiResponse.ApiResponseBuilder<String> builder =
                ApiResponse.<String>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .results("data");

        assertNotNull(builder.toString());
    }

    @Test
    void equals_shouldReturnTrueForEqualObjects() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertEquals(first, second);
    }

    @Test
    void equals_shouldReturnFalseWhenStatusCodeDiffers() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "ERROR",
                        "Success",
                        "data"
                );

        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldReturnFalseWhenStatusMessageDiffers() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "OK",
                        "Failure",
                        "data"
                );

        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldReturnFalseWhenResultsDiffer() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data1"
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data2"
                );

        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldReturnTrueForSameObject() {
        ApiResponse<String> response =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertEquals(response, response);
    }

    @Test
    void equals_shouldReturnFalseForNull() {
        ApiResponse<String> response =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertNotEquals(response, null);
    }

    @Test
    void equals_shouldReturnFalseForDifferentClass() {
        ApiResponse<String> response =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertNotEquals(
                response,
                "not an ApiResponse"
        );
    }

    @Test
    void equals_shouldHandleNullFields() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        null,
                        null,
                        null
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        null,
                        null,
                        null
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equals_shouldDetectNullStatusCodeDifference() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        null,
                        "Success",
                        "data"
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldDetectNullStatusMessageDifference() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        "OK",
                        null,
                        "data"
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldDetectNullResultsDifference() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        null
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertNotEquals(first, second);
    }

    @Test
    void hashCode_shouldBeEqualForEqualObjects() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        assertEquals(
                first.hashCode(),
                second.hashCode()
        );
    }

    @Test
    void hashCode_shouldHandleDifferentValues() {
        ApiResponse<String> first =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        ApiResponse<String> second =
                new ApiResponse<>(
                        "ERROR",
                        "Failure",
                        "other"
                );

        assertNotEquals(
                first.hashCode(),
                second.hashCode()
        );
    }

    @Test
    void toString_shouldContainFields() {
        ApiResponse<String> response =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        String result = response.toString();

        assertTrue(result.contains("OK"));
        assertTrue(result.contains("Success"));
        assertTrue(result.contains("data"));
    }

    @Test
    void toString_shouldHandleNullFields() {
        ApiResponse<String> response =
                new ApiResponse<>(
                        null,
                        null,
                        null
                );

        assertNotNull(response.toString());
    }


    @Test
    void equals_shouldReturnFalseWhenOtherCanEqualReturnsFalse() {

        ApiResponse<String> response =
                new ApiResponse<>(
                        "OK",
                        "Success",
                        "data"
                );

        class IncompatibleApiResponse
                extends ApiResponse<String> {

            IncompatibleApiResponse(
                    String statusCode,
                    String statusMessage,
                    String results
            ) {
                super(
                        statusCode,
                        statusMessage,
                        results
                );
            }

            @Override
            protected boolean canEqual(Object other) {
                return false;
            }
        }

        ApiResponse<String> incompatible =
                new IncompatibleApiResponse(
                        "OK",
                        "Success",
                        "data"
                );

        assertNotEquals(
                response,
                incompatible
        );
    }


}
