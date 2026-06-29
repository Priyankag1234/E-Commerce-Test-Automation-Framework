package com.ecommerce.tests.api;

import com.ecommerce.framework.api.clients.BookStoreApiClient;
import com.ecommerce.framework.api.models.Book;
import com.ecommerce.framework.api.models.BooksResponse;
import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.listeners.TestListener;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * BookStoreApiTest — Validates the DemoQA BookStore REST API.
 *
 * <p>API Base: https://demoqa.com/BookStore/v1
 *
 * <p>Note: No WebDriver needed for API tests — no BaseTest inheritance.
 *    Uses @ExtendWith(TestListener.class) directly for Extent Reports integration.
 */
@ExtendWith(TestListener.class)
@Tag(FrameworkConstants.TAG_API)
@Tag(FrameworkConstants.TAG_REGRESSION)
class BookStoreApiTest {

    private static final Logger log = LogManager.getLogger(BookStoreApiTest.class);
    private static final String VALID_ISBN = "9781449325862"; // Known valid ISBN in the API
    private static final String INVALID_ISBN = "0000000000000";

    private BookStoreApiClient apiClient;

    @BeforeEach
    void setUp() {
        apiClient = new BookStoreApiClient();
    }

    @Test
    @Tag(FrameworkConstants.TAG_SMOKE)
    @DisplayName("GET /Books returns HTTP 200 and non-empty list")
    void shouldReturnAllBooks() {
        BooksResponse response = apiClient.getAllBooks();

        assertThat(response.getBooks())
                .as("Books list should not be null or empty")
                .isNotNull()
                .isNotEmpty();

        assertThat(response.getBookCount())
                .as("Should return at least 1 book")
                .isGreaterThan(0);

        log.info("Total books returned: {}", response.getBookCount());
    }

    @Test
    @DisplayName("GET /Books response matches JSON schema")
    void shouldMatchBooksJsonSchema() {
        apiClient.getAllBooksRaw()
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(FrameworkConstants.BOOKS_SCHEMA_PATH));
    }

    @Test
    @Tag(FrameworkConstants.TAG_SMOKE)
    @DisplayName("GET /Book by valid ISBN returns correct book data")
    void shouldReturnBookByValidIsbn() {
        Book book = apiClient.getBookByIsbn(VALID_ISBN);

        assertThat(book.getIsbn())
                .as("ISBN in response should match the requested ISBN")
                .isEqualTo(VALID_ISBN);

        assertThat(book.getTitle())
                .as("Book title should not be null or blank")
                .isNotBlank();

        assertThat(book.getAuthor())
                .as("Author should not be null or blank")
                .isNotBlank();

        log.info("Book found: {}", book);
    }

    @Test
    @Tag(FrameworkConstants.TAG_NEGATIVE)
    @DisplayName("GET /Book with invalid ISBN returns HTTP 400")
    void shouldReturn400ForInvalidIsbn() {
        Response response = apiClient.getBookByInvalidIsbn(INVALID_ISBN);

        assertThat(response.getStatusCode())
                .as("Invalid ISBN should return HTTP 400 or 404")
                .isIn(400, 404);

        log.info("Response for invalid ISBN {} — Status: {}", INVALID_ISBN, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /Books response time is under 3 seconds")
    void shouldRespondWithinThreeSeconds() {
        long responseTime = apiClient.getAllBooksRaw().getTime();

        assertThat(responseTime)
                .as("API response time should be under 3000ms")
                .isLessThan(3000L);

        log.info("API response time: {}ms", responseTime);
    }

    @Test
    @DisplayName("All books have non-null ISBN, title, and author fields")
    void shouldHaveRequiredFieldsOnAllBooks() {
        BooksResponse response = apiClient.getAllBooks();

        response.getBooks().forEach(book -> {
            assertThat(book.getIsbn())
                    .as("ISBN should not be null for: " + book)
                    .isNotNull();
            assertThat(book.getTitle())
                    .as("Title should not be blank for ISBN: " + book.getIsbn())
                    .isNotBlank();
            assertThat(book.getAuthor())
                    .as("Author should not be blank for ISBN: " + book.getIsbn())
                    .isNotBlank();
        });
    }

    @Test
    @DisplayName("GET /Books returns Content-Type application/json header")
    void shouldReturnJsonContentType() {
        String contentType = apiClient.getAllBooksRaw().getContentType();

        assertThat(contentType)
                .as("Response should be JSON content type")
                .containsIgnoringCase("application/json");
    }
}
