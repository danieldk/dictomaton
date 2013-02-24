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
 * An immutable mapping from {@link String} to <tt>double</tt>.
 */
public class ImmutableStringDoubleMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private PerfectHashDictionary d_keys;
    private double[] d_values;

    /**
     * A builder for {@link ImmutableStringDoubleMap}. Mappings
     * can be added to the builder using the {@link #put} and
     * {@link #putAll} methods. The {@link ImmutableStringDoubleMap}
     * can then be constructed using the {@link #build} method.
     */
    public static class Builder {
        private TreeMap<String, Double> d_map;

        public Builder() {
            d_map = new TreeMap<String, Double>();
        }

        /**
         * Put a key/value pair.
         */
        public synchronized Builder put(String key, Double value) {
            d_map.put(key, value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link Map}.
         */
        public synchronized Builder putAll(Map<String, Double> map) {
            d_map.putAll(map);
            return this;
        }

        /**
         * Construct a {@link ImmutableStringDoubleMap}.
         */
        public synchronized ImmutableStringDoubleMap build() throws DictionaryBuilderException {
            DictionaryBuilder dictBuilder = new DictionaryBuilder();
            dictBuilder.addAll(d_map.keySet());
            PerfectHashDictionary dict = dictBuilder.buildPerfectHash();

            double[] values = new double[d_map.size()];

            int i = 0;
            for (double value : d_map.values())
                values[i++] = value;

            return new ImmutableStringDoubleMap(dict, values);
        }
    }

    private ImmutableStringDoubleMap(PerfectHashDictionary keys, double[] values) {
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
    public double getOrElse(String key, double defaultValue) {
        int hash = d_keys.number(key);
        if (hash == -1)
            return defaultValue;

        return d_values[hash - 1];
    }
}
