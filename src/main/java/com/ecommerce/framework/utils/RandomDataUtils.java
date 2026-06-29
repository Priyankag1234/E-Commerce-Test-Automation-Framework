package com.ecommerce.framework.utils;

import com.github.javafaker.Faker;

import java.util.Locale;

/**
 * RandomDataUtils — Generates randomized test data for form filling.
 *
 * <p>Uses JavaFaker library to produce realistic, locale-aware data.
 * <p>Prevents test interdependence by ensuring each test run uses unique inputs.
 */
public final class RandomDataUtils {

    private static final Faker faker = new Faker(Locale.ENGLISH);

    private RandomDataUtils() {}

    /** Returns a random first name. */
    public static String getFirstName() {
        return faker.name().firstName();
    }

    /** Returns a random last name. */
    public static String getLastName() {
        return faker.name().lastName();
    }

    /** Returns a random full name. */
    public static String getFullName() {
        return faker.name().fullName();
    }

    /** Returns a random email address. */
    public static String getEmail() {
        return faker.internet().emailAddress();
    }

    /** Returns a random US-format ZIP code (5 digits). */
    public static String getZipCode() {
        return faker.address().zipCode().substring(0, 5);
    }

    /** Returns a random street address. */
    public static String getStreetAddress() {
        return faker.address().streetAddress();
    }

    /** Returns a random city name. */
    public static String getCity() {
        return faker.address().city();
    }

    /** Returns a random state abbreviation. */
    public static String getState() {
        return faker.address().stateAbbr();
    }

    /** Returns a random phone number. */
    public static String getPhoneNumber() {
        return faker.phoneNumber().cellPhone();
    }

    /** Returns a random alphanumeric string of given length. */
    public static String getRandomString(int length) {
        return faker.lorem().fixedString(length);
    }

    /** Returns a random integer between min and max (inclusive). */
    public static int getRandomInt(int min, int max) {
        return faker.number().numberBetween(min, max);
    }

    /** Returns a random product name. */
    public static String getProductName() {
        return faker.commerce().productName();
    }
}
