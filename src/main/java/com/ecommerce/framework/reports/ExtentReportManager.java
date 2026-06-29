package com.ecommerce.framework.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.ecommerce.framework.config.ConfigReader;
import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.utils.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * ExtentReportManager — Singleton manager for the Extent Reports instance.
 *
 * <p>Design Pattern: Singleton (thread-safe via synchronized lazy initialization)
 * <p>Responsibility:
 * <ul>
 *   <li>Initialize the ExtentReports with configured reporter (SparkReporter)</li>
 *   <li>Attach system environment metadata</li>
 *   <li>Provide the shared ExtentReports instance to ExtentTestManager</li>
 *   <li>Flush reports to disk when all tests complete</li>
 * </ul>
 */
public final class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;

    private ExtentReportManager() {}

    /**
     * Returns the shared ExtentReports instance.
     * Initializes it on first call (lazy singleton).
     *
     * @return the ExtentReports instance
     */
    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            extentReports = createInstance();
        }
        return extentReports;
    }

    /**
     * Flushes (writes) all collected test data to the HTML report file.
     * MUST be called once after all tests complete (in a JUnit 5 extension or suite teardown).
     */
    public static synchronized void flushReports() {
        if (extentReports != null) {
            log.info("Flushing Extent Report to disk...");
            extentReports.flush();
            log.info("Extent Report generated at: {}", FrameworkConstants.REPORT_OUTPUT_PATH);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Private Init
    // ─────────────────────────────────────────────────────────────

    private static ExtentReports createInstance() {
        // Ensure the output directory exists
        File reportFile = new File(FrameworkConstants.REPORT_OUTPUT_PATH);
        reportFile.getParentFile().mkdirs();

        // Configure the Spark (HTML) Reporter
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFile);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle(FrameworkConstants.REPORT_TITLE);
        sparkReporter.config().setReportName(FrameworkConstants.REPORT_NAME);
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        sparkReporter.config().setCss(buildCustomCss());

        // Create ExtentReports and attach the reporter
        ExtentReports reports = new ExtentReports();
        reports.attachReporter(sparkReporter);

        // Attach system environment information
        ConfigReader config = ConfigReader.getInstance();
        reports.setSystemInfo("OS", System.getProperty("os.name"));
        reports.setSystemInfo("Java Version", System.getProperty("java.version"));
        reports.setSystemInfo("Environment", System.getProperty("env", "qa"));
        reports.setSystemInfo("Browser", System.getProperty("browser", config.getProperty("browser", "chrome")));
        reports.setSystemInfo("Execution Mode", System.getProperty("execution", config.getProperty("execution.mode", "local")));
        reports.setSystemInfo("Base URL", config.getProperty("app.base.url"));
        reports.setSystemInfo("Execution Time", DateUtils.getReportTimestamp());
        reports.setSystemInfo("Author", "QA Automation Team");

        log.info("ExtentReports initialized. Report will be saved to: {}", reportFile.getAbsolutePath());
        return reports;
    }

    /** Minimal custom CSS to improve readability. */
    private static String buildCustomCss() {
        return ".badge-primary { background-color: #6366f1; } " +
               ".report-name { font-size: 1.5rem; font-weight: bold; }";
    }
}
