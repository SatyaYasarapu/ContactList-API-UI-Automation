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

import io.restassured.response.Response;

public class GetContactApiTest extends BaseApiTest {
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

        System.out.println("Contact created successfully. ID = " + contactId);
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

        System.out.println("Email fetched for Contact ID: "
                + response.jsonPath().getString("email"));
    }
    
    @AfterClass(alwaysRun = true)
    public void cleanupContactTestData() {
        if(contactId != null){
            ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        
            Response deleteResponse = contactClient.deleteContact(contactId);
            System.out.println("CLEANUP: Deleted contact status code: " + deleteResponse.getStatusCode());
        }
    }
}
