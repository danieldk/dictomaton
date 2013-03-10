package eu.danieldk.fsadict.collections;

import com.google.common.collect.testing.MapInterfaceTest;
import eu.danieldk.fsadict.DictionaryBuilderException;
import eu.danieldk.fsadict.categories.Tests;
import org.junit.experimental.categories.Category;

import java.util.Map;

/**
 * Map interface invariant tests via Guava's {@link MapInterfaceTest}.
 */
@Category(Tests.class)
public class ImmutableStringIntMapInterfaceTest extends MapInterfaceTest<String, Integer> {
    public ImmutableStringIntMapInterfaceTest() {
        super(false, false, false, false, false, false);
    }

    @Override
    protected Map<String, Integer> makeEmptyMap() throws UnsupportedOperationException {
        try {
            return new ImmutableStringIntMap.Builder().build();
        } catch (DictionaryBuilderException e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected Map<String, Integer> makePopulatedMap() throws UnsupportedOperationException {
        try {
        return new ImmutableStringIntMap.Builder()
                .put("Miles Davis", 9)
                .put("John Zorn", 9)
                .put("Thelonious Monk", 8)
                .put("The White Stripes", 8)
                .put("Canned Heat", 3).build();
        } catch (DictionaryBuilderException e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected String getKeyNotInPopulatedMap() throws UnsupportedOperationException {
        return "Mahavishnu Orchestra";
    }

    @Override
    protected Integer getValueNotInPopulatedMap() throws UnsupportedOperationException {
        return 5;
    }


}
