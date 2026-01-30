package com.thinkingtester.ui.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thinkingtester.base.BaseUiTest;
import com.thinkingtester.ui.pages.ContactsPage;

public class AuthBypassTest extends BaseUiTest {

    @Test(groups = { "ui", "regression" })
    public void loginwithoutUI() {
        System.out.println("Current URL: " + page.url());
        System.out.println("Page Title: " + page.title());

        ContactsPage contactsPage = new ContactsPage(page);

        String email = "njhiloria@test.com";

        Assert.assertTrue(
                contactsPage.isContactPresent(email),
                "Expected contact with email '" + email + "' to be present in Contacts table"
        );

        System.out.println("Cookie-based auth successful. Contact found: " + email);
    }
}
