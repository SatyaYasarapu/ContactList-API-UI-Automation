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

public class PartialFailureHybridTests extends BaseUiTest{
    protected String url = ConfigReader.get("base.ui.url");
    protected String email;
    protected String contactId;
    protected String phoneNumber;
    protected String invalidPhoneNumber;

    @BeforeClass(alwaysRun = true)
    public void setupContactTestData() {
        email = TestDataUtil.generateUniqueEmail();
        phoneNumber = TestDataUtil.generatePhoneNumber();

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

    @Test(groups = {"api", "ui", "regression", "partial"})
    public void partialE2EContactsFlow() {
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        ContactsPage contactsPage = new ContactsPage(page);

        String updatedPhoneNumber = TestDataUtil.generatePhoneNumber();

        //API: Valid Update to the contact
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("phone", updatedPhoneNumber);

        Response updateResponse = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 200,
                "Expected 200 OK Status code for contact updation.");
        System.out.println("API: Contact Updated. Updated PhoneNumber is: " + updatedPhoneNumber);

        //UI: Verify Contact Phone Number is changed.
        page.navigate(url + "/contactList");

        Assert.assertNotEquals(phoneNumber, contactsPage.getContactPhone(email),
                    "Actual Phone Number and Updated Phone numbers are same.");
        System.out.println("UI: PhoneNumber updated from "+ phoneNumber +" to new PhoneNumber: " + contactsPage.getContactPhone(email));

        //API: Invalid Update with invalid Payload data.
        invalidPhoneNumber = TestDataUtil.generateInvalidPhoneNumber();
        updatePayload.clear();
        updatePayload.put("phone", invalidPhoneNumber);

        Response updateResponse2 = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse2.getStatusCode(), 400,
                "Expected 400 Bad Request Status code for Invalid Updation.");

        System.out.println("API: Invalid Update Completed.");

        //UI: Verify Invalid Update
        page.reload();

        String invalidPhoneUpdate = contactsPage.getContactPhone(email);
        Assert.assertEquals(updatedPhoneNumber, invalidPhoneUpdate,
                    "Phone Number is changed after invalid Update");

        System.out.println("UI: PhoneNumber remains unchanged: " + contactsPage.getContactPhone(email));
    }

    @AfterClass(alwaysRun = true)
    public void cleanUpContactTestData() {
        if(contactId != null){
            ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());

            Response deleteResponse = contactClient.deleteContact(contactId);
            System.out.println("CLEANUP: Contact deletion status: " + deleteResponse.getStatusCode());
        }
    }
}
