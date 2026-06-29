package com.ecommerce.framework.api.clients;

import com.ecommerce.framework.api.models.Book;
import com.ecommerce.framework.api.models.BooksResponse;
import com.ecommerce.framework.api.specs.ApiRequestSpecBuilder;
import com.ecommerce.framework.api.specs.ApiResponseSpecBuilder;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

/**
 * BookStoreApiClient — Reusable API client for the DemoQA BookStore REST API.
 *
 * <p>Wraps raw Rest Assured calls into business-readable methods.
 * <p>Base URL: https://demoqa.com/BookStore/v1
 *
 * <p>Available endpoints:
 * <ul>
 *   <li>GET /Books — list all books</li>
 *   <li>GET /Book?ISBN={isbn} — get a single book</li>
 * </ul>
 */
public class BookStoreApiClient {

    private static final Logger log = LogManager.getLogger(BookStoreApiClient.class);
    private static final String BOOKS_PATH    = "/BookStore/v1/Books";
    private static final String BOOK_PATH     = "/BookStore/v1/Book";

    // ─────────────────────────────────────────────────────────────
    // GET Requests
    // ─────────────────────────────────────────────────────────────

    /**
     * Fetches all books from the BookStore API.
     * Validates: HTTP 200, JSON content type.
     *
     * @return deserialized BooksResponse
     */
    public BooksResponse getAllBooks() {
        log.info("API Request: GET {}", BOOKS_PATH);

        Response response = given()
                .spec(ApiRequestSpecBuilder.getDefaultSpec())
                .when()
                .get(BOOKS_PATH)
                .then()
                .spec(ApiResponseSpecBuilder.getSuccessSpec())
                .extract()
                .response();

        log.info("GET /Books returned {} items", response.jsonPath().getList("books").size());
        return response.as(BooksResponse.class);
    }

    /**
     * Fetches a single book by its ISBN.
     * Validates: HTTP 200, JSON content type.
     *
     * @param isbn the ISBN to query
     * @return deserialized Book object
     */
    public Book getBookByIsbn(String isbn) {
        log.info("API Request: GET {}?ISBN={}", BOOK_PATH, isbn);

        Response response = given()
                .spec(ApiRequestSpecBuilder.getDefaultSpec())
                .queryParam("ISBN", isbn)
                .when()
                .get(BOOK_PATH)
                .then()
                .spec(ApiResponseSpecBuilder.getSuccessSpec())
                .extract()
                .response();

        return response.as(Book.class);
    }

    /**
     * Requests a book by an invalid ISBN.
     * Designed for negative testing — does NOT validate status code.
     * Caller asserts the expected status.
     *
     * @param invalidIsbn the invalid ISBN string
     * @return raw Response object for assertion
     */
    public Response getBookByInvalidIsbn(String invalidIsbn) {
        log.info("API Request (negative): GET {}?ISBN={}", BOOK_PATH, invalidIsbn);

        return given()
                .spec(ApiRequestSpecBuilder.getDefaultSpec())
                .queryParam("ISBN", invalidIsbn)
                .when()
                .get(BOOK_PATH)
                .then()
                .extract()
                .response();
    }

    /**
     * Fetches all books and returns the raw Response.
     * Use when direct response assertion is needed (status code, timing, etc.)
     *
     * @return raw Rest Assured Response
     */
    public Response getAllBooksRaw() {
        log.info("API Request (raw): GET {}", BOOKS_PATH);

        return given()
                .spec(ApiRequestSpecBuilder.getDefaultSpec())
                .when()
                .get(BOOKS_PATH)
                .then()
                .extract()
                .response();
    }
}
