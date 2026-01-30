package com.thinkingtester.api.clients;

import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ContactApiClient {
    private final RequestSpecification requestSpec;

    public ContactApiClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public Response singUpNewUser(Map<String, Object> signUpPayload) {
        return RestAssured
                        .given()
                            .spec(requestSpec)
                            .body(signUpPayload)
                        .when()
                            .post("/users")
                        .then()
                            .extract()
                            .response();
    }

    public Response addContact(Map<String, Object> contactPayload) {  
        return RestAssured
                        .given()
                            .spec(requestSpec)
                            .body(contactPayload)
                        .when()
                            .post("/contacts")
                        .then()
                            .extract()
                            .response();
    }

    public Response getAllContacts() {
        return RestAssured
                        .given()
                            .spec(requestSpec)
                        .when()
                            .get("/contacts")
                        .then()
                            .extract()
                            .response();
    }

    public Response getContactById(String contactId) {
        return RestAssured
                        .given()
                            .spec(requestSpec)
                        .when()
                            .get("/contacts/" + contactId)
                        .then()
                            .extract()
                            .response();
    }

    public Response updateContact(Map<String, Object> updatePayload, String contactId) {
        return RestAssured
                        .given()
                            .spec(requestSpec)
                            .body(updatePayload)
                        .when()
                            .patch("/contacts/" + contactId)
                        .then()
                            .extract()
                            .response();
    }

    public Response deleteContact(String contactId) {
        return RestAssured
                        .given()
                            .spec(requestSpec)
                        .when()
                            .delete("/contacts/" + contactId)
                        .then()
                            .extract()
                            .response();
    }

    public Response addContactWithoutAuthentication(Map<String, Object> contactPayload) {
        return RestAssured
                        .given()
                            .spec(requestSpec)
                            .body(contactPayload)
                        .when()
                            .post("/contacts")
                        .then()
                            .extract()
                            .response();
    }
}
