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

public class GetContactApiTest extends BaseApiTest {
    private static final TestLogger logger = TestLogger.getLogger(GetContactApiTest.class);
    protected String contactId;
    protected String email;
    protected String phoneNumber;
    protected Map<String, Object> contactPayload = new HashMap<>();

    @BeforeClass(alwaysRun = true)
    public void setUpContactTestData() {
        email = TestDataUtil.generateUniqueEmail();
        phoneNumber = TestDataUtil.generatePhoneNumber();

       ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);
        contactPayload.put("firstName", "Nikhila");
        contactPayload.put("lastName", "Surabhi");
        contactPayload.put("email", email);
        contactPayload.put("phone", phoneNumber);
        contactPayload.put("city", "Hyderabad");
        contactPayload.put("state", "Telangana");
        contactPayload.put("country", "India");

        Response response = contactClient.addContact(contactPayload);

        Assert.assertEquals(response.getStatusCode(), 201, 
                    "Expected status code 201 for contact creation");

        contactId = response.jsonPath().getString("_id");
        Assert.assertNotNull(contactId,"Contact ID should not be null");

        logger.setup("Contact created successfully - ID: %s", contactId);
    }

    @Test(groups = { "api", "regression" })
    public void getContactById() {
        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);

        Response response = contactClient.getContactById(contactId);

        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected 200 OK when fetching contact");

        Assert.assertEquals(response.jsonPath().get("_id"), contactId,
                "Fetched Contact ID mismatch");
        Assert.assertNotNull(response.jsonPath().getString("firstName"),
                "First Name should not be null");

        logger.info("Email fetched for Contact ID: %s - Email: %s",
                contactId, response.jsonPath().getString("email"));
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
