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

public class UpdateContactApiTest extends BaseApiTest{
    protected String contactId;
    protected String email;
    protected String phoneNumber;
    protected String updatedPhoneNumber;
    protected Map<String, Object> contactPayload = new HashMap<>();
    protected Map<String, Object> updatePayload = new HashMap<>();

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

        System.out.println("Contact created successfully. ID = " + contactId);
        System.out.println("Contact reponse before Update is: " + createResponse.asPrettyString());
    }

    @Test(groups = {"api", "regression"})
    public void updateContactByID() {
        updatedPhoneNumber = TestDataUtil.generatePhoneNumber();
        updatePayload.put("phone", updatedPhoneNumber);
        
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());

        Response updateResponse = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 200, 
                    "Expected 200 OK Status code for Contact Updation");

        Assert.assertEquals(updateResponse.jsonPath().get("_id"), contactId,
                        "Fetched Contact ID mismatch");
        
        System.out.println("Contact reponse after Update is: " + updateResponse.asPrettyString());
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
