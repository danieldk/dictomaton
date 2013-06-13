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

import com.google.common.collect.testing.MapInterfaceTest;
import eu.danieldk.dictomaton.DictionaryBuilderException;
import eu.danieldk.dictomaton.categories.Tests;
import org.junit.experimental.categories.Category;

import java.util.Map;

/**
 * Map interface invariant tests via Guava's {@link com.google.common.collect.testing.MapInterfaceTest}.
 */
@Category(Tests.class)
public class ImmutableStringStringMapInterfaceTest extends MapInterfaceTest<String, String>
{
	public ImmutableStringStringMapInterfaceTest()
	{
		super(false, false, false, false, false, false);
	}

	@Override
	protected Map<String, String> makeEmptyMap() throws UnsupportedOperationException
	{
		try
		{
			return new ImmutableStringStringMap.Builder().build();
		}
		catch (DictionaryBuilderException e)
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected Map<String, String> makePopulatedMap() throws UnsupportedOperationException
	{
		try
		{
			return new ImmutableStringStringMap.Builder().put("Miles Davis", "Jazz").put("John Zorn", "Avant-Garde")
					.put("Queens of the Stone Age", "Stoner Rock").put("The White Stripes", "Rock")
					.put("Beastie Boys", "Hip Hop").build();
		}
		catch (DictionaryBuilderException e)
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected String getKeyNotInPopulatedMap() throws UnsupportedOperationException
	{
		return "Mahavishnu Orchestra";
	}

	@Override
	protected String getValueNotInPopulatedMap() throws UnsupportedOperationException
	{
		return "Country";
	}

}
