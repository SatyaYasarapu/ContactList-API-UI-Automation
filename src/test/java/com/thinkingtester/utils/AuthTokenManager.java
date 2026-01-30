package com.thinkingtester.utils;

import java.util.HashMap;
import java.util.Map;

import com.thinkingtester.config.ConfigReader;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthTokenManager {
    public static String token;

    private AuthTokenManager() {

    }

    public static synchronized String getToken() {
        if (token == null) {
            token = generateToken();
        }
        return token;
    }

    private static String generateToken() {
        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", "satya@test.com");
        loginPayload.put("password", "satya123");

        Response response = 
                        RestAssured
                                .given()
                                    .baseUri(ConfigReader.get("base.api.url"))
                                    .contentType("application/json")
                                    .body(loginPayload)
                                    .post("/users/login")
                                .then()
                                    .statusCode(200)
                                    .extract()
                                    .response();
        return response.jsonPath().getString("token");
    }

    public static synchronized void clearToken() {
        token = null;
    }
}
