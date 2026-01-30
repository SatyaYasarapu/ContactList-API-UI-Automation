package com.thinkingtester.base;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import com.thinkingtester.config.ConfigReader;
import com.thinkingtester.utils.AuthTokenManager;

public abstract class BaseUiTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected static final String url = ConfigReader.get("base.ui.url");

    @BeforeClass(alwaysRun = true)
    public void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                .setArgs(java.util.List.of("--start-maximized"))
        );
    }

    @BeforeMethod(alwaysRun = true)
    public void createAuthenticatedContext() {

        context = browser.newContext(new Browser.NewContextOptions()
                        .setViewportSize(null));
        addAuthCookie(context);

        page = context.newPage();
    }

    @AfterMethod(alwaysRun = true)
    public void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    //Helper methods
    private void addAuthCookie(BrowserContext context) {

        String token = AuthTokenManager.getToken();

        Cookie authCookie = new Cookie("token", token)
                .setDomain("thinking-tester-contact-list.herokuapp.com")
                .setPath("/");

        context.addCookies(List.of(authCookie));
    }
}
