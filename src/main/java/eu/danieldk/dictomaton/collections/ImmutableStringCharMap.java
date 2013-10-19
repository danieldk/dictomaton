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
 * An immutable mapping from {@link String} to <tt>char</tt>.
 */
public class ImmutableStringCharMap extends AbstractMap<String, Character> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PerfectHashDictionary d_keys;
    private final char[] d_values;

    /**
     * A builder for {@link ImmutableStringCharMap}. Mappings
     * can be added to the builder using the {@link #put} and
     * {@link #putAll} methods. The {@link ImmutableStringCharMap}
     * can then be constructed using the {@link #build} method.
     */
    public static class Builder {

        private final TreeMap<String, Character> d_map;

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
            PerfectHashDictionary dict = new DictionaryBuilder().addAll(d_map.keySet()).buildPerfectHash(false);

            char[] values = new char[d_map.size()];

            int i = 0;
            for (char value : d_map.values())
                values[i++] = value;

            return new ImmutableStringCharMap(dict, values);
        }

    }

    private class EntrySet extends AbstractSet<Entry<String, Character>> {
        private class EntrySetIterator implements Iterator<Entry<String, Character>> {
            private final Iterator<String> d_keyIter;

            public EntrySetIterator() {
                d_keyIter = d_keys.iterator();
            }

            @Override
            public boolean hasNext() {
                return d_keyIter.hasNext();
            }

            @Override
            public Entry<String, Character> next() {
                String key = d_keyIter.next();
                int idx = d_keys.number(key) - 1;
                return new SimpleEntry<String, Character>(key, d_values[idx]);
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

            if (!(e.getKey() instanceof String) || !(e.getValue() instanceof Character))
                return false;

            String key = (String) e.getKey();
            Character value = (Character) e.getValue();

            int hash = d_keys.number(key);

            // Does not contain the key.
            if (hash == -1)
                return false;

            return d_values[hash - 1] == value.charValue();

        }

        @Override
        public boolean isEmpty()  {
            return d_keys.isEmpty();
        }

        @Override
        public Iterator<Entry<String, Character>> iterator() {
            return new EntrySetIterator();
        }

        @Override
        public int size() {
            return d_keys.size();
        }
    }


    private class CharArrayList extends AbstractList<Character> {
        @Override
        public Character get(int index) {
            return d_values[index];
        }

        @Override
        public int size() {
            return d_values.length;
        }
    }

    private ImmutableStringCharMap(PerfectHashDictionary keys, char[] values) {
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
    public Set<Entry<String, Character>> entrySet() {
        return new EntrySet();
    }

    @Override
    public Character get(Object o) {
        if (!(o instanceof String))
            return null;

        String key = (String) o;

        int hashcode = d_keys.number(key);
        if (hashcode == -1)
            return null;

        return d_values[hashcode - 1];
    }

    /**
     * Get the value associated with a key, returning a default value is it
     * is not in the mapping.
     */
    public char getOrElse(String key, char defaultValue) {
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
    public Character put(String k, Character v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Character> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Character remove(Object key) {
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
    public Collection<Character> values() {
        return new CharArrayList();
    }
}
