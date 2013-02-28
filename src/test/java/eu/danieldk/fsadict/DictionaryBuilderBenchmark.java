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

package eu.danieldk.fsadict;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import eu.danieldk.fsadict.categories.Benchmarks;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Category(Benchmarks.class)
public class DictionaryBuilderBenchmark extends AbstractBenchmark {
    private List<String> d_words1;
    private List<String> d_words2;
    private Dictionary d_dict;
    private static SortedSet d_wordsLong;
    private List<String> d_wordsLong2;

    @BeforeClass
    public static void initializeExpensive() throws IOException
    {
        d_wordsLong = loadWordList("eu/danieldk/fsadict/web2");
    }

    @Before
    public void initialize() throws DictionaryBuilderException {
    }

    @Test
    public void treeSetConstructionTest() {
        TreeSet<String> ts = new TreeSet<String>();
        ts.addAll(d_wordsLong);
    }

    @Test
    public void dictionaryConstructionTest() throws DictionaryBuilderException {
        DictionaryBuilder builder = new DictionaryBuilder();
        builder.addAll(d_wordsLong);
        builder.build();
    }

    private static SortedSet<String> loadWordList(String resourceName) throws IOException {
        InputStream in = ClassLoader.getSystemResourceAsStream(resourceName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        TreeSet<String> words = new TreeSet<String>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }

        } finally {
            reader.close();
        }

        return words;
    }
}
