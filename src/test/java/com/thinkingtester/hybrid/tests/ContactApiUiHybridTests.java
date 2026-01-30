package com.thinkingtester.hybrid.tests;

import java.util.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseUiTest;
import com.thinkingtester.config.ConfigReader;
import com.thinkingtester.ui.pages.ContactsPage;

import io.restassured.response.Response;

import com.thinkingtester.base.ApiRequestFactory;

public class ContactApiUiHybridTests extends BaseUiTest{
    
    @Test(groups = {"api", "ui", "hybrid", "e2e"})
    public void contactCrudHybridFlow() {
        String url = ConfigReader.get("base.ui.url");

        //API: Create contact
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        Map<String, Object> contactPayload = new HashMap<>();

        contactPayload.put("firstName", "Anirudh");
        contactPayload.put("lastName", "Kalvala");
        contactPayload.put("email", "akalvala@test.com");
        contactPayload.put("phone", "9000000000");
        contactPayload.put("city", "Hyderabad");
        contactPayload.put("country", "India");

        Response createResponse = contactClient.addContact(contactPayload);

        Assert.assertEquals(createResponse.getStatusCode(), 201,
                "Expected 200 OK status code for contact creation");
        
        String contactId = createResponse.jsonPath().getString("_id");
        Assert.assertNotNull(contactId, "Contact ID should not be null");

        String email = createResponse.jsonPath().getString("email");
        System.out.println("Fetched Email is: "+ email);

        Assert.assertNotNull(email, "Email should not be null");

        System.out.println("API: Contact created ->" + contactId);

        //UI: Verify Contact Exists
        page.navigate(url + "/contactList");

        ContactsPage contactPage = new ContactsPage(page);
        contactPage.waitForContactsTableToLoad();

        Assert.assertTrue(contactPage.isContactPresent(email),
                "Contact created via API should appear in UI");

        System.out.println("UI: Contact Found: " + email);

        //API: Update contact details
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("phone", "9111111111");

        Response updateResponse = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse.statusCode(), 200,
                "Expected 200 OK Status code for updating contact successfully!");
        
        System.out.println("API: Contact Updated.");

        //UI: Verify email is updated.
        page.reload();

        String updatedPhone = contactPage.getContactPhone(email);

        Assert.assertNotEquals(createResponse.jsonPath().getString("phone"), updatedPhone,
                "Updated phone number not reflected in UI");
        System.out.println("UI: Update reflected: " + updatedPhone);

        //API: Delete Contact
        Response deleteResponse = contactClient.deleteContact(contactId);

        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                "Expected 200 OK Status code for contact deletion.");
        System.out.println("API: Contact Deleted");

        //UI: Verify delete
        page.reload();

        Assert.assertFalse(contactPage.isContactPresent(email),
            "Deleted contact should not appear in UI");

        System.out.println("UI: Deletion reflected");
    }
}
