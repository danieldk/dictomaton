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

import java.util.Set;

/**
 * A finite state dictionary. Dictionaries of this type can are constructed
 * using {@link DictionaryBuilder}.
 * 
 * @author Daniël de Kok 
 */
public class Dictionary {
	// Offset in the transition table of the given state. E.g. d_stateOffsets[3] = 10
	// means that state 3 starts at index 10 in the transition table.
	protected final int[] d_stateOffsets;
	
	// Note: we do not use an array of transition instances to represent the
	//       transition table, since this would require an additional pointer
	//       for each transition. Instead, we maintain the table as two parallel
	//       arrays.
	
	protected final char[] d_transitionChars;
	protected final int[] d_transtitionTo;
	protected final Set<Integer> d_finalStates;
	
	/**
	 * Check whether the dictionary contains the given sequence.
	 * @param seq
	 * @return
	 */
	public boolean contains(CharSequence seq)
	{
		int state = 0;
		for (int i = 0; i < seq.length(); i++)
		{
			state = next(state, seq.charAt(i));
			
			if (state == -1)
				return false;
		}
		
		return d_finalStates.contains(state);
	}

	/**
	 * Give the Graphviz dot representation of this automaton.
	 * @return
	 */
	public String toDot()
	{
		StringBuilder dotBuilder = new StringBuilder();
		
		dotBuilder.append("digraph G {\n");
		
		for (int state = 0; state < d_stateOffsets.length; ++state)
		{
			for (int trans = d_stateOffsets[state]; trans < transitionsUpperBound(state); ++trans)
				dotBuilder.append(String.format("%d -> %d [label=\"%c\"]\n",
						state, d_transtitionTo[trans], d_transitionChars[trans]));
			
			if (d_finalStates.contains(state))
				dotBuilder.append(String.format("%d [peripheries=2];\n", state));
		}
		
		dotBuilder.append("}");
		
		return dotBuilder.toString();
	}

	/**
	 * Construct a dictionary.
	 * 
	 * @param stateOffsets Per-state offset in the transition table.
	 * @param transitionChars Transition table (characters).
	 * @param transitionTo Transition table (to-transitions).
	 * @param finalStates Set of final states.
	 */
	protected Dictionary(int[] stateOffsets, char[] transitionChars,
			int[] transitionTo, Set<Integer> finalStates)
	{
		d_stateOffsets = stateOffsets;
		d_transitionChars = transitionChars;
		d_transtitionTo = transitionTo;
		d_finalStates = finalStates;
	}

	/**
	 * Calculate the upper bound for this state in the transition table.
	 * 
	 * @param state
	 * @return
	 */
	protected int transitionsUpperBound(int state)
	{
		return state + 1 < d_stateOffsets.length ? d_stateOffsets[state + 1] :
			d_transitionChars.length;
	}

	/**
	 * Find the transition for the given character in the given state. Since the
	 * transitions are ordered by character, we can use a binary search.
	 * 
	 * @param state
	 * @param c
	 * @return
	 */
	protected int findTransition(int state, char c)
	{
		int start = d_stateOffsets[state];
		int end = transitionsUpperBound(state) - 1;
		
		// Binary search
		while (end >= start)
		{
			int mid = start + ((end - start) / 2);
		
			if (d_transitionChars[mid] > c)
				end = mid - 1;
			else if (d_transitionChars[mid] < c)
				start = mid + 1;
			else
				return mid;
		}
	
		return -1;
	}

	/**
	 * Get the next state, given a character.
	 * 
	 * @param state
	 * @param c
	 * @return
	 */
	private int next(int state, char c)
	{
		int trans = findTransition(state, c);
		
		if (trans == -1)
			return -1;
		
		return d_transtitionTo[trans];
	}

}
