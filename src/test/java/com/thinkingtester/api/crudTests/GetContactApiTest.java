package com.thinkingtester.api.crudTests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseApiTest;

import io.restassured.response.Response;

public class GetContactApiTest extends BaseApiTest {

    @Test(groups = { "api", "regression" }, dependsOnMethods = {
            "com.thinkingtester.api.tests.AddContactApiTest.addContactWithValidData"
    })
    public void getContactById() {
        String contactId = AddContactApiTest.contactId;

        Assert.assertNotNull(contactId,
                "Contact ID should not be null before GET");

        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);

        Response response = contactClient.getContactById(contactId);

        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected 200 OK when fetching contact");

        Assert.assertEquals(response.jsonPath().get("_id"),
                contactId,
                "Fetched Contact ID mismatch");

        Assert.assertNotNull(response.jsonPath().getString("firstName"),
                "First Name should not be null");

        System.out.println("Email fetched for Contact ID: "
                + response.jsonPath().getString("email"));
    }
}
