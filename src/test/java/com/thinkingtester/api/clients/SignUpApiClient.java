package com.thinkingtester.api.clients;

import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SignUpApiClient {
    private RequestSpecification reqSpec;

    public SignUpApiClient(RequestSpecification specReq) {
        this.reqSpec = specReq;
    }

    public Response addUser(Map<String, Object> signUpPayload) {
        return RestAssured
                        .given()
                            .spec(reqSpec)
                            .body(signUpPayload)
                        .when()
                            .post("/users")
                        .then()
                            .extract()
                            .response();
    }

    public Response getUser(String SignInToken) {
        return RestAssured
                        .given()
                            .spec(reqSpec)
                            .header("Authorization", "Bearer " + SignInToken)
                        .when()
                            .get("/users/me")
                        .then()
                            .extract()
                            .response();
    }

    public Response deleteUser(String SignInToken) {
        return RestAssured
                        .given()
                            .spec(reqSpec)
                            .header("Authorization", "Bearer " + SignInToken)
                        .when()
                            .delete("/users/me")
                        .then()
                            .extract()
                            .response();
    }
}
