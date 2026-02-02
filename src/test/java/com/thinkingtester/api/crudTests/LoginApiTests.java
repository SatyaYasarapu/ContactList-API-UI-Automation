package com.thinkingtester.api.crudTests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.base.BaseApiTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class LoginApiTests extends BaseApiTest{

    @Test(groups = {"api", "smoke"})
    public void loginwithValidCredentials() {
        Map<String, Object> validLoginPayload = new HashMap<>();
        validLoginPayload.put("email", "rpraveen@test.com");
        validLoginPayload.put("password", "rpraveen123");

        Response response = RestAssured
                                    .given()
                                        .spec(baseRequestSpec)
                                        .body(validLoginPayload)
                                    .when()
                                        .post("/users/login")
                                    .then()
                                        .statusCode(200)
                                        .extract()
                                        .response();
        Assert.assertNotNull(response.jsonPath().getString("token"),
                    "Auth token should not be null");  
        System.out.println("Valid Case" + response.asPrettyString());
     }

    @Test(groups = {"api", "negative"})
    public void loginwithInvalidCredentials() {
        Map<String, Object> validLoginPayload = new HashMap<>();
        validLoginPayload.put("email", "rpraveen@testing.com");
        validLoginPayload.put("password", "rpraveen123456");

        Response response = RestAssured
                                    .given()
                                        .spec(baseRequestSpec)
                                        .body(validLoginPayload)
                                    .when()
                                        .post("/users/login")
                                    .then()
                                        .statusCode(401)
                                        .extract()
                                        .response();
        Assert.assertTrue(response.asString() == null || response.asString().isEmpty()
                || response.asString().contains("Unauthorized"),
                "Expected unauthorized response");

        System.out.println("Negative login validated successfully");
     }
}
