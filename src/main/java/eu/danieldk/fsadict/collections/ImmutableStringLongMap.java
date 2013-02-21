package eu.danieldk.fsadict.collections;

import eu.danieldk.fsadict.DictionaryBuilder;
import eu.danieldk.fsadict.DictionaryBuilderException;
import eu.danieldk.fsadict.PerfectHashDictionary;

import java.util.Map;
import java.util.TreeMap;

/**
 * An immutable mapping from {@link String} to long.
 */
public class ImmutableStringLongMap {
    private PerfectHashDictionary d_keys;
    private long[] d_values;

    /**
     * A builder for {@link ImmutableStringLongMap}.
     */
    public static class Builder {
        private TreeMap<String, Long> d_map;

        public Builder() {
            d_map = new TreeMap<String, Long>();
        }

        /**
         * Put a key/value pair.
         */
        public Builder put(String key, Long value) {
            d_map.put(key, value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link Map}.
         */
        public Builder putAll(Map<String, Long> map) {
            d_map.putAll(map);
            return this;
        }

        /**
         * Construct a {@link ImmutableStringLongMap}.
         */
        public ImmutableStringLongMap build() throws DictionaryBuilderException {
            DictionaryBuilder dictBuilder = new DictionaryBuilder();
            dictBuilder.addAll(d_map.keySet());
            PerfectHashDictionary dict = dictBuilder.buildPerfectHash();

            long[] values = new long[d_map.size()];

            int i = 0;
            for (long value : d_map.values())
                values[i++] = value;

            return new ImmutableStringLongMap(dict, values);
        }
    }

    private ImmutableStringLongMap(PerfectHashDictionary keys, long[] values) {
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
    public long getOrElse(String key, long defaultValue) {
        int hash = d_keys.number(key);
        if (hash == -1)
            return defaultValue;

        return d_values[hash - 1];
    }
}
