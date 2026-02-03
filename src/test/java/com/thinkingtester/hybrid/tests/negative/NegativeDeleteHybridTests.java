package com.thinkingtester.hybrid.tests.negative;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.ApiRequestFactory;
import com.thinkingtester.base.BaseUiTest;
import com.thinkingtester.config.ConfigReader;
import com.thinkingtester.ui.pages.ContactsPage;
import com.thinkingtester.utils.TestDataUtil;

import io.restassured.response.Response;

public class NegativeDeleteHybridTests extends BaseUiTest{
    protected String url = ConfigReader.get("base.ui.url");
    protected String email;
    protected String phoneNumber;
    protected String invalidContactId;
    protected String contactId;

    @BeforeClass(alwaysRun = true)
    public void setupContactTestData() {
        email = TestDataUtil.generateUniqueEmail();
        phoneNumber = TestDataUtil.generatePhoneNumber();
        invalidContactId = "697d930f901e190015c4e49e";

        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());

        // API: Create Contact
        Map<String, Object> contactPayload = new HashMap<>();
        contactPayload.put("firstName", "Hyndavi");
        contactPayload.put("lastName", "Yasarapu");
        contactPayload.put("email", email);
        contactPayload.put("phone", phoneNumber);
        contactPayload.put("country", "India");
        contactPayload.put("state", "Telangana");
        contactPayload.put("city", "Hyderabad");

        Response createResponse = contactClient.addContact(contactPayload);
        Assert.assertEquals(createResponse.getStatusCode(), 201,
                "Expected 201 OK Status code contact creation");

        contactId = createResponse.jsonPath().getString("_id");
        Assert.assertNotNull(contactId, "Contact Id should not be null");

        System.out.println("SETUP API: Contact created with ID: " + contactId);
    }

    @Test(groups = {"hybrid", "regression"})
    public void negativeContactDeletes() {
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        ContactApiClient contactClientWithoutAuth = new ContactApiClient(ApiRequestFactory.noAuthRequest());
        ContactsPage contactsPage = new ContactsPage(page);

        //API : Delete contact with Invalid Contact ID.
        Response deleteResponse = contactClient.deleteContact(invalidContactId);

        Assert.assertEquals(deleteResponse.getStatusCode(), 404,
            "Expected 404 Not Found Status code for invalid contact Id");

        System.out.println("API: Contact deletion failed for Invalid contactId");

        //UI: Verify that contact is not deleted for Invalid contactId.
        page.navigate(url + "/contactList");

        Assert.assertTrue(contactsPage.isContactPresent(email), 
                "Unfortunately Contact is deleted when Invalid contact Id is passed");

        System.out.println("UI: Contact Found upon invalid deletion." + email);

        //API: Delete Contact without Authentication
        Response deleteResponse2 = contactClientWithoutAuth.deleteContact(contactId);

        Assert.assertEquals(deleteResponse2.getStatusCode(), 401,
                    "Expected 401 Unauthorized Status code");
        System.out.println("API: Contact deletion failed successfully for Unauthorized access.");

        //UI: Verify contact is not deleted upon Unauthorized delete action.
        page.reload();

        Assert.assertTrue(contactsPage.isContactPresent(email), 
                "Contact is deleted upon Unauthorized access.");
        System.out.println("UI: Contact Found on Unauthorized deletion");
    }
    
    @AfterClass(alwaysRun = true)
    public void cleanUpContactTestData() {
        if(contactId != null) {
            ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());

            Response deleteResponse = contactClient.deleteContact(contactId);
            Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                        "Expected 200 OK Status code for Contact Deletion");
            System.out.println("CLEANUP: Contact deletion status: " + deleteResponse.getStatusCode());
        }
    }
}
