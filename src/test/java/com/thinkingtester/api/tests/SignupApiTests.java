package com.thinkingtester.api.tests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.SignUpApiClient;
import com.thinkingtester.base.BaseApiTest;

import io.restassured.response.Response;

public class SignUpApiTests extends BaseApiTest {
    
    @Test(groups = {"api", "regression"})
    public void signUpWithValidData() {
        SignUpApiClient signUpClient = new SignUpApiClient(noAuthRequestSpec);

        Map<String, Object> signUpPayload = new HashMap<>();
        signUpPayload.put("firstName", "Dustin");
        signUpPayload.put("lastName", "Henderson");
        signUpPayload.put("email", "dustinhenderson@test.com");
        signUpPayload.put("password", "dustinhenderson1235");

        Response singUpResponse = signUpClient.addUser(signUpPayload);

        Assert.assertEquals(singUpResponse.getStatusCode(), 201,
                "Expected 201 OK Status code for new User SignIn");
        String signInToken = singUpResponse.jsonPath().getString("token");
        
        System.out.println("API: New User Sign-In successful.");

        //Get User Profile
        Response getResponse = signUpClient.getUser(signInToken);
        System.out.println("User Profile reponse body: " + getResponse.asPrettyString());

        // Delete User
        Response deleteResponse = signUpClient.deleteUser(signInToken);

        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                    "Expected 200 OK Status code for user deletion");
        System.out.println("API: User Deleted");
    }
}
