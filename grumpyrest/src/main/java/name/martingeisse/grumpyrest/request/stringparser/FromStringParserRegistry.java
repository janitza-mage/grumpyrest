/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FromStringParserRegistry implements ParseFromStringService {

    // This list is not thread-safe, but adding parsers after starting to serve requests would mess up things anyway.
    private final List<FromStringParser> parserList = new ArrayList<>();
    private final ConcurrentMap<Type, FromStringParser> parserMap = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------------------------------------------------
    // configuration-time methods
    // ----------------------------------------------------------------------------------------------------------------

    public void clearParsers() {
        parserList.clear();
    }

    public void addParser(FromStringParser parser) {
        parserList.add(Objects.requireNonNull(parser, "parser"));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // run-time methods
    // ----------------------------------------------------------------------------------------------------------------

    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        if (parserMap.containsKey(type)) {
            return true;
        }
        for (var parser : parserList) {
            if (parser.supportsType(type)) {
                return true;
            }
        }
        return false;
    }

    public FromStringParser getParser(Type type) {
        Objects.requireNonNull(type, "type");

        // computeIfAbsent() cannot be used, if it behaves as it should, because recursively adding recognized types
        // would cause a ConcurrentModificationException. Note that thread safety is not a concern here because,
        // while two threads might *both* decide to create a missing adapter, we just end up with either one of them
        // and they should be equivalent.
        FromStringParser parser = parserMap.get(type);

        // check if one of the registered parsers supports this type
        if (parser == null) {
            for (FromStringParser parserFromList : parserList) {
                if (parserFromList.supportsType(type)) {
                    parser = parserFromList;
                    parserMap.put(type, parser);
                    break;
                }
            }
        }

        // if this failed, then we don't have an appropriate parser
        if (parser == null) {
            throw new RuntimeException("no from-string parser found for type: " + type);
        }

        return parser;
    }

    @Override
    public Object parseFromString(String text, Type type) throws FromStringParserException {
        return getParser(type).parseFromString(text, type);
    }

}
