package com.ecommerce.framework.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Book — POJO model for the DemoQA BookStore API Book entity.
 *
 * <p>Used for serialization (request payloads) and deserialization (response parsing).
 * <p>@JsonIgnoreProperties(ignoreUnknown = true) tolerates extra fields in API responses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("title")
    private String title;

    @JsonProperty("subTitle")
    private String subTitle;

    @JsonProperty("author")
    private String author;

    @JsonProperty("publish_date")
    private String publishDate;

    @JsonProperty("publisher")
    private String publisher;

    @JsonProperty("pages")
    private int pages;

    @JsonProperty("description")
    private String description;

    @JsonProperty("website")
    private String website;

    // Default constructor for Jackson deserialization
    public Book() {}

    // Full constructor for programmatic test data creation
    public Book(String isbn, String title, String author) {
        this.isbn   = isbn;
        this.title  = title;
        this.author = author;
    }

    // ─────────────────────────────────────────────────────────────
    // Getters & Setters
    // ─────────────────────────────────────────────────────────────

    public String getIsbn()        { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle()       { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubTitle()    { return subTitle; }
    public void setSubTitle(String subTitle) { this.subTitle = subTitle; }

    public String getAuthor()      { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }

    public String getPublisher()   { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPages()          { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWebsite()     { return website; }
    public void setWebsite(String website) { this.website = website; }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + "', title='" + title + "', author='" + author + "'}";
    }
}
