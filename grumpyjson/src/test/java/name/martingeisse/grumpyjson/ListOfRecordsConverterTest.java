/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import name.martingeisse.grumpyjson.builtin.IntegerConverter;
import name.martingeisse.grumpyjson.builtin.ListConverter;
import name.martingeisse.grumpyjson.builtin.StringConverter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.json_model.JsonArray;
import name.martingeisse.grumpyjson.json_model.JsonNumber;
import name.martingeisse.grumpyjson.json_model.JsonObject;
import name.martingeisse.grumpyjson.json_model.JsonString;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;

import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistries;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("rawtypes")
public class ListOfRecordsConverterTest {

    private record Record(int myInt, String myString) {}

    private final TypeToken<List<Record>> listOfRecordsTypeToken = new TypeToken<>() {};
    private final Type listOfRecordsType = listOfRecordsTypeToken.getType();

    private final JsonSerializer<List> serializer;
    private final JsonDeserializer deserializer;

    public ListOfRecordsConverterTest() throws Exception {
        JsonRegistries registries = createRegistries(new IntegerConverter(), new StringConverter());
        registries.registerDualConverter(new ListConverter(registries));
        registries.seal();
        serializer = registries.getSerializer(List.class);
        deserializer = registries.getDeserializer(listOfRecordsTypeToken.getType());
    }

    @Test
    public void testHappyCase() throws Exception {

        JsonObject object1 = JsonObject.of("myInt", JsonNumber.of(12), "myString", JsonString.of("foo"));
        JsonObject object2 = JsonObject.of("myInt", JsonNumber.of(34), "myString", JsonString.of("bar"));

        Record record1 = new Record(12, "foo");
        Record record2 = new Record(34, "bar");

        assertEquals(JsonArray.of(), serializer.serialize(List.of()));
        assertEquals(JsonArray.of(object1), serializer.serialize(List.of(record1)));
        assertEquals(JsonArray.of(object1, object2), serializer.serialize(List.of(record1, record2)));

        assertEquals(List.of(), deserializer.deserialize(JsonArray.of(), listOfRecordsType));
        assertEquals(List.of(record1), deserializer.deserialize(JsonArray.of(object1), listOfRecordsType));
        assertEquals(List.of(record1, record2), deserializer.deserialize(JsonArray.of(object1, object2), listOfRecordsType));
    }

}
