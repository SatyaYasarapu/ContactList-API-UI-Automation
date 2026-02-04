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
import com.thinkingtester.utils.TestLogger;

import io.restassured.response.Response;

public class NegativeUpdateHybridTests extends BaseUiTest {
    private static final TestLogger logger = TestLogger.getHybridLogger(NegativeUpdateHybridTests.class);
    protected String contactId;
    protected String email;
    protected String invalidContactId;
    protected String phoneNumber;
    protected String invalidPhoneNumber;
    protected String url = ConfigReader.get("base.ui.url");

    @BeforeClass(alwaysRun = true)
    public void setupContactTestData() {
        email = TestDataUtil.generateUniqueEmail();
        phoneNumber = TestDataUtil.generatePhoneNumber();
        invalidPhoneNumber = TestDataUtil.generateInvalidPhoneNumber();

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

        logger.setup("API: Contact created with ID: %s", contactId);
    }

    @Test(groups = {"hybrid", "regression"})
    public void negativeContactUpdates() {
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        ContactApiClient contactClientWithoutAuth = new ContactApiClient(ApiRequestFactory.noAuthRequest());
        ContactsPage contactsPage = new ContactsPage(page);

        //UI: Verify Contact Exists
        page.navigate(url + "/contactList");

        contactsPage.waitForContactsTableToLoad();
        Assert.assertTrue(contactsPage.isContactPresent(email),
                "Contact created via API should appear in UI");
        logger.ui("Contact found in UI");

        //API: Invalid Update via API
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("phone", invalidPhoneNumber);

        Response updateResponse = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 400,
                "Expected 400 Bad Request Status code for invalid contact updation");
        logger.api("Contact update attempted with invalid data (400 Bad Request)");

        //UI: Verify Contact data is unchanged for invalid Update Payload.
        page.reload();

        String phoneNumberAfterUpdate = contactsPage.getContactPhone(email);

        Assert.assertEquals(phoneNumberAfterUpdate, phoneNumber,
            "Phone number is changed after invalid contact updation");
        logger.ui("No changes detected - Invalid email format rejected");

        //API: Update Contact with Invalid ContactId
        invalidContactId = "697d930f901e190015c4e49e";
        updatePayload.clear();
        updatePayload.put("phone", TestDataUtil.generatePhoneNumber());

        Response updateResponse2 = contactClient.updateContact(updatePayload, invalidContactId);

        Assert.assertEquals(updateResponse2.getStatusCode(), 404,
                "Expected 404 Not Found Status code");
        logger.api("Update with invalid ContactId completed (404 Not Found)");

        //UI: Verify contact data is unchanged for invalid contactId
        page.reload();

        phoneNumberAfterUpdate = contactsPage.getContactPhone(email);

        Assert.assertEquals(phoneNumberAfterUpdate, phoneNumber,
            "Phone Number is changed after updating with invalid contactId");
        logger.ui("No changes detected - Invalid ContactId");

        //API: Update contact without Authentication
        updatePayload.clear();
        updatePayload.put("country", "USA");
        Response updateResponse3 = contactClientWithoutAuth.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse3.getStatusCode(), 401,
                "Expected 401 Unauthorized Status code");

        logger.api("Contact update failed - Unauthorized (401)");

        //UI: Verify contact data is unchanged with unauthorized access
        page.reload();

        Assert.assertEquals(contactsPage.getContactCountry(email), "India",
                    "Contact country is changed after unauthorized access");

        logger.ui("No changes detected - Unauthorized update rejected");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUpContactTestData() {
            if (contactId != null) {
                    ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
                    Response deleteResponse = contactClient.deleteContact(contactId);

                    Assert.assertEquals(deleteResponse.getStatusCode(), 200,
                                    "Expected 200 OK Status code for Contact deletion");
                    logger.cleanup("Contact deletion status: %s", deleteResponse.getStatusCode());
            }
    }
    
}
