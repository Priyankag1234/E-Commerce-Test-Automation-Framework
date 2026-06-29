package com.ecommerce.framework.api.specs;

import com.ecommerce.framework.config.FrameworkConstants;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

/**
 * ApiResponseSpecBuilder — Centralizes Rest Assured ResponseSpecification construction.
 *
 * <p>Defines standard response expectations to avoid repetitive assertion boilerplate.
 */
public final class ApiResponseSpecBuilder {

    private ApiResponseSpecBuilder() {}

    /**
     * Returns the default success response spec: HTTP 200, JSON content type.
     *
     * @return pre-configured ResponseSpecification
     */
    public static ResponseSpecification getSuccessSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(FrameworkConstants.HTTP_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Returns a response spec expecting HTTP 201 Created.
     *
     * @return ResponseSpecification for 201
     */
    public static ResponseSpecification getCreatedSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(FrameworkConstants.HTTP_CREATED)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Returns a response spec expecting HTTP 404 Not Found.
     *
     * @return ResponseSpecification for 404
     */
    public static ResponseSpecification getNotFoundSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(FrameworkConstants.HTTP_NOT_FOUND)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Returns a response spec expecting a specific status code.
     *
     * @param statusCode expected HTTP status code
     * @return ResponseSpecification for the given code
     */
    public static ResponseSpecification getSpecForStatus(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(LogDetail.ALL)
                .build();
    }
}
