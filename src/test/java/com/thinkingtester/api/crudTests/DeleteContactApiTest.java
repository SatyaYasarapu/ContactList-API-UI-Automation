package com.thinkingtester.api.crudTests;

import org.testng.Assert;
import org.testng.annotations.Test;


import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseApiTest;

import io.restassured.response.Response;

public class DeleteContactApiTest extends BaseApiTest{
    @Test(groups = {"api", "regression"}, dependsOnMethods = {
        "com.thinkingtester.api.tests.AddContactApiTest.addContactWithValidData"
    })
    public void deleteContactById() {
        String contactId = AddContactApiTest.contactId;
        Assert.assertNotNull(contactId, 
            "Contact ID should not be null before GET");

        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);
        
        Response response = contactClient.deleteContact(contactId);

        Assert.assertEquals(response.getStatusCode(), 200,
                 "Expected 200 OK Status code for sucessfull contact deletion");
        
        System.out.println("Contact Deletion successful for Id: " + contactId);

        //Post Validation for deletion.
        Response getResponse = contactClient.getContactById(contactId);

        Assert.assertEquals(getResponse.getStatusCode(), 404,
                "Deleted contact should not be retrievable");

    }
}
