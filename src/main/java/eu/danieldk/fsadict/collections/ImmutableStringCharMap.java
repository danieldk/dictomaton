// Copyright 2013 Daniel de Kok
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package eu.danieldk.fsadict.collections;

import eu.danieldk.fsadict.DictionaryBuilder;
import eu.danieldk.fsadict.DictionaryBuilderException;
import eu.danieldk.fsadict.PerfectHashDictionary;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * An immutable mapping from {@link String} to <tt>char</tt>.
 */
public class ImmutableStringCharMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private PerfectHashDictionary d_keys;
    private char[] d_values;

    /**
     * A builder for {@link ImmutableStringCharMap}. Mappings
     * can be added to the builder using the {@link #put} and
     * {@link #putAll} methods. The {@link ImmutableStringCharMap}
     * can then be constructed using the {@link #build} method.
     */
    public static class Builder {
        private TreeMap<String, Character> d_map;

        public Builder() {
            d_map = new TreeMap<String, Character>();
        }

        /**
         * Put a key/value pair.
         */
        public synchronized Builder put(String key, Character value) {
            d_map.put(key, value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link Map}.
         */
        public synchronized Builder putAll(Map<String, Character> map) {
            d_map.putAll(map);
            return this;
        }

        /**
         * Construct a {@link ImmutableStringCharMap}.
         */
        public synchronized ImmutableStringCharMap build() throws DictionaryBuilderException {
            PerfectHashDictionary dict = new DictionaryBuilder().addAll(d_map.keySet()).buildPerfectHash();

            char[] values = new char[d_map.size()];

            int i = 0;
            for (char value : d_map.values())
                values[i++] = value;

            return new ImmutableStringCharMap(dict, values);
        }
    }

    private ImmutableStringCharMap(PerfectHashDictionary keys, char[] values) {
        d_keys = keys;
        d_values = values;
    }

    /**
     * Check whether the map contains the given {@link String} as a
     * key.
     */
    public boolean contains(String key) {
        int hash = d_keys.number(key);
        return hash != -1;
    }

    /**
     * Get the value associated with a key. Return a default value is it
     * is not in the mapping.
     */
    public char getOrElse(String key, char defaultValue) {
        int hash = d_keys.number(key);
        if (hash == -1)
            return defaultValue;

        return d_values[hash - 1];
    }

    /**
     * Get an iterator over the keys in the mapping.
     */
    public Iterator<String> keyIterator()
    {
        return d_keys.iterator();
    }
}
