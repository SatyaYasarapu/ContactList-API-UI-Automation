package com.thinkingtester.api.clients;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class AuthApiClient {
    public static void main(String[] args) {
        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", "rpraveen@test.com");
        loginPayload.put("password", "rpraveen123");

        Response response = 
                        given()
                            .contentType("application/json")
                            .body(loginPayload)
                        .when()
                            .post("https://thinking-tester-contact-list.herokuapp.com/users/login")
                        .then()
                            .statusCode(200)
                            .extract()
                            .response();
        String token = 
                    given()
                        .contentType("application/json")
                        .body(loginPayload)
                    .when()
                        .post("https://thinking-tester-contact-list.herokuapp.com/users/login")
                    .then()
                        .statusCode(200)
                        .extract()
                        .path("token");
        System.out.println("Login Successfull: " + token);
        System.out.println("Response is: " + response.asPrettyString());
        // System.out.println("Email of logged in User is: " + email);
    }
}
