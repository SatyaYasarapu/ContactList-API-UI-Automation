package com.thinkingtester.base;

import org.testng.annotations.BeforeClass;

import com.thinkingtester.config.ConfigReader;
import com.thinkingtester.utils.AuthTokenManager;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public abstract class BaseApiTest {
    protected static RequestSpecification baseRequestSpec;
    protected RequestSpecification noAuthRequestSpec;

    @BeforeClass(alwaysRun = true)
    public void setUpApi() {

        RestAssured.baseURI = ConfigReader.get("base.api.url");

        baseRequestSpec = new RequestSpecBuilder()
                .setContentType("application/json")
                .addHeader("Authorization", "Bearer " + AuthTokenManager.getToken())
                .build();

        noAuthRequestSpec = new RequestSpecBuilder()
                .setContentType("application/json")
                .build();
    }

    protected io.restassured.specification.RequestSpecification request() {
        return io.restassured.RestAssured
                .given()
                .spec(com.thinkingtester.base.BaseApiTest.baseRequestSpec);
    }

}
