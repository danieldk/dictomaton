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

import java.util.Map.Entry;
import java.util.TreeMap;

class State {
    private final TreeMap<Character, State> d_transitions;
    private boolean d_final;
    private boolean d_recomputeHash;
    private int d_cachedHash;

    public State() {
        d_transitions = new TreeMap<Character, State>();
        d_final = false;
        d_recomputeHash = true;
    }

    private State(TreeMap<Character, State> d_transitions, boolean d_final, boolean d_recomputeHash, int d_cachedHash) {
        this.d_transitions = d_transitions;
        this.d_final = d_final;
        this.d_recomputeHash = d_recomputeHash;
        this.d_cachedHash = d_cachedHash;
    }

    public void addTransition(Character c, State s) {
        d_transitions.put(c, s);
        d_recomputeHash = true;
    }

    public State clone()
    {
        TreeMap<Character, State> newTrans = (TreeMap<Character, State>) d_transitions.clone();

        return new State(newTrans, d_final, d_recomputeHash, d_cachedHash);
    }

    @Override
    public int hashCode() {
        if (!d_recomputeHash)
            return d_cachedHash;

        final int prime = 31;
        int result = 1;
        result = prime * result + (d_final ? 1231 : 1237);
        result = prime * result
                + ((d_transitions == null) ? 0 : d_transitions.hashCode());

        d_recomputeHash = false;
        d_cachedHash = result;

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
        if (d_transitions == null) {
            if (other.d_transitions != null)
                return false;
        } else if (!d_transitions.equals(other.d_transitions))
            return false;
        return true;
    }

    public boolean isFinal() {
        return d_final;
    }

    public boolean hasOutgoing() {
        return d_transitions.size() != 0;
    }

    public State lastState() {
        Entry<Character, State> last = d_transitions.lastEntry();
        if (last == null)
            return null;

        return last.getValue();
    }

    public void setLastState(State s) {
        Entry<Character, State> entry = d_transitions.lastEntry();
        d_transitions.put(entry.getKey(), s);
        d_recomputeHash = true;
    }

    public TreeMap<Character, State> transitions() {
        return d_transitions;
    }

    public State move(Character c) {
        return d_transitions.get(c);
    }

    void setFinal(boolean finalState) {
        d_final = finalState;
        d_recomputeHash = true;
    }
}
