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

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import eu.danieldk.dictomaton.categories.Benchmarks;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

@Category(Benchmarks.class)
public class DictionaryBuilderBenchmark extends AbstractBenchmark {
    private static SortedSet<String> d_wordsLong;

    @BeforeClass
    public static void initializeExpensive() throws IOException {
        d_wordsLong = Util.loadWordList("eu/danieldk/dictomaton/web2-1");
        d_wordsLong.addAll(Util.loadWordList("eu/danieldk/dictomaton/web2-2"));
    }

    @Test
    public void treeSetConstructionTest() {
        TreeSet<String> ts = new TreeSet<String>();
        ts.addAll(d_wordsLong);
    }

    @Test
    public void dictionaryConstructionTest() throws DictionaryBuilderException {
        new DictionaryBuilder().addAll(d_wordsLong).build();
    }
}
