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

package eu.danieldk.dictomaton.levenshtein;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import eu.danieldk.dictomaton.DictionaryBuilderException;
import eu.danieldk.dictomaton.Util;
import eu.danieldk.dictomaton.categories.Benchmarks;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;

@Category(Benchmarks.class)
public class LevenshteinAutomatonBenchmark extends AbstractBenchmark {
    private static SortedSet<String> d_words;

    @BeforeClass
    public static void initializeExpensive() throws DictionaryBuilderException, IOException {
        d_words = Util.loadWordList("eu/danieldk/dictomaton/web2-1");
    }

    @Test
    public void distance1Benchmark() {
        int i = 0;
        Iterator<String> iter = d_words.iterator();
        while (i++ < 10000 && iter.hasNext()) {
            String word = iter.next();
            Assert.assertNotNull(new LevenshteinAutomaton(word, 1));
        }
    }

    @Test
    public void distance2Benchmark() {
        int i = 0;
        Iterator<String> iter = d_words.iterator();
        while (i++ < 10000 && iter.hasNext()) {
            String word = iter.next();
            Assert.assertNotNull(new LevenshteinAutomaton(word, 2));
        }
    }
}
