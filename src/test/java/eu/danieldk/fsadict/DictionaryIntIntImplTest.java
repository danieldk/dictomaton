// Copyright 2013 Daniël de Kok
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DictionaryIntIntImplTest {
	private List<String> d_words1;
	private List<String> d_words2;
	private Dictionary d_dict;

	@SuppressWarnings("serial")
	@Before
	public void initialize() throws DictionaryBuilderException
	{
		d_words1 = new ArrayList<String>(){{
			add("al");
			add("alleen");
			add("avonden");
			add("zeemeeuw");
			add("zeker");
			add("zeven");
			add("zoeven");
		}};

		d_words2 = new ArrayList<String>(){{
			add("als");
			add("allen");
			add("avond");
			add("zeemeeuwen");
			add("zeer");
			add("zepen");
			add("zoef");
		}};

		DictionaryBuilder builder = new DictionaryBuilder();
		builder.addAll(d_words1);
		d_dict = builder.build();
	}

	@Test
	public void containsWordsTest()
	{
		for (String word: d_words1)
			Assert.assertTrue(d_dict.contains(word));
	}

	@Test
	public void doesNotContainWordsTest()
	{
		for (String word: d_words2)
			Assert.assertTrue(!d_dict.contains(word));
	}

	@Test
	public void iterationTest()
	{
		List<CharSequence> listFromIteration = new LinkedList<CharSequence>();
		for (CharSequence seq: d_dict)
			listFromIteration.add(seq);
		Assert.assertEquals(d_words1, listFromIteration);
	}
}
