package com.thinkingtester.api.tests;


import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseApiTest;
import com.thinkingtester.utils.TestDataUtil;
import com.thinkingtester.utils.TestLogger;

import io.restassured.response.Response;

public class ContactCrudApiTests extends BaseApiTest{
    private static final TestLogger logger = TestLogger.getLogger(ContactCrudApiTests.class);
    private String contactId;
    private Map<String, Object> contactPayload = new HashMap<>();
    private Map<String, Object> updateContactPayload = new HashMap<>();
    private String email = TestDataUtil.generateUniqueEmail();
    private String phoneNumber = TestDataUtil.generatePhoneNumber();

    @Test(groups = {"api", "regression", "crud"})
    public void contacte2eFlow() {
        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);

        //Create new Contact
        contactPayload.put("firstName", "William");
        contactPayload.put("lastName", "Byers");
        contactPayload.put("email", email);
        contactPayload.put("phone", phoneNumber);
        contactPayload.put("city", "Hawkins");
        contactPayload.put("state", "Texas");
        contactPayload.put("country", "United States of America");

        Response response = contactClient.addContact(contactPayload);
        contactId = response.jsonPath().getString("_id");

        Assert.assertNotNull(contactId, "Contact Id cannot be Null");

        Assert.assertEquals(response.getStatusCode(), 201,
                    "Expected 201 OK Status Code for Contact creation");

        logger.info("Contact created successfully - Id: %s", contactId);

        //Get Contact
        Response getResponse = contactClient.getContactById(contactId);
        
        Assert.assertEquals(getResponse.getStatusCode(), 200, 
            "Expected 200 OK Status code to retrieve contact details");

        Assert.assertEquals(contactId, getResponse.jsonPath().getString("_id"),
            "Fetched Contact Id mismatch");

        logger.debugResponse("Get Contact", getResponse.asPrettyString());

        //Update contact
        String newEmail = TestDataUtil.generateUniqueEmail();
        String newPhoneNumber = TestDataUtil.generatePhoneNumber();

        updateContactPayload.put("email", newEmail);
        updateContactPayload.put("phone", newPhoneNumber);

        Response updateResponse = contactClient.updateContact(updateContactPayload, contactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 200,
            "Expected 200 OK Status code for contact updation");

        logger.debugResponse("Update Contact", updateResponse.asPrettyString());

        //Delete Contact
        Response deleteResponse = contactClient.deleteContact(contactId);

        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                 "Expected 200 OK Status code for sucessfull contact deletion");

        logger.success("Contact deletion successful for Id: %s", contactId);

        //Post Validation for deletion.
        Response validatedeleteResponse = contactClient.getContactById(contactId);
        contactId = null;

        Assert.assertEquals(validatedeleteResponse.getStatusCode(), 404,
                "Deleted contact should not be retrievable");
        logger.validation("Deleted contact successfully not retrieved (404 response)");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUpContactTestData() {
        if(contactId != null){
            ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);
            Response deleteResponse = contactClient.deleteContact(contactId);

            logger.cleanup("Contact deleted with status code: %s", deleteResponse.getStatusCode());
        }
    }
}