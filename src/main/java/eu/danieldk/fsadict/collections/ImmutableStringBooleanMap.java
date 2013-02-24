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
import java.util.Map;
import java.util.TreeMap;

/**
 * An immutable mapping from {@link String} to <tt>boolean</tt>.
 */
public class ImmutableStringBooleanMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private PerfectHashDictionary d_keys;
    private boolean[] d_values;

    /**
     * A builder for {@link ImmutableStringBooleanMap}. Mappings
     * can be added to the builder using the {@link #put} and
     * {@link #putAll} methods. The {@link ImmutableStringBooleanMap}
     * can then be constructed using the {@link #build} method.
     */
    public static class Builder {
        private TreeMap<String, Boolean> d_map;

        public Builder() {
            d_map = new TreeMap<String, Boolean>();
        }

        /**
         * Put a key/value pair.
         */
        public synchronized Builder put(String key, Boolean value) {
            d_map.put(key, value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link Map}.
         */
        public synchronized Builder putAll(Map<String, Boolean> map) {
            d_map.putAll(map);
            return this;
        }

        /**
         * Construct a {@link ImmutableStringBooleanMap}.
         */
        public synchronized ImmutableStringBooleanMap build() throws DictionaryBuilderException {
            DictionaryBuilder dictBuilder = new DictionaryBuilder();
            dictBuilder.addAll(d_map.keySet());
            PerfectHashDictionary dict = dictBuilder.buildPerfectHash();

            boolean[] values = new boolean[d_map.size()];

            int i = 0;
            for (boolean value : d_map.values())
                values[i++] = value;

            return new ImmutableStringBooleanMap(dict, values);
        }
    }

    private ImmutableStringBooleanMap(PerfectHashDictionary keys, boolean[] values) {
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
    public boolean getOrElse(String key, boolean defaultValue) {
        int hash = d_keys.number(key);
        if (hash == -1)
            return defaultValue;

        return d_values[hash - 1];
    }
}
