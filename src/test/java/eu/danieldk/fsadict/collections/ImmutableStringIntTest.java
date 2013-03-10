package eu.danieldk.fsadict.collections;

import eu.danieldk.fsadict.DictionaryBuilderException;
import eu.danieldk.fsadict.categories.Tests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.*;

/**
 * Unit tests for {@link ImmutableStringIntMap}.
 */
@Category(Tests.class)
public class ImmutableStringIntTest {
    private Map<String, Integer> d_check1;
    private Map<String, Integer> d_check2;

    @Before
    public void initialize() {
        d_check1 = new HashMap<String, Integer>() {{
            put("Pulp Fiction", 9);
            put("The Godfather", 9);
            put("Dumb and Dumber", 3);
        }};

        d_check2 = new HashMap<String, Integer>() {{
            put("Miles Davis", 9);
            put("John Zorn", 9);
            put("Thelonious Monk", 8);
            put("The White Stripes", 8);
            put("Canned Heat", 3);
        }};
    }

    @Test
    public void containsTest() throws DictionaryBuilderException {
        ImmutableStringIntMap test = new ImmutableStringIntMap.Builder().putAll(d_check1).build();

        for (Map.Entry<String, Integer> entry : d_check1.entrySet())
            Assert.assertTrue(test.containsKey(entry.getKey()));

        for (Map.Entry<String, Integer> entry : d_check2.entrySet())
            Assert.assertFalse(test.containsKey(entry.getKey()));

    }

    @Test
    public void getOrElseTest() throws DictionaryBuilderException {
        ImmutableStringIntMap test = new ImmutableStringIntMap.Builder().putAll(d_check1).build();

        for (Map.Entry<String, Integer> entry : d_check1.entrySet())
            Assert.assertEquals(entry.getValue().intValue(), test.getOrElse(entry.getKey(), -1));

        for (Map.Entry<String, Integer> entry : d_check2.entrySet())
            Assert.assertEquals(-1, test.getOrElse(entry.getKey(), -1));
    }

    @Test
    public void iteratorTest() throws DictionaryBuilderException {
        ImmutableStringIntMap test = new ImmutableStringIntMap.Builder().putAll(d_check1).build();

        Set<String> isiKeys = new HashSet<String>();
        Iterator<String> iter = test.keyIterator();
        while (iter.hasNext())
            isiKeys.add(iter.next());


        Assert.assertEquals(d_check1.keySet(), isiKeys);
    }


}
