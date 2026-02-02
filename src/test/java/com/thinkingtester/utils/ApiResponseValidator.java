package com.thinkingtester.utils;

import org.testng.Assert;

import io.restassured.response.Response;

public final class ApiResponseValidator {

    private ApiResponseValidator() {
        // Prevent instantiation
    }

    // CREATE (POST /contacts)

    public static void validateContactCreated(Response response) {
        Assert.assertEquals(response.getStatusCode(), 201,
                "Expected 201 Created status");

        Assert.assertNotNull(response.jsonPath().getString("_id"),
                "Contact ID should not be null");

        Assert.assertNotNull(response.jsonPath().getString("email"),
                "Email should not be null in create response");
    }

    public static void validateCreateBadRequest(Response response) {
        Assert.assertEquals(response.getStatusCode(), 400,
                "Expected 400 Bad Request for invalid create payload");
    }

    // READ (GET /contacts/{id})

    public static void validateContactFetched(Response response, String expectedContactId) {
        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected 200 OK while fetching contact");

        Assert.assertEquals(response.jsonPath().getString("_id"),
                expectedContactId,
                "Fetched contact ID mismatch");
    }

    public static void validateContactNotFound(Response response) {
        Assert.assertEquals(response.getStatusCode(), 404,
                "Expected 404 Not Found");
    }

    // UPDATE (PUT /contacts/{id})

    public static void validateContactUpdated(Response response) {
        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected 200 OK for successful update");
    }

    public static void validateUpdateBadRequest(Response response) {
        Assert.assertEquals(response.getStatusCode(), 400,
                "Expected 400 Bad Request for invalid update payload");
    }

    public static void validateUpdateNotFound(Response response) {
        Assert.assertEquals(response.getStatusCode(), 404,
                "Expected 404 Not Found for invalid contact ID");
    }

    // DELETE (DELETE /contacts/{id})

    public static void validateContactDeleted(Response response) {
        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected 200 OK for successful deletion");
    }

    // AUTH / SECURITY

    public static void validateUnauthorized(Response response) {
        Assert.assertEquals(response.getStatusCode(), 401,
                "Expected 401 Unauthorized");
    }

    // GENERIC HELPERS

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Unexpected HTTP status code");
    }
}

