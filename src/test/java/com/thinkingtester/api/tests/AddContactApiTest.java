package com.thinkingtester.api.tests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseApiTest;

import io.restassured.response.Response;

public class AddContactApiTest extends BaseApiTest {
    protected static String contactId;

    @Test(groups = {"api", "regression"})
    public void addContactWithValidData() {
        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);

        Map<String, Object> contactPayload = new HashMap<>();
        contactPayload.put("firstName", "Nikhila");
        contactPayload.put("lastName", "Surabhi");
        contactPayload.put("email", "nikhilasurabhi@test.com");
        contactPayload.put("phone", "9876543210");
        contactPayload.put("city", "Hyderabad");
        contactPayload.put("state", "Telangana");
        contactPayload.put("country", "India");

        Response response = contactClient.addContact(contactPayload);

        Assert.assertEquals(response.getStatusCode(), 201, 
                    "Expected status code 201 for contact creation");
        contactId = response.jsonPath().getString("_id");

        Assert.assertNotNull("Contact ID should not be null");

        System.out.println("Contact created successfully. ID = " + contactId);

        System.out.println("Response for " +contactId+ " is: " + response.asPrettyString());
    }
}
