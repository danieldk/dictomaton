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

package eu.danieldk.dictomaton.collections;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.danieldk.dictomaton.DictionaryBuilderException;
import eu.danieldk.dictomaton.Util;
import eu.danieldk.dictomaton.categories.Benchmarks;

@Category(Benchmarks.class)
public class ImmutableStringIntMapBenchmark extends AbstractBenchmark
{
	private static SortedSet<String> d_words;
	private ImmutableStringIntMap map;

	@BeforeClass
	public static void initializeExpensive() throws DictionaryBuilderException, IOException
	{
		d_words = Util.loadWordList("eu/danieldk/dictomaton/web2-1");
	}

	@Before
	public void initialize() throws DictionaryBuilderException
	{
		ImmutableStringIntMap.Builder builder = new ImmutableStringIntMap.Builder();

		int i = 0;
		for (String word : d_words)
		{
			builder.put(word, i++);
		}

		map = builder.build();
	}

	@Test
	public void getBenchmark()
	{
        int i = 0;
		for (String word : d_words)
            Assert.assertEquals(i++, (int) map.get(word));
	}
}
