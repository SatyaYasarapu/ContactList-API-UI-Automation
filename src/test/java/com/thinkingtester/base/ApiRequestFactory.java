package com.thinkingtester.base;

import com.thinkingtester.config.ConfigReader;
import com.thinkingtester.utils.AuthTokenManager;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiRequestFactory {
    
    public static RequestSpecification newRequest() {
        RestAssured.baseURI = ConfigReader.get("base.api.url");

        return new RequestSpecBuilder()
                .setContentType("application/json")
                .addHeader("Authorization", "Bearer " + AuthTokenManager.getToken())
                .build();
    }
}
