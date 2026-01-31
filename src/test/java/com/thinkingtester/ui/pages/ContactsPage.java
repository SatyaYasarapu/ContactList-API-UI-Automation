package com.thinkingtester.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ContactsPage {

    private final Page page;

    public ContactsPage(Page page) {
        this.page = page;
    }

    // Locator for the contacts table
    private Locator contactsTable() {
        return page.locator("table");
    }

    private Locator contactRows() {
        return page.locator("tr.contactTableBodyRow");
    }

    // Locator for a contact row by email
    private Locator contactRowByEmail(String email) {
        return page.locator("tr.contactTableBodyRow")
                   .filter(new Locator.FilterOptions().setHasText(email));
    }

    // Verify contacts table is visible (login success proof)
    public boolean isContactsTableVisible() {
        return contactsTable().isVisible();
    }

    public void waitForContactsTableToLoad() {
        contactRows().first().waitFor();
    }

    // Verify contact exists
    public boolean isContactPresent(String email) {
        Locator row = contactRowByEmail(email);
        try {
            row.first().waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Get contact name
    public String getContactName(String email) {
        return contactRowByEmail(email)
                .locator("td")
                .nth(1)
                .innerText();
    }

    // Get contact phone
    public String getContactPhone(String email) {
        return contactRowByEmail(email)
                .locator("td")
                .nth(4)
                .innerText();
    }

    // Get contact country
    public String getContactCountry(String email) {
        return contactRowByEmail(email)
                .locator("td")
                .nth(7)
                .innerText();
    }
}
