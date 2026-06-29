package com.ecommerce.framework.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * BooksResponse — POJO wrapping the DemoQA BookStore API GET /Books response.
 *
 * <p>API Response structure:
 * <pre>
 * {
 *   "books": [ {...}, {...} ]
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BooksResponse {

    @JsonProperty("books")
    private List<Book> books;

    // Default constructor for Jackson
    public BooksResponse() {}

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public int getBookCount() {
        return books == null ? 0 : books.size();
    }

    @Override
    public String toString() {
        return "BooksResponse{bookCount=" + getBookCount() + "}";
    }
}
