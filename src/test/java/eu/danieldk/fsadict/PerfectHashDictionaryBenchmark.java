package eu.danieldk.fsadict;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import eu.danieldk.fsadict.categories.Benchmarks;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.HashSet;
import java.util.SortedSet;

@Category(Benchmarks.class)
public class PerfectHashDictionaryBenchmark extends AbstractBenchmark {
    private static SortedSet<String> d_words1;
    private static SortedSet<String> d_words2;
    private static PerfectHashDictionary d_dict;

    @BeforeClass
    public static void initializeExpensive() throws DictionaryBuilderException, IOException {
        d_words1 = Util.loadWordList("eu/danieldk/fsadict/web2-1");
        d_words2 = Util.loadWordList("eu/danieldk/fsadict/web2-2");

        DictionaryBuilder builder = new DictionaryBuilder();
        builder.addAll(d_words1);
        d_dict = builder.buildPerfectHash();
    }

    @Test
    public void wordToNumberBenchmark() {
        int i = 1;
        for (String word : d_words1) {
            Assert.assertEquals(i, d_dict.number(word));
            ++i;
        }

        for (String word : d_words2)
            Assert.assertEquals(-1, d_dict.number(word));

    }

    @Test
    public void numberToWordBenchmark() {
        int i = 1;
        for (String word : d_words1) {
            Assert.assertEquals(word, d_dict.sequence(i));
            i++;
        }
    }
}
