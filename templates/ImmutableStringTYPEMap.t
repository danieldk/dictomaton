package eu.danieldk.fsadict.collections;

import eu.danieldk.fsadict.DictionaryBuilder;
import eu.danieldk.fsadict.DictionaryBuilderException;
import eu.danieldk.fsadict.PerfectHashDictionary;

import java.util.Map;
import java.util.TreeMap;

/**
 * An immutable mapping from {@link String} to ##UNBOXED_TYPE##.
 */
public class ImmutableString##TYPE_NAME##Map {
    private PerfectHashDictionary d_keys;
    private ##UNBOXED_TYPE##[] d_values;

    /**
     * A builder for {@link ImmutableString##TYPE_NAME##Map}.
     */
    public static class Builder {
        private TreeMap<String, ##BOXED_TYPE##> d_map;

        public Builder() {
            d_map = new TreeMap<String, ##BOXED_TYPE##>();
        }

        /**
         * Put a key/value pair.
         */
        public Builder put(String key, ##BOXED_TYPE## value) {
            d_map.put(key, value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link Map}.
         */
        public Builder putAll(Map<String, ##BOXED_TYPE##> map) {
            d_map.putAll(map);
            return this;
        }

        /**
         * Construct a {@link ImmutableString##TYPE_NAME##Map}.
         */
        public ImmutableString##TYPE_NAME##Map build() throws DictionaryBuilderException {
            DictionaryBuilder dictBuilder = new DictionaryBuilder();
            dictBuilder.addAll(d_map.keySet());
            PerfectHashDictionary dict = dictBuilder.buildPerfectHash();

            ##UNBOXED_TYPE##[] values = new ##UNBOXED_TYPE##[d_map.size()];

            int i = 0;
            for (##UNBOXED_TYPE## value : d_map.values())
                values[i++] = value;

            return new ImmutableString##TYPE_NAME##Map(dict, values);
        }
    }

    private ImmutableString##TYPE_NAME##Map(PerfectHashDictionary keys, ##UNBOXED_TYPE##[] values) {
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
    public ##UNBOXED_TYPE## getOrElse(String key, ##UNBOXED_TYPE## defaultValue) {
        int hash = d_keys.number(key);
        if (hash == -1)
            return defaultValue;

        return d_values[hash - 1];
    }
}
