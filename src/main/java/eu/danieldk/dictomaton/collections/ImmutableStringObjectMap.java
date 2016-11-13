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
package eu.danieldk.dictomaton.collections;

import eu.danieldk.dictomaton.DictionaryBuilder;
import eu.danieldk.dictomaton.DictionaryBuilderException;
import eu.danieldk.dictomaton.PerfectHashDictionary;

import java.io.Serializable;
import java.util.*;

/**
 * An immutable mapping from {@link String} to an object with the type <code>V</code>, where keys are compactly stored
 * using a finite state automaton.
 */
public class ImmutableStringObjectMap<V> extends AbstractMap<String, V> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PerfectHashDictionary d_keys;
    private final V[] d_values;

    /**
     * A builder for {@link ImmutableStringIntMap}. Mappings
     * can be added to the builder using the {@link #put} and
     * {@link #putAll} methods. The {@link ImmutableStringIntMap}
     * can then be constructed using the {@link #build} method.
     */
    public static class Builder<V> {

        private final TreeMap<String, V> d_map;

        public Builder() {
            d_map = new TreeMap<>();
        }


        /**
         * Put a key/value pair.
         */
        public synchronized Builder<V> put(String key, V value) {
            d_map.put(key, value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link java.util.Map}.
         */
        public synchronized Builder<V> putAll(Map<String, V> map) {
            d_map.putAll(map);
            return this;
        }

        /**
         * Construct a {@link ImmutableStringIntMap}.
         */
        public synchronized ImmutableStringObjectMap<V> build() throws DictionaryBuilderException {
            PerfectHashDictionary dict = new DictionaryBuilder().addAll(d_map.keySet()).buildPerfectHash(false);

            @SuppressWarnings("unchecked")
            V[] values = (V[]) new Object[d_map.size()];

            int i = 0;
            for (V value : d_map.values())
                values[i++] = value;

            return new ImmutableStringObjectMap<>(dict, values);
        }
    }

    /**
     * A builder for {@link ImmutableStringObjectMap}. Mappings can be added to the builder using the {@link #put} and
     * {@link #putAll} methods. The {@link ImmutableStringObjectMap} can then be constructed using the {@link #build}
     * method. <b>Note:</b> This builder assumes that entries are put in key order. This additional assumption makes
     * the builder more efficient than {@link Builder}.
     */
    public static class OrderedBuilder<V> {
        private final DictionaryBuilder dictionaryBuilder;

        private final ArrayList<V> values;

        public OrderedBuilder() {
            this.dictionaryBuilder = new DictionaryBuilder();
            this.values = new ArrayList<>();
        }

        /**
         * Put a key/value pair.
         */
        public synchronized OrderedBuilder put(String key, V value) throws DictionaryBuilderException {
            dictionaryBuilder.add(key);
            values.add(value);
            return this;
        }

        /**
         * Put all key/value pairs from a {@link Map}. The map should be an ordered map (by key). If
         * not, a {@link IllegalArgumentException} is thrown.
         */
        public synchronized OrderedBuilder putAll(SortedMap<String, V> map) throws DictionaryBuilderException {
            if (map.comparator() != null)
                throw new IllegalArgumentException("SortedMap does not use the natural ordering of its keys");

            values.ensureCapacity(values.size() + map.size());

            for (SortedMap.Entry<String, V> entry: map.entrySet()) {
                dictionaryBuilder.add(entry.getKey());
                values.add(entry.getValue());
            }

            return this;
        }

        /**
         * Construct a {@link ImmutableStringIntMap}.
         */
        public synchronized ImmutableStringObjectMap build() throws DictionaryBuilderException {
            PerfectHashDictionary dict = dictionaryBuilder.buildPerfectHash(false);

            @SuppressWarnings("unchecked")
            V[] arr = (V[]) new Object[values.size()];

            for (int i = 0; i < values.size(); ++i)
                arr[i] = values.get(i);

            return new ImmutableStringObjectMap<>(dict, arr);
        }
    }

    private class EntrySet extends AbstractSet<Entry<String, V>> {
        private class EntrySetIterator implements Iterator<Entry<String, V>> {
            private final Iterator<String> d_keyIter;

            public EntrySetIterator() {
                d_keyIter = d_keys.iterator();
            }

            @Override
            public boolean hasNext() {
                return d_keyIter.hasNext();
            }

            @Override
            public Entry<String, V> next() {
                String key = d_keyIter.next();
                int idx = d_keys.number(key) - 1;
                return new SimpleEntry<>(key, d_values[idx]);
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

            // Key cannot be null.
            if (e.getKey() == null)
                return false;

            if (!(e.getKey() instanceof String))
                return false;

            String key = (String) e.getKey();
            Object value = e.getValue();

            int hash = d_keys.number(key);

            // Does not contain the key.
            if (hash == -1)
                return false;

            return d_values[hash - 1].equals(value);

        }

        @Override
        public boolean isEmpty() {
            return d_keys.isEmpty();
        }

        @Override
        public Iterator<Entry<String, V>> iterator() {
            return new EntrySetIterator();
        }

        @Override
        public int size() {
            return d_keys.size();
        }
    }


    private class ObjectArrayList extends AbstractList<V> {
        @Override
        public V get(int index) {
            return d_values[index];
        }

        @Override
        public int size() {
            return d_values.length;
        }
    }

    private ImmutableStringObjectMap(PerfectHashDictionary keys, V[] values) {
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
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet();
    }

    @Override
    public V get(Object o) {
        if (!(o instanceof String))
            return null;

        String key = (String) o;

        int hashcode = d_keys.number(key);
        if (hashcode == -1)
            return null;

        return d_values[hashcode - 1];
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
    public V put(String k, V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
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
    public Collection<V> values() {
        return new ObjectArrayList();
    }
}
