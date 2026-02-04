package com.thinkingtester.api.crudTests;

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
import com.thinkingtester.utils.TestLogger;

import io.restassured.response.Response;

public class DeleteContactApiTest extends BaseApiTest{
    private static final TestLogger logger = TestLogger.getLogger(DeleteContactApiTest.class);
    protected String contactId;
    protected String email;
    protected String phoneNumber;
    protected Map<String, Object> contactPayload = new HashMap<>();

    @BeforeClass(alwaysRun = true)
    public void setUpContactTestData() {
        email = TestDataUtil.generateUniqueEmail();
        phoneNumber = TestDataUtil.generatePhoneNumber();

        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        contactPayload.put("firstName", "Nikhila");
        contactPayload.put("lastName", "Surabhi");
        contactPayload.put("email", email);
        contactPayload.put("phone", phoneNumber);
        contactPayload.put("city", "Hyderabad");
        contactPayload.put("state", "Telangana");
        contactPayload.put("country", "India");

        Response createResponse = contactClient.addContact(contactPayload);

        Assert.assertEquals(createResponse.getStatusCode(), 201, 
                    "Expected status code 201 for contact creation");

        contactId = createResponse.jsonPath().getString("_id");
        Assert.assertNotNull(contactId,"Contact ID should not be null");

        logger.setup("Contact created successfully - ID: %s", contactId);
        logger.debugResponse("Create Contact (Setup)", createResponse.asPrettyString());
    }
    @Test(groups = {"api", "regression"})
    public void deleteContactById() {
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        
        Response deleteResponse = contactClient.deleteContact(contactId);
        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                 "Expected 200 OK Status code for sucessfull contact deletion");

        logger.success("Contact deletion successful for Id: %s", contactId);

        //Post Validation for deletion.
        Response getResponse = contactClient.getContactById(contactId);
        contactId = null;

        Assert.assertEquals(getResponse.getStatusCode(), 404,
                "Deleted contact should not be retrievable");

    }
    
    @AfterClass(alwaysRun = true)
    public void cleanupContactTestData() {
        if(contactId != null){
            ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());

            Response deleteResponse = contactClient.deleteContact(contactId);
            logger.cleanup("Deleted contact - Status code: %s", deleteResponse.getStatusCode());
        }
    }
}
