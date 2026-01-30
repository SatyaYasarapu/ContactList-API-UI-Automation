package com.thinkingtester.api.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseApiTest;

import io.restassured.response.Response;

public class GetAllContactsApiTest extends BaseApiTest {

    @Test(groups = { "api", "regression" })
    public void getAllContactsResponse() {
        ContactApiClient contactClient = new ContactApiClient(baseRequestSpec);

        Response response = contactClient.getAllContacts();
        
        Assert.assertEquals(response.getStatusCode(), 200, 
            "Expected Status code 200 to get all the contacts list.");

        System.out.println(response.asPrettyString());
    }
}
