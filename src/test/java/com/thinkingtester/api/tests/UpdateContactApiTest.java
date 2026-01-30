package com.thinkingtester.api.tests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseApiTest;

import io.restassured.response.Response;

public class UpdateContactApiTest extends BaseApiTest{

    @Test(groups = {"api", "regression"}, dependsOnMethods = {
        "com.thinkingtester.api.tests.AddContactApiTest.addContactWithValidData"
    })
    public void updateContactByID() {
        String contactId = AddContactApiTest.contactId;
        // String contactID = "123";

        Assert.assertNotNull(contactId, "Contact ID should not be null before GET");

        Map<String, Object> updateContactPayload = new HashMap<>();
        updateContactPayload.put("city", "New York");
        updateContactPayload.put("phone", "9087651234");
        
        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);

        Response response = contactClient.updateContact(updateContactPayload, contactId);

        Assert.assertEquals(response.getStatusCode(), 200, 
                    "Expected 200 OK Status code for Contact Updation");

        Assert.assertEquals(response.jsonPath().get("_id"),
                        contactId,
                        "Fetched Contact ID mismatch");
        
        System.out.println("Contact reponse after Updation is: " + response.asPrettyString());
    }
}
