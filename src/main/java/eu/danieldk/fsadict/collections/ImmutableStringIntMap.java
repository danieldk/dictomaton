package eu.danieldk.fsadict.collections;

import eu.danieldk.fsadict.DictionaryBuilder;
import eu.danieldk.fsadict.DictionaryBuilderException;
import eu.danieldk.fsadict.PerfectHashDictionary;

import java.util.Map;
import java.util.TreeMap;

/**
 * An immutable mapping from {@link String} to int.
 */
public class ImmutableStringIntMap {
    private PerfectHashDictionary d_keys;
    private int[] d_values;

    /**
     * A builder for {@link ImmutableStringIntMap}.
     */
    public static class Builder {
        private TreeMap<String, Integer> d_map;

        public Builder() {
            d_map = new TreeMap<String, Integer>();
        }

        /**
         * Put a key/value pair.
         */
        public Builder put(String key, Integer value) {
            d_map.put(key, value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link Map}.
         */
        public Builder putAll(Map<String, Integer> map) {
            d_map.putAll(map);
            return this;
        }

        /**
         * Construct a {@link ImmutableStringIntMap}.
         */
        public ImmutableStringIntMap build() throws DictionaryBuilderException {
            DictionaryBuilder dictBuilder = new DictionaryBuilder();
            dictBuilder.addAll(d_map.keySet());
            PerfectHashDictionary dict = dictBuilder.buildPerfectHash();

            int[] values = new int[d_map.size()];

            int i = 0;
            for (int value : d_map.values())
                values[i++] = value;

            return new ImmutableStringIntMap(dict, values);
        }
    }

    private ImmutableStringIntMap(PerfectHashDictionary keys, int[] values) {
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
    public int getOrElse(String key, int defaultValue) {
        int hash = d_keys.number(key);
        if (hash == -1)
            return defaultValue;

        return d_values[hash - 1];
    }
}
