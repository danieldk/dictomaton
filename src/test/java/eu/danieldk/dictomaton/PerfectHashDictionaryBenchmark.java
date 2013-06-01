package eu.danieldk.dictomaton;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import eu.danieldk.dictomaton.categories.Benchmarks;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.SortedSet;

@Category(Benchmarks.class)
public class PerfectHashDictionaryBenchmark extends AbstractBenchmark {
    private static SortedSet<String> d_words1;
    private static SortedSet<String> d_words2;
    private static PerfectHashDictionary d_dict;
    private static PerfectHashDictionary d_transCardDict;

    @BeforeClass
    public static void initializeExpensive() throws DictionaryBuilderException, IOException {
        d_words1 = Util.loadWordList("eu/danieldk/dictomaton/web2-1");
        d_words2 = Util.loadWordList("eu/danieldk/dictomaton/web2-2");

        d_dict = new DictionaryBuilder().addAll(d_words1).buildPerfectHash();
        d_transCardDict = new DictionaryBuilder().addAll(d_words1).buildPerfectHash(false);
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
    public void wordToNumberTransCardBenchmark() {
        int i = 1;
        for (String word : d_words1) {
            Assert.assertEquals(i, d_transCardDict.number(word));
            ++i;
        }

        for (String word : d_words2)
            Assert.assertEquals(-1, d_transCardDict.number(word));

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
