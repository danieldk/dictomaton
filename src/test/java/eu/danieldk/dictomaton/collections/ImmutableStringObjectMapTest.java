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

import java.util.HashMap;
import java.util.Map;

@Category(Tests.class)
public class ImmutableStringObjectMapTest {
    private Map<String, String> d_locations;

    @Before
    public void initialize() {
        d_locations = new HashMap<String, String>();
        d_locations.put("New York", "USA");
        d_locations.put("Amsterdam", "The Netherlands");
    }

    @Test
    public void getTest() throws DictionaryBuilderException {
        Map<String, String> iso = new ImmutableStringObjectMap.Builder<String>().putAll(d_locations).build();

        Assert.assertEquals(2, iso.size());
        Assert.assertEquals("USA", iso.get("New York"));
        Assert.assertEquals("The Netherlands", iso.get("Amsterdam"));
    }

    @Test
    public void equalsTest() throws DictionaryBuilderException {
        Map<String, String> iss = new ImmutableStringObjectMap.Builder<String>().putAll(d_locations).build();
        Assert.assertEquals(d_locations, iss);
    }
}
