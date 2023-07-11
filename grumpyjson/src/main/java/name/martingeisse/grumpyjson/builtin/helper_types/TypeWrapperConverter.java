/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * The {@link JsonTypeAdapter} for {@link TypeWrapper}.
 * <p>
 * Only supports serialization. Type wrappers are a workaround for missing type arguments during serialization, and
 * do not provide any benefit when parsing JSON. You should only use type wrappers as the top-level value.
 * <p>
 * This adapter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public class TypeWrapperConverter implements JsonTypeAdapter<TypeWrapper<?>> {

    private final JsonRegistries registries;

    public TypeWrapperConverter(JsonRegistries registries) {
        this.registries = registries;
    }

    @Override
    public boolean supportsType(Type type) {
        if (type instanceof Class<?> c) {
            return TypeWrapper.class.isAssignableFrom(c);
        }
        if (type instanceof ParameterizedType pt) {
            return pt.getRawType() == TypeWrapper.class;
        }
        return false;
    }

    @Override
    public TypeWrapper<?> deserialize(JsonElement json, Type _ignored) throws JsonDeserializationException {
        throw new UnsupportedOperationException("TypeWrapper cannot be parsed");
    }

    @Override
    public JsonElement serialize(TypeWrapper<?> value, Type _ignored) throws JsonSerializationException {
        @SuppressWarnings("rawtypes") JsonTypeAdapter adapter = registries.get(value.getType());
        //noinspection unchecked
        return adapter.serialize(value.getValue(), value.getType());
    }

}
