package com.thinkingtester.api.tests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseApiTest;

import io.restassured.response.Response;

public class NegativeContactCrudApiTests extends BaseApiTest{
    private String contactId;
    private Map<String, Object> contactPayload = new HashMap<>();
    private Map<String, Object> updatePayload = new HashMap<>();

    @Test(groups = {"api", "regression", "negative", "e2e"})
    public void negativeContacte2eFlow() {
        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);
        ContactApiClient contactClientWithoutAuth = new ContactApiClient(noAuthRequestSpec);

        //Create Contact with missing mandatory fields
        contactPayload.put("firstName", "");
        contactPayload.put("lastName", "");

        Response addResponse = contactClient.addContact(contactPayload);
        
        Assert.assertEquals(addResponse.getStatusCode(), 400, 
                "Expected 400 Bad Request status code for invalid contact creation");

        contactId = addResponse.jsonPath().getString("_id");
        Assert.assertNull(contactId, 
                "Contact Id should not be generated for a invalid request");
        System.out.println("Contact creation failed. Invalid FirstName and LastName");

        //Create a contact with Invalid email format.
        contactPayload.put("firstName", "Michael");
        contactPayload.put("lastName", "Wheeler");
        contactPayload.put("email", "mikewheelertest.com");

        Response addResponse2 = contactClient.addContact(contactPayload);

        Assert.assertEquals(addResponse2.getStatusCode(), 400,
                "Expected 400 Bad Request for invalid email format");
        
        contactId = addResponse2.jsonPath().getString("_id");
        Assert.assertNull(contactId,
                "Contact Id should not be generated for invalid request");
        System.out.println("Contact creation failed: Invalid email address provided.");

        //Create Contact without Authentication
        contactPayload.put("firstName", "Nancy");
        contactPayload.put("lastName", "Wheeler");

        Response addResponse3 = contactClientWithoutAuth.addContactWithoutAuthentication(contactPayload);
        Assert.assertEquals(addResponse3.getStatusCode(), 401,
                    "Expected 401 Unauthorized status code for unauthorized access");
        
        contactId = addResponse3.jsonPath().getString("_id");
        Assert.assertNull(contactId, 
            "Contact creation failed. Invalid Authentication");

        System.out.println("Contact creation failed. 401 Unauthorized");

        //Update Contact with Invalid ID
        String invalidContactId = "6975c237ee4f4400154acee3";
        updatePayload.put("email", "invalidupdate@test.com");

        Response updateResponse = contactClient.updateContact(updatePayload, invalidContactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 404,
                    "Expected 404 Not Found status code for invalid contact Id");
        
        System.out.println("Invalid Contact Id is Passed. 404 Not Found status code encountered");

        //Update Contact with Invalid Payload
        String validContactId = "6975c237ee4f4400154acee9";
        updatePayload.put("email", "invalidemailtest.com");
        updatePayload.put("phone", "");

        Response getResponse = contactClient.getContactById(validContactId);

        Response updaResponse2 = contactClient.updateContact(updatePayload, validContactId);

        Assert.assertEquals(updaResponse2.getStatusCode(), 400, 
                    "Expected 400 Bad Request status code for Invalid email format and Empty Update Payload");
        
        Response getResponse2 = contactClient.getContactById(validContactId);

        Assert.assertEquals(getResponse.jsonPath().getString("email"), 
                getResponse2.jsonPath().getString("email"), 
                "Email is unchanged before and after update. So contact remains unchanged.");
        System.out.println("Email is unchanged before and after update. Contact remains unchanged");

        //Update contact without Authentication
        updatePayload.put("phone", "9870462439");

        Response updateResponse3 = contactClientWithoutAuth.updateContact(updatePayload, validContactId);
        Assert.assertEquals(updateResponse3.getStatusCode(), 401,
                 "Expected 401 Unauthorized status code for Invalid authentication");
        
        System.out.println("Invalid Authentication. 401 Unauthorized");

        //Delete contact with Invalid ContactID
        Response deletResponse = contactClient.deleteContact(invalidContactId);

        Assert.assertEquals(deletResponse.getStatusCode(), 404,
                "Expected 404 Not Found status code when invalid Contact Id passed");
        
        System.out.println("Contact deletion failed. 404 Not Found");

        //Delete contact without Authentication
        Response deletResponse2 = contactClientWithoutAuth.deleteContact(validContactId);

        Assert.assertEquals(deletResponse2.getStatusCode(), 401,
                "Expected 401 Unauthorized status code for Invalid Authentication.");
        
        System.out.println("Contact deletion failed. 401 Unauthorized");

        //Negative E2E Flow
        contactPayload.put("firstName", "Jane");
        contactPayload.put("lastName", "Hopper");
        contactPayload.put("email", "janehopper@test.com");
        contactPayload.put("phone", "9016354836");

        Response addResponse4 = contactClient.addContact(contactPayload);

        Assert.assertEquals(addResponse4.getStatusCode(), 201,
                "Expected 201 OK Status code for adding new contact");

        contactId = addResponse4.jsonPath().getString("_id");
        //email = addResponse4.jsonPath().getString("email");

        Assert.assertNotNull(contactId, "Contact Id cannot be null");

        System.out.println("Contact created successfully. ID: " + contactId);

        updatePayload.put("email", "janehoppertest.com");

        Response updateResponse2 = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse2.getStatusCode(), 400,
            "Expected 400 Bad Request status code for inavlid payload data.");

        // Assert.assertEquals(email, updateResponse2.jsonPath().getString("email"),
        //             "Email is unchanged before and after update.");  
        
        //Delete contact
        Response deleteResponse = contactClient.deleteContact(contactId);

        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                "Expected 200 OK Status Code for contact deletion.");

        //Post delete Negative validation
        Response validatedeleteResponse = contactClient.getContactById(contactId);

        Assert.assertEquals(validatedeleteResponse.getStatusCode(), 404,
                "Expected 404 Not Found status code for deleted contact.");

        System.out.println("Deleted contact successfully not retrieved");
    }
}
