package com.ecommerce.framework.utils;

import com.ecommerce.framework.exceptions.FrameworkException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * JsonUtils — Jackson wrapper for JSON serialization and deserialization.
 *
 * <p>Provides a shared, pre-configured ObjectMapper to avoid repeated instantiation.
 * <p>Key ObjectMapper settings:
 * <ul>
 *   <li>FAIL_ON_UNKNOWN_PROPERTIES = false — tolerates extra fields in API responses</li>
 *   <li>WRITE_DATES_AS_TIMESTAMPS = false — renders dates as ISO strings</li>
 * </ul>
 */
public final class JsonUtils {

    private static final Logger log = LogManager.getLogger(JsonUtils.class);

    /** Shared ObjectMapper instance — ObjectMapper is thread-safe after configuration. */
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private JsonUtils() {}

    /**
     * Serializes a Java object to a JSON string.
     *
     * @param object the object to serialize
     * @return JSON string representation
     */
    public static String toJson(Object object) {
        try {
            String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            log.debug("Serialized object to JSON: {}", json);
            return json;
        } catch (IOException e) {
            throw new FrameworkException("Failed to serialize object to JSON: " + object.getClass().getName(), e);
        }
    }

    /**
     * Deserializes a JSON string to a Java object of the given class.
     *
     * @param json      the JSON string
     * @param valueType the target class
     * @param <T>       the type parameter
     * @return deserialized object
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return MAPPER.readValue(json, valueType);
        } catch (IOException e) {
            throw new FrameworkException("Failed to deserialize JSON to " + valueType.getName(), e);
        }
    }

    /**
     * Deserializes a JSON string to a generic type (e.g., List<SomeClass>).
     *
     * @param json     the JSON string
     * @param typeRef  TypeReference describing the generic target type
     * @param <T>      the type parameter
     * @return deserialized object
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            throw new FrameworkException("Failed to deserialize JSON to generic type", e);
        }
    }

    /**
     * Reads a JSON file from the classpath and deserializes it to the given class.
     *
     * @param resourcePath classpath-relative path (e.g., "testdata/users.json")
     * @param valueType    the target class
     * @param <T>          the type parameter
     * @return deserialized object
     */
    public static <T> T readJsonFile(String resourcePath, Class<T> valueType) {
        log.info("Reading JSON file from classpath: [{}]", resourcePath);
        try (InputStream inputStream = JsonUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new FrameworkException("JSON file not found on classpath: [" + resourcePath + "]");
            }
            return MAPPER.readValue(inputStream, valueType);
        } catch (IOException e) {
            throw new FrameworkException("Failed to read JSON file: [" + resourcePath + "]", e);
        }
    }

    /**
     * Reads a JSON file from the classpath and deserializes to a List of the given class.
     *
     * @param resourcePath classpath-relative path
     * @param elementType  class of list elements
     * @param <T>          element type
     * @return list of deserialized objects
     */
    public static <T> List<T> readJsonFileAsList(String resourcePath, Class<T> elementType) {
        log.info("Reading JSON file as List from classpath: [{}]", resourcePath);
        try (InputStream inputStream = JsonUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new FrameworkException("JSON file not found on classpath: [" + resourcePath + "]");
            }
            return MAPPER.readValue(inputStream,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            throw new FrameworkException("Failed to read JSON file as list: [" + resourcePath + "]", e);
        }
    }

    /**
     * Returns the shared ObjectMapper instance for direct use.
     *
     * @return configured ObjectMapper
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
