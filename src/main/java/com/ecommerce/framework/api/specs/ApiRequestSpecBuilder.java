package com.ecommerce.framework.api.specs;

import com.ecommerce.framework.config.ConfigReader;
import com.ecommerce.framework.config.FrameworkConstants;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * ApiRequestSpecBuilder — Centralizes Rest Assured RequestSpecification construction.
 *
 * <p>Design Pattern: Builder — assembles complex configuration in a readable way.
 * <p>Eliminates repetition of base URI, headers, and logging across every test method.
 */
public final class ApiRequestSpecBuilder {

    private ApiRequestSpecBuilder() {}

    /**
     * Returns the default request specification for the Book Store API.
     * Configures: base URI, content-type, accepted type, and logging.
     *
     * @return pre-configured RequestSpecification
     */
    public static RequestSpecification getDefaultSpec() {
        String baseUri = ConfigReader.getInstance().getProperty("api.base.url");

        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", FrameworkConstants.ACCEPT_JSON)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Returns an authenticated request specification with a Bearer token.
     *
     * @param token the Bearer authentication token
     * @return authenticated RequestSpecification
     */
    public static RequestSpecification getAuthSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(getDefaultSpec())
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    /**
     * Returns a spec with a custom base URI (for multi-endpoint testing).
     *
     * @param baseUri the full base URI
     * @return RequestSpecification with custom base
     */
    public static RequestSpecification getSpecForUri(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", FrameworkConstants.ACCEPT_JSON)
                .log(LogDetail.ALL)
                .build();
    }
}
