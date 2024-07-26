/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import name.martingeisse.grumpyjson.JsonTestUtil;
import name.martingeisse.grumpyjson.json_model.JsonElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class JsonElementConverterTest {

    private final JsonElementConverter converter = new JsonElementConverter();

    @Test
    public void test() throws Exception {
        JsonTestUtil.forJsonElements(element -> assertSame(element, converter.deserialize(element, JsonElement.class)));
        JsonTestUtil.forJsonElements(element -> assertSame(element, converter.serialize(element)));
    }

}
