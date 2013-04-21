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

package eu.danieldk.dictomaton;

import eu.danieldk.dictomaton.categories.Tests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Category(Tests.class)
public class DictionaryTest {
    private List<String> d_words1;
    private List<String> d_words2;
    private Dictionary d_dict;

    @SuppressWarnings("serial")
    @Before
    public void initialize() throws DictionaryBuilderException {
        d_words1 = new ArrayList<String>() {{
            add("al");
            add("alleen");
            add("avonden");
            add("zeemeeuw");
            add("zeker");
            add("zeven");
            add("zoeven");
        }};

        d_words2 = new ArrayList<String>() {{
            add("als");
            add("allen");
            add("avond");
            add("zeemeeuwen");
            add("zeer");
            add("zepen");
            add("zoef");
        }};

        d_dict = new DictionaryBuilder().addAll(d_words1).build();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addTest() {
        d_dict.add("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addAllTest() {
        d_dict.addAll(d_words2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void clearTest() {
        d_dict.clear();
    }

    @Test
    public void containsTest() {
        for (String word : d_words1)
            Assert.assertTrue(d_dict.contains(word));
    }

    @Test
    public void containsAllTest() {
        Assert.assertTrue(d_dict.containsAll(d_words1));
        Assert.assertFalse(d_dict.containsAll(d_words2));
    }

    @Test
    public void doesNotContainWordsTest() {
        for (String word : d_words2)
            Assert.assertTrue(!d_dict.contains(word));
    }

    @Test
    public void emptyTest() {
        Dictionary dict = new DictionaryBuilder().build();
        Assert.assertFalse(dict.contains("foo"));
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void isEmptyTest() {
        Assert.assertFalse(d_dict.isEmpty());
        Dictionary dict = new DictionaryBuilder().build();
        Assert.assertTrue(dict.isEmpty());
    }

    @Test
    public void iterationTest() {
        List<CharSequence> listFromIteration = new LinkedList<CharSequence>();
        for (CharSequence seq : d_dict)
            listFromIteration.add(seq);
        Assert.assertEquals(d_words1, listFromIteration);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void iteratorRemoveTest() {
        Iterator<String> iter = d_dict.iterator();
        if (iter.hasNext())
            iter.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeTest() {
        d_dict.remove("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeAllTest() {
        d_dict.removeAll(d_words1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void retainAllTest() {
        d_dict.retainAll(d_words1);
    }

    @Test
    public void sizeTest() {
        Assert.assertEquals(7, d_dict.size());
    }

    @Test
    public void toArrayTest() {
        Object[] check = d_words1.toArray();
        Assert.assertArrayEquals(check, d_dict.toArray());

        Object[] check2 = new Object[d_dict.size() + 2];
        Object[] conv = d_dict.toArray(check2);

        Assert.assertTrue(check2 == conv);
        Assert.assertTrue(check2[d_dict.size()] == null);
    }
}
