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

import org.junit.Assert;
import org.junit.Test;

public class DictionaryBuilderTest {
	@Test(expected = DictionaryBuilderException.class)
	public void incorrectOrderTest() throws DictionaryBuilderException {
		DictionaryBuilder builder = new DictionaryBuilder();
		builder.add("ziezo");
		builder.add("oeps");
	}
	
	@Test(expected = DictionaryBuilderException.class)
	public void addToFinalizedTest() throws DictionaryBuilderException {
		DictionaryBuilder builder = new DictionaryBuilder();
		builder.add("oeps");
		builder.toDot();
		builder.add("oeps");
	}

	@Test
	public void testDot() throws DictionaryBuilderException {
		DictionaryBuilder builder = new DictionaryBuilder();
		
		builder.add("al");
		builder.add("alleen");
		builder.add("avonden");
		builder.add("zeemeeuw");
		builder.add("zeker");
		builder.add("zeven");
		builder.add("zoeven");
		
		String check = "digraph G { 12 -> 11 [label=\"v\"]; 0 -> 1 [label=\"a\"]; 0 -> 2 [label=\"z\"]; " +
				"10 -> 15 [label=\"e\"]; 20 -> 18 [label=\"w\"]; 9 -> 14 [label=\"m\"]; 19 -> 20 [label=\"u\"]; " +
				"18 [peripheries=2]; 11 -> 16 [label=\"e\"]; 8 -> 13 [label=\"n\"]; 14 -> 17 [label=\"e\"]; " +
				"4 -> 8 [label=\"o\"]; 7 -> 11 [label=\"e\"]; 13 -> 11 [label=\"d\"]; 1 -> 3 [label=\"l\"]; " +
				"1 -> 4 [label=\"v\"]; 6 -> 12 [label=\"e\"]; 15 -> 18 [label=\"r\"]; 2 -> 5 [label=\"e\"]; " +
				"2 -> 6 [label=\"o\"]; 16 -> 18 [label=\"n\"]; 17 -> 19 [label=\"e\"]; 3 [peripheries=2]; " +
				"3 -> 7 [label=\"l\"]; 5 -> 9 [label=\"e\"]; 5 -> 10 [label=\"k\"]; 5 -> 11 [label=\"v\"]; }";
		
		Assert.assertEquals(check, builder.toDot().replace('\n', ' '));
	}
}
