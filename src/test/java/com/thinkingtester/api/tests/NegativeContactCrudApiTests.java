package com.thinkingtester.api.tests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.ApiRequestFactory;
import com.thinkingtester.base.BaseApiTest;
import com.thinkingtester.utils.TestDataUtil;

import io.restassured.response.Response;

public class NegativeContactCrudApiTests extends BaseApiTest{
    private String contactId;
    private String email;
    private String invalidEmail;
    private String phoneNumber;
    private Map<String, Object> contactPayload = new HashMap<>();
    private Map<String, Object> updatePayload = new HashMap<>();

    @BeforeClass(alwaysRun = true)
    public void setupContactTestData() {
        email = TestDataUtil.generateUniqueEmail();
        phoneNumber = TestDataUtil.generatePhoneNumber();

        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);
        contactPayload.put("firstName", "William");
        contactPayload.put("lastName", "Byers");
        contactPayload.put("email", email);
        contactPayload.put("phone", phoneNumber);
        contactPayload.put("city", "Hawkins");
        contactPayload.put("state", "Texas");
        contactPayload.put("country", "United States of America");

        Response createResponse = contactClient.addContact(contactPayload);
        Assert.assertEquals(createResponse.getStatusCode(), 201,
                        "Expected 201 OK Status code for contact creation");
        contactId = createResponse.jsonPath().getString("_id");
        System.out.println("API: Contact Created with Id: " + contactId);
    }

    @Test(groups = {"api", "regression", "negative", "e2e"})
    public void negativeContacte2eFlow() {
        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);
        ContactApiClient contactClientWithoutAuth = new ContactApiClient(noAuthRequestSpec);

        //Create Contact with missing mandatory fields
        contactPayload.clear();
        contactPayload.put("firstName", "");
        contactPayload.put("lastName", "");

        Response addResponse = contactClient.addContact(contactPayload);
        
        Assert.assertEquals(addResponse.getStatusCode(), 400, 
                "Expected 400 Bad Request status code for invalid contact creation");

        System.out.println("Contact creation failed. Invalid FirstName and LastName");

        //Create a contact with Invalid email format.
        contactPayload.clear();
        invalidEmail = TestDataUtil.generateInvalidEmail();

        contactPayload.put("firstName", "Michael");
        contactPayload.put("lastName", "Wheeler");
        contactPayload.put("email", invalidEmail);

        Response addResponse2 = contactClient.addContact(contactPayload);

        Assert.assertEquals(addResponse2.getStatusCode(), 400,
                "Expected 400 Bad Request for invalid email format");
        
        System.out.println("Contact creation failed: Invalid email address provided.");

        //Create Contact without Authentication
        contactPayload.clear();
        contactPayload.put("firstName", "Nancy");
        contactPayload.put("lastName", "Wheeler");

        Response addResponse3 = contactClientWithoutAuth.addContactWithoutAuthentication(contactPayload);
        Assert.assertEquals(addResponse3.getStatusCode(), 401,
                    "Expected 401 Unauthorized status code for unauthorized access");
        
        System.out.println("Contact creation failed. 401 Unauthorized");

        //Update Contact with Invalid ID
        String invalidContactId = "6975c237ee4f4400154acee3";
        updatePayload.put("email", email);

        Response updateResponse = contactClient.updateContact(updatePayload, invalidContactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 404,
                    "Expected 404 Not Found status code for invalid contact Id");
        
        System.out.println("Invalid Contact Id is Passed. 404 Not Found status code encountered");

        //Update Contact with Invalid Payload
        invalidEmail = TestDataUtil.generateInvalidEmail();
        updatePayload.clear();
        updatePayload.put("email", invalidEmail);

        Response updateResponse2 = contactClient.updateContact(updatePayload, contactId);
        Response getResponse = contactClient.getContactById(contactId);

        Assert.assertEquals(updateResponse2.getStatusCode(), 400, 
                    "Expected 400 Bad Request status code for Invalid email format and Empty Update Payload");
        
        Assert.assertEquals(getResponse.jsonPath().getString("email"), email, 
                "Email is unchanged before and after update. So contact remains unchanged.");
        System.out.println("Email is unchanged before and after update. Contact remains unchanged");

        //Update contact without Authentication
        updatePayload.put("phone", TestDataUtil.generatePhoneNumber());

        Response updateResponse3 = contactClientWithoutAuth.updateContact(updatePayload, contactId);
        Assert.assertEquals(updateResponse3.getStatusCode(), 401,
                 "Expected 401 Unauthorized status code for Invalid authentication");
        
        System.out.println("Invalid Authentication. 401 Unauthorized");

        //Delete contact with Invalid ContactID
        Response deletResponse = contactClient.deleteContact(invalidContactId);

        Assert.assertEquals(deletResponse.getStatusCode(), 404,
                "Expected 404 Not Found status code when invalid Contact Id passed");
        
        System.out.println("Contact deletion failed. 404 Not Found");

        //Delete contact without Authentication
        Response deletResponse2 = contactClientWithoutAuth.deleteContact(contactId);

        Assert.assertEquals(deletResponse2.getStatusCode(), 401,
                "Expected 401 Unauthorized status code for Invalid Authentication.");
        
        System.out.println("Contact deletion failed. 401 Unauthorized");
 
        //Delete contact
        Response deleteResponse = contactClient.deleteContact(contactId);

        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                "Expected 200 OK Status Code for contact deletion.");

        //Post delete Negative validation
        Response validatedeleteResponse = contactClient.getContactById(contactId);
        contactId = null;

        Assert.assertEquals(validatedeleteResponse.getStatusCode(), 404,
                "Expected 404 Not Found status code for deleted contact.");

        System.out.println("Deleted contact successfully not retrieved");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUpContactTestData() {
            if (contactId != null) {
                    ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);
                    Response deleteResponse = contactClient.deleteContact(contactId);

                    Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                                    "Expected 200 OK Status code for Contact deletion");
                    System.out.println("CLEANUP: Contact deletion status: " + deleteResponse.getStatusCode());
            } else {
                System.out.println("Contact Id is null. No CleanUp is required");
            }
    }
}
