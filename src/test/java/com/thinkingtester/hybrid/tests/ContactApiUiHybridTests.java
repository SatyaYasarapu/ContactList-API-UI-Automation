package com.thinkingtester.hybrid.tests;

import java.util.*;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.BaseUiTest;
import com.thinkingtester.config.ConfigReader;
import com.thinkingtester.ui.pages.ContactsPage;
import com.thinkingtester.utils.TestDataUtil;

import io.restassured.response.Response;

import com.thinkingtester.base.ApiRequestFactory;

public class ContactApiUiHybridTests extends BaseUiTest{
    protected String url = ConfigReader.get("base.ui.url");
    protected String email;
    protected String phone;
    protected String contactId;
    protected Map<String, Object> contactPayload;

    @BeforeClass(alwaysRun = true)
    public void setupContactTestData() {
        url = ConfigReader.get("base.ui.url");
        email = TestDataUtil.generateUniqueEmail();
        phone = TestDataUtil.generatePhoneNumber();

        contactPayload = new HashMap<>();

        contactPayload.put("firstName", "Anirudh");
        contactPayload.put("lastName", "Kalvala");
        contactPayload.put("email", email);
        contactPayload.put("phone", phone);
        contactPayload.put("city", "Hyderabad");
        contactPayload.put("country", "India");
    }
    
    @Test(groups = {"api", "ui", "hybrid", "e2e"})
    public void contactCrudHybridFlow() {
        //API: Create contact
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        Response createResponse = contactClient.addContact(contactPayload);

        Assert.assertEquals(createResponse.getStatusCode(), 201,
                "Expected 201 OK status code for contact creation");
        
        contactId = createResponse.jsonPath().getString("_id");
        Assert.assertNotNull(contactId, "Contact ID should not be null");

        String emailFromResponse = createResponse.jsonPath().getString("email");
        System.out.println("Fetched Email is: "+ emailFromResponse);

        Assert.assertNotNull(emailFromResponse, "Email should not be null");

        Assert.assertEquals(email, emailFromResponse,
                "Email added and Fetched email from Response does not match");

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
        String newPhoneNumber = TestDataUtil.generatePhoneNumber();
        updatePayload.put("phone", newPhoneNumber);

        Response updateResponse = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse.statusCode(), 200,
                "Expected 200 OK Status code for updating contact successfully!");
        
        System.out.println("API: Contact Updated. New PhoneNumber: " + newPhoneNumber);

        //UI: Verify email is updated.
        page.reload();
        String updatedPhone = contactPage.getContactPhone(email);

        Assert.assertEquals(updatedPhone, newPhoneNumber,
                "Updated phone number not reflected correctly in UI");

        System.out.println("UI: Update reflected: " + updatedPhone);

        //API: Delete Contact
        Response deleteResponse = contactClient.deleteContact(contactId);
        contactId = null;

        Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                "Expected 200 OK Status code for contact deletion.");
        System.out.println("API: Contact Deleted");

        //UI: Verify delete
        page.reload();

        Assert.assertFalse(contactPage.isContactPresent(email),
            "Deleted contact should not appear in UI");

        System.out.println("UI: Deletion reflected");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUpContactTestData() {
            if (contactId != null) {
                    ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
                    Response deleteResponse = contactClient.deleteContact(contactId);

                    System.out.println("CLEANUP API: Contact Deleted Status code: " + deleteResponse.getStatusCode());
            }
    }
}
