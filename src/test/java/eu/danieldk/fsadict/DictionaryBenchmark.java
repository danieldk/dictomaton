package eu.danieldk.fsadict;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import eu.danieldk.fsadict.categories.Benchmarks;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.HashSet;
import java.util.SortedSet;

@Category(Benchmarks.class)
public class DictionaryBenchmark extends AbstractBenchmark {
    private static SortedSet<String> d_words1;
    private static SortedSet<String> d_words2;
    private static Dictionary d_dict;
    private static HashSet<String> d_hashSet;

    @BeforeClass
    public static void initializeExpensive() throws DictionaryBuilderException, IOException {
        d_words1 = Util.loadWordList("eu/danieldk/fsadict/web2-1");
        d_words2 = Util.loadWordList("eu/danieldk/fsadict/web2-2");

        DictionaryBuilder builder = new DictionaryBuilder();
        builder.addAll(d_words1);
        d_dict = builder.build();

        d_hashSet = new HashSet<String>(d_words1);
    }

    @Test
    public void dictionaryContainsBenchmark() {
        d_dict.containsAll(d_words1);
    }

    @Test
    public void hashSetContainsBenchmark() {
        d_hashSet.containsAll(d_words1);
    }
}
