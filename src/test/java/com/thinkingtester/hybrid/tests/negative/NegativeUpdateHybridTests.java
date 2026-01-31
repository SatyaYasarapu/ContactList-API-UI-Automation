package com.thinkingtester.hybrid.tests.negative;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.ApiRequestFactory;
import com.thinkingtester.base.BaseUiTest;
import com.thinkingtester.config.ConfigReader;
import com.thinkingtester.ui.pages.ContactsPage;
import com.thinkingtester.utils.TestDataUtil;

import io.restassured.response.Response;

public class NegativeUpdateHybridTests extends BaseUiTest {

    @Test(groups = { "api", "ui", "regression", "negative" })
    public void invalidContactUpdates() {
        String url = ConfigReader.get("base.ui.url");
        String email = TestDataUtil.generateUniqueEmail();
        String phoneNumber = TestDataUtil.generatePhoneNumber();
        String invalidPhoneNumber = TestDataUtil.generateInvalidPhoneNumber();
        String contactId;

        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        ContactApiClient contactClientWithoutAuth = new ContactApiClient(ApiRequestFactory.noAuthRequest());
        ContactsPage contactsPage = new ContactsPage(page);

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

        System.out.println("API: Contact Created. Contact Id: " + contactId);

        //UI: Verify Contact Exists
        page.navigate(url + "/contactList");

        contactsPage.waitForContactsTableToLoad();
        
        Assert.assertTrue(contactsPage.isContactPresent(email), 
                "Contact created via API should appear in UI");
        System.out.println("UI: Contact Found");

        //API: Invalid Update via API
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("phone", invalidPhoneNumber);

        Response updateResponse = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 400,
                "Expected 400 Bad Request Status code for invalid contact updation");
        System.out.println("API: Contact Updated with invalid data");

        //UI: Verify Contact data is unchanged for invalid Update Payload.
        page.reload();

        String phoneNumberAfterUpdate = contactsPage.getContactPhone(email);

        Assert.assertEquals(phoneNumberAfterUpdate, phoneNumber,
            "Phone number is changed after invalid contact updation");
        System.out.println("UI: No changes detected when an invalid contact ID is provided.");

        //API: Update Contact with Invalid ContactId
        String invalidContactId = "697d930f901e190015c4e49e";
        updatePayload.clear();
        updatePayload.put("phone", TestDataUtil.generatePhoneNumber());

        Response updateResponse2 = contactClient.updateContact(updatePayload, invalidContactId);

        Assert.assertEquals(updateResponse2.getStatusCode(), 404,
                "Expected 404 Not Found Status code");
        System.out.println("API: Update with Invalid ContactId completed");

        //UI: Verify contact data is unchanged for invalid contactId
        page.reload();

        phoneNumberAfterUpdate = contactsPage.getContactPhone(email);

        Assert.assertEquals(phoneNumberAfterUpdate, phoneNumber, 
            "Phone Number is changed after updating with invalid contactId");
        System.out.println("UI: No changes detected when an invalid contact ID is provided.");

        //API: Update contact without Authentication
        updatePayload.clear();
        updatePayload.put("country", "USA");
        Response updateResponse3 = contactClientWithoutAuth.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse3.getStatusCode(), 401,
                "Expected 401 Unauthorized Status code");

        System.out.println("API: Contact Updated. 401 Unauthorized");

        //UI: Verify contact data is unchanged with unauthorized access
        page.reload();

        Assert.assertEquals(contactsPage.getContactCountry(email), "India",
                    "Contact country is changed after unauthorized access");

        System.out.println("UI: No changes detected when an Unauthorized update is done.");
    }
}
