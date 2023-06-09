package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.JsonValidationException;

import java.lang.reflect.Type;
import java.util.Objects;

public class BooleanAdapter implements JsonTypeAdapter<Boolean> {

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return type.equals(Boolean.TYPE) || type.equals(Boolean.class);
    }

    @Override
    public Boolean fromJson(JsonElement json, Type type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        if (json instanceof JsonPrimitive primitive) {
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        }

        throw new JsonValidationException("expected boolean, found: " + json);
    }

    @Override
    public JsonElement toJson(Boolean value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return new JsonPrimitive(value);
    }

}
