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
import java.util.*;

/**
 * An immutable mapping from {@link String} to <tt>byte</tt>.
 */
public class ImmutableStringByteMap extends AbstractMap<String, Byte> implements Serializable {
    private static final long serialVersionUID = 1L;

    private PerfectHashDictionary d_keys;
    private byte[] d_values;

    /**
     * A builder for {@link ImmutableStringByteMap}. Mappings
     * can be added to the builder using the {@link #put} and
     * {@link #putAll} methods. The {@link ImmutableStringByteMap}
     * can then be constructed using the {@link #build} method.
     */
    public static class Builder {

        private TreeMap<String, Byte> d_map;

        public Builder() {
            d_map = new TreeMap<String, Byte>();
        }

        /**
         * Put a key/value pair.
         */
        public synchronized Builder put(String key, Byte value) {
            d_map.put(key, value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link Map}.
         */
        public synchronized Builder putAll(Map<String, Byte> map) {
            d_map.putAll(map);
            return this;
        }

        /**
         * Construct a {@link ImmutableStringByteMap}.
         */
        public synchronized ImmutableStringByteMap build() throws DictionaryBuilderException {
            PerfectHashDictionary dict = new DictionaryBuilder().addAll(d_map.keySet()).buildPerfectHash();

            byte[] values = new byte[d_map.size()];

            int i = 0;
            for (byte value : d_map.values())
                values[i++] = value;

            return new ImmutableStringByteMap(dict, values);
        }

    }

    private class EntrySet extends AbstractSet<Entry<String, Byte>> {
        private class EntrySetIterator implements Iterator<Entry<String, Byte>> {
            private final Iterator<String> d_keyIter;

            public EntrySetIterator() {
                d_keyIter = d_keys.iterator();
            }

            @Override
            public boolean hasNext() {
                return d_keyIter.hasNext();
            }

            @Override
            public Entry<String, Byte> next() {
                String key = d_keyIter.next();
                int idx = d_keys.number(key) - 1;
                return new SimpleEntry<String, Byte>(key, d_values[idx]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public boolean contains(Object o) {
            if (o == null)
                return false;

            if (!(o instanceof Entry))
                return false;

            Entry e = (Entry) o;

            // Values are primitive and cannot be null.
            if (e.getKey() == null || e.getKey() == null)
                return false;

            if (!(e.getKey() instanceof String) || !(e.getValue() instanceof Byte))
                return false;

            String key = (String) e.getKey();
            Byte value = (Byte) e.getValue();

            int hash = d_keys.number(key);

            // Does not contain the key.
            if (hash == -1)
                return false;

            return d_values[hash - 1] == value.byteValue();

        }

        @Override
        public boolean isEmpty()  {
            return d_keys.isEmpty();
        }

        @Override
        public Iterator<Entry<String, Byte>> iterator() {
            return new EntrySetIterator();
        }

        @Override
        public int size() {
            return d_keys.size();
        }
    }


    private class ByteArrayList extends AbstractList<Byte> {
        @Override
        public Byte get(int index) {
            return d_values[index];
        }

        @Override
        public int size() {
            return d_values.length;
        }
    }

    private ImmutableStringByteMap(PerfectHashDictionary keys, byte[] values) {
        d_keys = keys;
        d_values = values;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object o) {
        return d_keys.contains(o);
    }

    @Override
    public Set<Entry<String, Byte>> entrySet() {
        return new EntrySet();
    }

    /**
     * Get the value associated with a key, returning a default value is it
     * is not in the mapping.
     */
    public byte getOrElse(String key, byte defaultValue) {
        int hash = d_keys.number(key);
        if (hash == -1)
            return defaultValue;

        return d_values[hash - 1];
    }

    @Override
    public boolean isEmpty() {
        return d_keys.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return d_keys;
    }

    @Override
    public Byte put(String k, Byte v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Byte> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Byte remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return d_keys.size();
    }

    /**
     * Get an iterator over the keys in the mapping.
     */
    public Iterator<String> keyIterator() {
        return d_keys.iterator();
    }

    @Override
    public Collection<Byte> values() {
        return new ByteArrayList();
    }
}
