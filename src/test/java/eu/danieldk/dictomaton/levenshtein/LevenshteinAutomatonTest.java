package eu.danieldk.dictomaton.levenshtein;

import eu.danieldk.dictomaton.Dictionary;
import eu.danieldk.dictomaton.DictionaryBuilder;
import eu.danieldk.dictomaton.DictionaryBuilderException;
import eu.danieldk.dictomaton.categories.Tests;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

@Category(Tests.class)
public class LevenshteinAutomatonTest {
    private final char[] d_characters = {'a', 'b', 'c', 'd', 'e', 'f'};
    private final RandomEditOperation[] d_editOperations = {new RandomSubstitution(), new RanndomInsert(), new RandomDelete()};

    private final int MIN_LENGTH = 5;
    private final int MAX_LENGTH = 15;
    private final int N_PERMUTED_STRINGS = 1000;
    private final int N_REPETITIONS = 500;

    private Random d_rng;

    @Before
    public void initialize() {
        d_rng = new Random(42);
    }

    @Test
    public void intersectionLanguage1Test() throws DictionaryBuilderException {
        for (int attempt = 0; attempt < N_REPETITIONS; ++attempt)
            generateAndCheckPermutations(MIN_LENGTH, MAX_LENGTH, N_PERMUTED_STRINGS, 3, 1);
    }

    @Test
    public void intersectionLanguage2Test() throws DictionaryBuilderException {
        for (int attempt = 0; attempt < N_REPETITIONS; ++attempt)
            generateAndCheckPermutations(MIN_LENGTH, MAX_LENGTH, N_PERMUTED_STRINGS, 4, 2);
    }

    /**
     * Generate a word, create a dictionary of permutations permutations that are created using random edit operations,
     * and check that Levenshtein automaton for that word finds permutations within its edit distance.
     *
     * @param minLength             The minimum lenth of the generated word.
     * @param maxLength             The maximum length of the generated word.
     * @param nPermutations         The number of permutations to generate.
     * @param nRandomEditOperations The maximum number of random edit operations.
     * @param distance              Test the levenshtein automaton with this edit distance.
     * @throws DictionaryBuilderException
     */
    private void generateAndCheckPermutations(int minLength, int maxLength, int nPermutations, int nRandomEditOperations,
                                              int distance) throws DictionaryBuilderException {
        String str = randomString(minLength + (maxLength - minLength + 1));

        TreeSet<String> all = new TreeSet<>();
        Set<String> shouldHave = new HashSet<>();

        for (int i = 0; i < nPermutations; ++i) {
            int n = d_rng.nextInt(nRandomEditOperations);

            StringBuilder permutedBuilder = new StringBuilder(str);
            for (int perm = 0; perm < n; ++perm)
                d_editOperations[d_rng.nextInt(d_editOperations.length)].apply(permutedBuilder);

            String permuted = permutedBuilder.toString();

            all.add(permuted);

            if (StringUtils.getLevenshteinDistance(str, permuted) <= distance)
                shouldHave.add(permuted);
        }

        Dictionary dict = new DictionaryBuilder().addAll(all).build();
        LevenshteinAutomaton la = new LevenshteinAutomaton(str, distance);

        Assert.assertEquals(shouldHave, la.intersectionLanguage(dict));
    }

    private interface RandomEditOperation {
        void apply(StringBuilder string);
    }

    private class RandomSubstitution implements RandomEditOperation {

        public void apply(StringBuilder sb) {
            sb.setCharAt(d_rng.nextInt(sb.length()), d_characters[d_rng.nextInt(d_characters.length)]);
        }

    }

    private class RandomDelete implements RandomEditOperation {
        public void apply(StringBuilder sb) {
            sb.deleteCharAt(d_rng.nextInt(sb.length()));
        }
    }

    private class RanndomInsert implements RandomEditOperation {
        public void apply(StringBuilder sb) {
            sb.insert(d_rng.nextInt(sb.length() + 1),
                    d_characters[d_rng.nextInt(d_characters.length)]);
        }
    }

    private String randomString(int length) {
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < length; ++i)
            string.append(d_characters[d_rng.nextInt(d_characters.length)]);

        return string.toString();
    }
}
