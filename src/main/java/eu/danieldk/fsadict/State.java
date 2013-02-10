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

import java.util.Map.Entry;
import java.util.TreeMap;

public class State {
	private final TreeMap<Character, State> transitions;
	private boolean d_final;
	
	public State()
	{
		transitions = new TreeMap<Character, State>();
		d_final = false;
	}

	public void addTransition(Character c, State s)
	{
		transitions.put(c, s);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (d_final ? 1231 : 1237);
		result = prime * result
				+ ((transitions == null) ? 0 : transitions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (d_final != other.d_final)
			return false;
		if (transitions == null) {
			if (other.transitions != null)
				return false;
		} else if (!transitions.equals(other.transitions))
			return false;
		return true;
	}

	public boolean isFinal()
	{
		return d_final;
	}
	
	public boolean hasOutgoing()
	{
		return transitions.size() != 0;
	}
	
	public State lastState()
	{
		return transitions.lastEntry().getValue();
	}
	
	public void setLastState(State s)
	{
		Entry<Character, State> entry = transitions.lastEntry();
		transitions.put(entry.getKey(), s);
	}
	
	public TreeMap<Character, State> transitions()
	{
		return transitions;
	}
	
	public State move(Character c)
	{
		return transitions.get(c);
	}
	
	void setFinal(boolean finalState)
	{
		d_final = finalState;
	}
}
