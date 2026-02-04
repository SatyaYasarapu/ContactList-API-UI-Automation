package com.thinkingtester.api.crudTests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.ApiRequestFactory;
import com.thinkingtester.base.BaseApiTest;
import com.thinkingtester.utils.TestDataUtil;
import com.thinkingtester.utils.TestLogger;

import io.restassured.response.Response;

public class AddContactApiTest extends BaseApiTest {
    private static final TestLogger logger = TestLogger.getLogger(AddContactApiTest.class);
    protected String contactId;
    protected String email;
    protected String phoneNumber;
    protected Map<String, Object> contactPayload = new HashMap<>();

    @Test(groups = {"api", "regression"})
    public void addContactWithValidData() {
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

        Assert.assertNotNull(contactId, "Contact ID should not be null");

        logger.info("Contact created successfully - ID: %s", contactId);

        logger.debugResponse("Add Contact", response.asPrettyString());
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
