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

import eu.danieldk.dictomaton.DictionaryBuilderException;
import eu.danieldk.dictomaton.categories.Tests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Category(Tests.class)
public class ImmutableStringObjectMapTest {
    private Map<String, String> d_locations;

    @Before
    public void initialize() {
        d_locations = new HashMap<>();
        d_locations.put("New York", "USA");
        d_locations.put("Amsterdam", "The Netherlands");
        d_locations.put("Paris", "France");
    }

    @Test(expected = IllegalArgumentException.class)
    public void orderedComparatorTest() throws DictionaryBuilderException {
        TreeMap<String, String> bogus = new TreeMap<>(Collections.reverseOrder());
        ImmutableStringObjectMap<String> test = new ImmutableStringObjectMap.OrderedBuilder<String>().putAll(bogus).build();
        Assert.assertEquals(0, test.size());
    }

    @Test
    public void getTest() throws DictionaryBuilderException {
        // Unordered
        Map<String, String> iso = new ImmutableStringObjectMap.Builder<String>().putAll(d_locations).build();

        Assert.assertEquals(3, iso.size());
        Assert.assertEquals("USA", iso.get("New York"));
        Assert.assertEquals("The Netherlands", iso.get("Amsterdam"));
        Assert.assertEquals("France", iso.get("Paris"));

        // Ordered
        iso = new ImmutableStringObjectMap.OrderedBuilder<String>().putAll(new TreeMap<>(d_locations))
                .build();

        Assert.assertEquals(3, iso.size());
        Assert.assertEquals("USA", iso.get("New York"));
        Assert.assertEquals("The Netherlands", iso.get("Amsterdam"));
        Assert.assertEquals("France", iso.get("Paris"));
    }

    @Test
    public void containsOrderedPutTest() throws DictionaryBuilderException {
        // Ordered
        ImmutableStringObjectMap.OrderedBuilder<String> builder = new ImmutableStringObjectMap.OrderedBuilder<>();
        for (Map.Entry<String, String> e : new TreeMap<>(d_locations).entrySet())
            builder.put(e.getKey(), e.getValue());

        ImmutableStringObjectMap<String> test = builder.build();

        for (Map.Entry<String, String> entry : d_locations.entrySet())
            Assert.assertTrue(test.containsKey(entry.getKey()));
    }

    @Test
    public void equalsTest() throws DictionaryBuilderException {
        Map<String, String> iss = new ImmutableStringObjectMap.Builder<String>().putAll(d_locations).build();
        Assert.assertEquals(d_locations, iss);
    }

    @Test(expected = DictionaryBuilderException.class)
    public void invalidOrderTest() throws DictionaryBuilderException {
        ImmutableStringObjectMap<String> test = new ImmutableStringObjectMap.OrderedBuilder<String>()
                .put("Paris", "France").put("Amsterdam", "The Netherlands").build();
        Assert.assertEquals(2, test.size());
    }
}
