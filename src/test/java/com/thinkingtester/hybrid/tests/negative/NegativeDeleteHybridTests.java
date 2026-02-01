package com.thinkingtester.hybrid.tests.negative;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.api.clients.ContactApiClient;
import com.thinkingtester.base.ApiRequestFactory;
import com.thinkingtester.base.BaseUiTest;
import com.thinkingtester.config.ConfigReader;
import com.thinkingtester.ui.pages.ContactsPage;

import io.restassured.response.Response;

public class NegativeDeleteHybridTests extends BaseUiTest{

    @Test(groups = {"api", "ui", "negative", "regression"}, dependsOnMethods = {
        "com.thinkingtester.hybrid.tests.negative.NegativeUpdateHybridTests.negativeContactUpdates"
    })
    public void negativeContactDeletes() {
        String url = ConfigReader.get("base.ui.url");

        String contactId = NegativeUpdateHybridTests.contactId;
        String invalidContactId = NegativeUpdateHybridTests.invalidContactId;
        String email = NegativeUpdateHybridTests.email;

        ContactApiClient contactClient = new ContactApiClient(ApiRequestFactory.newRequest());
        ContactApiClient contactClientWithoutAuth = new ContactApiClient(ApiRequestFactory.noAuthRequest());
        ContactsPage contactsPage = new ContactsPage(page);

        //API : Delete contact with Invalid Contact ID.
        Response deleteResponse = contactClient.deleteContact(invalidContactId);

        Assert.assertEquals(deleteResponse.getStatusCode(), 404,
            "Expected 404 Not Found Status code for invalid contact Id");

        System.out.println("API: Contact deleted. Invalid contactId");

        //UI: Verify that contact is not deleted for Invalid contactId.
        page.navigate(url + "/contactList");

        Assert.assertTrue(contactsPage.isContactPresent(email), 
                "Unfortunately Contact is deleted when Invalid contact Id is passed");

        System.out.println("UI: Contact Found upon invalid deletion." + email);

        //API: Delete Contact without Authentication
        Response deleteResponse2 = contactClientWithoutAuth.deleteContact(contactId);

        Assert.assertEquals(deleteResponse2.getStatusCode(), 401,
                    "Expected 401 Unauthorized Status code");
        System.out.println("API: Unauthorized delete completed");

        //UI: Verify contact is not deleted upon Unauthorized delete action.
        page.reload();

        Assert.assertTrue(contactsPage.isContactPresent(email), 
                "Contact is deleted upon Unauthorized access.");
        System.out.println("UI: Contact Found on Unauthorized deletion.");
    }
    
}
