package eu.danieldk.dictomaton.collections;

import java.util.HashMap;
import java.util.Map;

import eu.danieldk.dictomaton.categories.Tests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.danieldk.dictomaton.DictionaryBuilderException;
import org.junit.experimental.categories.Category;

@Category(Tests.class)
public class ImmutableStringStringMapTest
{
	private Map<String, String> locations;

	@Before
	public void initialize()
	{
		locations = new HashMap<>();
		locations.put("New York", "USA");
		locations.put("Amsterdam", "The Netherlands");
	}

	@Test
	public void getTest() throws DictionaryBuilderException
	{
		Map<String, String> iss = new ImmutableStringStringMap.Builder().putAll(locations).build();

		Assert.assertEquals(2, iss.size());
		Assert.assertEquals("USA", iss.get("New York"));
		Assert.assertEquals("The Netherlands", iss.get("Amsterdam"));
	}

	@Test
	public void equalsTest() throws DictionaryBuilderException
	{
		Map<String, String> iss = new ImmutableStringStringMap.Builder().putAll(locations).build();
		Assert.assertEquals(locations, iss);
	}
}
