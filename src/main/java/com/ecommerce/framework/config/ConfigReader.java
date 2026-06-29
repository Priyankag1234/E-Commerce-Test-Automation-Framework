package com.ecommerce.framework.config;

import com.ecommerce.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader — Singleton utility for reading environment-specific properties.
 *
 * <p>Design Pattern: Singleton (thread-safe via static holder / initialization-on-demand)
 * <p>Responsibility: Load and expose configuration properties from the
 *    appropriate environment file (qa, uat, prod) based on the JVM argument -Denv.
 *
 * <p>Usage: ConfigReader.getInstance().getProperty("app.base.url")
 */
public final class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private final Properties properties;

    // ─────────────────────────────────────────────────────────────
    // Thread-safe Initialization-on-Demand Holder
    // ─────────────────────────────────────────────────────────────

    private static final class Holder {
        private static final ConfigReader INSTANCE = new ConfigReader();
    }

    /**
     * Private constructor — loads properties on first access.
     */
    private ConfigReader() {
        properties = new Properties();
        String env = resolveEnvironment();
        String configFile = "config/" + env + ".properties";
        log.info("Loading configuration for environment: [{}] from file: [{}]", env, configFile);
        loadProperties(configFile);
    }

    /**
     * Returns the singleton instance (lazy, thread-safe).
     *
     * @return ConfigReader instance
     */
    public static ConfigReader getInstance() {
        return Holder.INSTANCE;
    }

    // ─────────────────────────────────────────────────────────────
    // Public Property Accessors
    // ─────────────────────────────────────────────────────────────

    /**
     * Retrieves a required property value.
     * Throws FrameworkException if the key is not found.
     *
     * @param key the property key
     * @return the property value
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new FrameworkException("Property key not found in configuration: [" + key + "]");
        }
        return value.trim();
    }

    /**
     * Retrieves an optional property value with a default fallback.
     *
     * @param key          the property key
     * @param defaultValue fallback value if key is absent
     * @return the property value or default
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }

    /**
     * Retrieves a property value as an integer.
     *
     * @param key the property key
     * @return the integer value
     */
    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    /**
     * Retrieves a property value as a boolean.
     *
     * @param key the property key
     * @return the boolean value
     */
    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    // ─────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────

    /**
     * Resolves the active environment from:
     * 1. JVM system property: -Denv=qa
     * 2. Environment variable: ENV=qa
     * 3. Default fallback: qa
     */
    private String resolveEnvironment() {
        // Priority 1: JVM argument (-Denv=qa)
        String env = System.getProperty("env");
        if (env != null && !env.isBlank()) {
            log.debug("Environment resolved from JVM property: {}", env);
            return env.toLowerCase();
        }

        // Priority 2: OS environment variable (ENV=qa)
        env = System.getenv("ENV");
        if (env != null && !env.isBlank()) {
            log.debug("Environment resolved from OS environment variable: {}", env);
            return env.toLowerCase();
        }

        // Priority 3: Default
        log.warn("No environment specified. Defaulting to [qa]. Use -Denv=<env> to override.");
        return "qa";
    }

    /**
     * Loads properties from the given classpath resource path.
     *
     * @param resourcePath path relative to src/main/resources
     */
    private void loadProperties(String resourcePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new FrameworkException(
                        "Configuration file not found on classpath: [" + resourcePath + "]. " +
                        "Ensure the file exists under src/main/resources/" + resourcePath
                );
            }
            properties.load(inputStream);
            log.info("Successfully loaded {} properties from [{}]", properties.size(), resourcePath);
        } catch (IOException e) {
            throw new FrameworkException("Failed to load configuration file: [" + resourcePath + "]", e);
        }
    }
}
