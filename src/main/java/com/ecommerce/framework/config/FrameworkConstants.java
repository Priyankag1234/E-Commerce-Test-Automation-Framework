package com.ecommerce.framework.config;

/**
 * FrameworkConstants — Central location for all static constant values.
 *
 * <p>Responsibility: Prevent magic strings/numbers scattered throughout the codebase.
 * <p>Design: Utility class — private constructor, all members static final.
 */
public final class FrameworkConstants {

    // Private constructor to prevent instantiation of utility class
    private FrameworkConstants() {
        throw new UnsupportedOperationException("FrameworkConstants is a utility class and cannot be instantiated.");
    }

    // ─────────────────────────────────────────────────────────────
    // Report Configuration
    // ─────────────────────────────────────────────────────────────
    public static final String REPORT_OUTPUT_PATH = System.getProperty("user.dir") + "/test-output/reports/ExtentReport.html";
    public static final String REPORT_TITLE        = "E-Commerce Automation Report";
    public static final String REPORT_NAME         = "Hybrid UI + API Test Suite";

    // ─────────────────────────────────────────────────────────────
    // Screenshot Configuration
    // ─────────────────────────────────────────────────────────────
    public static final String SCREENSHOT_DIR = System.getProperty("user.dir") + "/test-output/screenshots/";

    // ─────────────────────────────────────────────────────────────
    // Test Data Paths (relative to classpath)
    // ─────────────────────────────────────────────────────────────
    public static final String USERS_JSON_PATH    = "testdata/users.json";
    public static final String PRODUCTS_XLSX_PATH = "testdata/products.xlsx";
    public static final String BOOKS_SCHEMA_PATH  = "schemas/books_schema.json";

    // ─────────────────────────────────────────────────────────────
    // Browser Constants
    // ─────────────────────────────────────────────────────────────
    public static final String CHROME  = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String EDGE    = "edge";

    // ─────────────────────────────────────────────────────────────
    // Execution Modes
    // ─────────────────────────────────────────────────────────────
    public static final String LOCAL  = "local";
    public static final String REMOTE = "remote";

    // ─────────────────────────────────────────────────────────────
    // Default Timeout Values (seconds)
    // ─────────────────────────────────────────────────────────────
    public static final int DEFAULT_EXPLICIT_WAIT = 15;
    public static final int DEFAULT_FLUENT_WAIT   = 10;
    public static final int DEFAULT_POLLING_MS    = 500;
    public static final int PAGE_LOAD_TIMEOUT     = 30;

    // ─────────────────────────────────────────────────────────────
    // API Constants
    // ─────────────────────────────────────────────────────────────
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String ACCEPT_JSON       = "application/json";
    public static final int    HTTP_OK           = 200;
    public static final int    HTTP_CREATED      = 201;
    public static final int    HTTP_NO_CONTENT   = 204;
    public static final int    HTTP_BAD_REQUEST  = 400;
    public static final int    HTTP_NOT_FOUND    = 404;

    // ─────────────────────────────────────────────────────────────
    // JUnit 5 Tag Constants
    // ─────────────────────────────────────────────────────────────
    public static final String TAG_SMOKE       = "smoke";
    public static final String TAG_REGRESSION  = "regression";
    public static final String TAG_SANITY      = "sanity";
    public static final String TAG_UI          = "ui";
    public static final String TAG_API         = "api";
    public static final String TAG_E2E         = "e2e";
    public static final String TAG_NEGATIVE    = "negative";
    public static final String TAG_CROSS_BROWSER = "crossbrowser";

    // ─────────────────────────────────────────────────────────────
    // SauceDemo User Constants
    // ─────────────────────────────────────────────────────────────
    public static final String STANDARD_USER = "standard_user";
    public static final String LOCKED_USER   = "locked_out_user";
    public static final String PROBLEM_USER  = "problem_user";
    public static final String DEFAULT_PASSWORD = "secret_sauce";
}
