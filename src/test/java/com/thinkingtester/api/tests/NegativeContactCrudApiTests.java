package com.thinkingtester.api.tests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;

import com.thinkingtester.base.BaseApiTest;
import com.thinkingtester.utils.TestDataUtil;
import com.thinkingtester.utils.TestLogger;

import io.restassured.response.Response;

public class NegativeContactCrudApiTests extends BaseApiTest{
    private static final TestLogger logger = TestLogger.getLogger(NegativeContactCrudApiTests.class);
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
        logger.setup("Contact created with Id: %s", contactId);
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

        logger.validation("Contact creation failed - Invalid FirstName and LastName (400 Bad Request)");

        //Create a contact with Invalid email format.
        contactPayload.clear();
        invalidEmail = TestDataUtil.generateInvalidEmail();

        contactPayload.put("firstName", "Michael");
        contactPayload.put("lastName", "Wheeler");
        contactPayload.put("email", invalidEmail);

        Response addResponse2 = contactClient.addContact(contactPayload);

        Assert.assertEquals(addResponse2.getStatusCode(), 400,
                "Expected 400 Bad Request for invalid email format");

        logger.validation("Contact creation failed - Invalid email address provided (400 Bad Request)");

        //Create Contact without Authentication
        contactPayload.clear();
        contactPayload.put("firstName", "Nancy");
        contactPayload.put("lastName", "Wheeler");

        Response addResponse3 = contactClientWithoutAuth.addContactWithoutAuthentication(contactPayload);
        Assert.assertEquals(addResponse3.getStatusCode(), 401,
                    "Expected 401 Unauthorized status code for unauthorized access");

        logger.validation("Contact creation failed - 401 Unauthorized");

        //Update Contact with Invalid ID
        String invalidContactId = "6975c237ee4f4400154acee3";
        updatePayload.put("email", email);

        Response updateResponse = contactClient.updateContact(updatePayload, invalidContactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 404,
                    "Expected 404 Not Found status code for invalid contact Id");

        logger.validation("Invalid Contact Id passed - 404 Not Found");

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
        logger.validation("Email unchanged after invalid update - Contact remains unchanged");

        //Update contact without Authentication
        updatePayload.put("phone", TestDataUtil.generatePhoneNumber());

        Response updateResponse3 = contactClientWithoutAuth.updateContact(updatePayload, contactId);
        Assert.assertEquals(updateResponse3.getStatusCode(), 401,
                 "Expected 401 Unauthorized status code for Invalid authentication");

        logger.validation("Update failed - Invalid Authentication (401 Unauthorized)");

        //Delete contact with Invalid ContactID
        Response deletResponse = contactClient.deleteContact(invalidContactId);

        Assert.assertEquals(deletResponse.getStatusCode(), 404,
                "Expected 404 Not Found status code when invalid Contact Id passed");

        logger.validation("Contact deletion failed - Invalid Contact Id (404 Not Found)");

        //Delete contact without Authentication
        Response deletResponse2 = contactClientWithoutAuth.deleteContact(contactId);

        Assert.assertEquals(deletResponse2.getStatusCode(), 401,
                "Expected 401 Unauthorized status code for Invalid Authentication.");

        logger.validation("Contact deletion failed - Unauthorized access (401)");
 
        //Delete contact
        Response deleteResponse = contactClient.deleteContact(contactId);

        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                "Expected 200 OK Status Code for contact deletion.");

        //Post delete Negative validation
        Response validatedeleteResponse = contactClient.getContactById(contactId);
        contactId = null;

        Assert.assertEquals(validatedeleteResponse.getStatusCode(), 404,
                "Expected 404 Not Found status code for deleted contact.");

        logger.validation("Deleted contact successfully not retrieved (404 response)");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUpContactTestData() {
            if (contactId != null) {
                    ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);
                    Response deleteResponse = contactClient.deleteContact(contactId);

                    Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                                    "Expected 200 OK Status code for Contact deletion");
                    logger.cleanup("Contact deletion status: %s", deleteResponse.getStatusCode());
            } else {
                logger.cleanup("Contact Id is null - No cleanup required");
            }
    }
}
