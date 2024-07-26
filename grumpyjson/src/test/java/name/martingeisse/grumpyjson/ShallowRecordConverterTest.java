/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import name.martingeisse.grumpyjson.builtin.IntegerConverter;
import name.martingeisse.grumpyjson.builtin.StringConverter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.json_model.JsonNumber;
import name.martingeisse.grumpyjson.json_model.JsonObject;
import name.martingeisse.grumpyjson.json_model.JsonString;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class ShallowRecordConverterTest {

    private record Record(int myInt, String myString) {}

    private final JsonSerializer<Record> serializer;
    private final JsonDeserializer deserializer;

    public ShallowRecordConverterTest() throws Exception {
        JsonRegistries registries = createRegistries(new IntegerConverter(), new StringConverter());
        registries.seal();
        serializer = registries.getSerializer(Record.class);
        deserializer = registries.getDeserializer(Record.class);
    }

    @Test
    public void testHappyCase() throws Exception {
        JsonObject json = JsonObject.of("myInt", JsonNumber.of(123), "myString", JsonString.of("foo"));

        Record record = new Record(123, "foo");

        Assertions.assertEquals(record, deserializer.deserialize(json, Record.class));
        Assertions.assertEquals(json, serializer.serialize(record));
    }

    @Test
    public void testDeserializationWrongType() throws Exception {
        forNull(json -> assertFailsDeserialization(deserializer, json, Record.class));
        forNumbers(json -> assertFailsDeserialization(deserializer, json, Record.class));
        forBooleans(json -> assertFailsDeserialization(deserializer, json, Record.class));
        forStrings(json -> assertFailsDeserialization(deserializer, json, Record.class));
        forArrays(json -> assertFailsDeserialization(deserializer, json, Record.class));
    }

    @Test
    public void testMissingProperty() {
        JsonObject json = JsonObject.of("myInt", JsonNumber.of(123));
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "myString")
        );
    }

    @Test
    public void testPropertyWithWrongType() {
        JsonObject json = JsonObject.of("myInt", JsonString.of("foo"), "myString", JsonString.of("foo"));
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, json, Record.class),
                new FieldErrorNode.FlattenedError("expected number, found: JSON:\"foo\"", "myInt")
        );
    }

    @Test
    public void testUnexpectedProperty() {
        JsonObject json = JsonObject.of(
                "myInt", JsonNumber.of(123),
                "thisDoesNotBelongHere", JsonNumber.of(456),
                "myString", JsonString.of("foo")
        );
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.UNEXPECTED_PROPERTY, "thisDoesNotBelongHere")
        );
    }

    @Test
    public void testMultipleFieldErrors() {
        JsonObject json = JsonObject.of("myInt", JsonString.of("foo"), "thisDoesNotBelongHere", JsonNumber.of(456));
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.UNEXPECTED_PROPERTY, "thisDoesNotBelongHere"),
                new FieldErrorNode.FlattenedError("expected number, found: JSON:\"foo\"", "myInt"),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "myString")
        );
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(serializer, null);
    }

}
