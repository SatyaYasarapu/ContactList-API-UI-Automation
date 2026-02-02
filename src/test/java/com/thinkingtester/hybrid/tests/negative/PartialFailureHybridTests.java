package com.thinkingtester.hybrid.tests.negative;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.ApiRequestFactory;
import com.thinkingtester.base.BaseUiTest;
import com.thinkingtester.ui.pages.ContactsPage;
import com.thinkingtester.utils.TestDataUtil;

import io.restassured.response.Response;

public class PartialFailureHybridTests extends BaseUiTest{
    @Test(groups = {"api", "ui", "regression", "partial"}, dependsOnMethods = 
        "com.thinkingtester.hybrid.tests.negative.NegativeUpdateHybridTests.negativeContactUpdates"
    )
    public void partialE2EContactsFlow() {
        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        ContactsPage contactsPage = new ContactsPage(page);

        String contactId = NegativeUpdateHybridTests.contactId;
        String actualPhoneNumber = NegativeUpdateHybridTests.phoneNumber;
        String email = NegativeUpdateHybridTests.email;
        Assert.assertNotNull(contactId, "Contact Id cannot be null");
        String updatedPhoneNumber = TestDataUtil.generatePhoneNumber();

        //API: Valid and Invalid Update to the contact
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("phone", updatedPhoneNumber);

        Response updateResponse = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse.getStatusCode(), 200,
                "Expected 200 OK Status code for contact updation.");
        System.out.println("API: Contact Updated. PhoneNumber: " + updatedPhoneNumber);

        //UI: Verify Contact Phone Number is changed.
        page.navigate(url + "/contactList");

        Assert.assertNotEquals(actualPhoneNumber, contactsPage.getContactPhone(email),
                    "Actual Phone Number and Updated Phone numbers are same.");
        System.out.println("UI: Phone Number Update Verification is complete: " + contactsPage.getContactPhone(email));

        //API: Invalid Update 
        updatePayload.clear();
        updatePayload.put("phone", "sfsdfs");

        Response updateResponse2 = contactClient.updateContact(updatePayload, contactId);

        Assert.assertEquals(updateResponse2.getStatusCode(), 400,
                "Expected 400 Bad Request Status code for Invalid Updation.");

        System.out.println("API: Invalid Update Completed.");

        //UI: Verify Invalid Update
        page.reload();

        String invalidPhoneUpdate = contactsPage.getContactPhone(email);

        Assert.assertEquals(updatedPhoneNumber, invalidPhoneUpdate,
                    "Phone Number is changed after invalid Update");
        System.out.println("UI: Invalid Phone Update Verification is completed.");
    }
}
