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
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PerfectHashDictionaryTest {
	private List<String> d_words1;
	private List<String> d_words2;
	private PerfectHashDictionary d_dict;
	
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
		d_dict = builder.buildPerfectHash();
	}

    @Test
    public void emptyTest()
    {
        DictionaryBuilder builder = new DictionaryBuilder();
        PerfectHashDictionary dict = builder.buildPerfectHash();
        Assert.assertEquals(-1, dict.number("foo"));
        Assert.assertNull(dict.sequence(1));
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void sizeTest()
    {
        Assert.assertEquals(7, d_dict.size());
    }

	@Test
	public void toNumberTest()
	{
		for (int i = 0; i < d_words1.size(); i++)
			Assert.assertEquals(i + 1, d_dict.number(d_words1.get(i)));
		
		for (int i = 0; i < d_words2.size(); i++)
			Assert.assertEquals(-1, d_dict.number(d_words2.get(i)));
	}
	
	@Test
	public void toWordTest()
	{
		for (int i = 0; i < d_words1.size(); i++)
			Assert.assertEquals(d_words1.get(i), d_dict.sequence(i + 1));
	}
	
	@Test
	public void unknownHashTest()
	{
		Assert.assertNull(d_dict.sequence(0));
		Assert.assertNull(d_dict.sequence(d_words1.size() + 1));
	}
}
