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

public class DeleteContactApiTest extends BaseApiTest{
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

        System.out.println("Contact created successfully. ID = " + contactId);
        System.out.println("Contact reponse before Update is: " + createResponse.asPrettyString());
    }
    @Test(groups = {"api", "regression"})
    public void deleteContactById() {
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        
        Response deleteResponse = contactClient.deleteContact(contactId);
        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                 "Expected 200 OK Status code for sucessfull contact deletion");
        
        System.out.println("Contact Deletion successful for Id: " + contactId);

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
            System.out.println("CLEANUP: Deleted contact status code: " + deleteResponse.getStatusCode());
        }
    }
}
