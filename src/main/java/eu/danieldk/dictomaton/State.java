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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

class State {
    private final TreeMap<Character, State> transitions;
    private boolean d_final;
    private boolean d_recomputeHash;
    private int d_cachedHash;

    public State() {
        transitions = new TreeMap<Character, State>();
        d_final = false;
        d_recomputeHash = true;
    }

    public void addTransition(Character c, State s) {
        transitions.put(c, s);
        d_recomputeHash = true;
    }

    @Override
    public int hashCode() {
        if (!d_recomputeHash)
            return d_cachedHash;

        final int prime = 31;
        int result = 1;
        result = prime * result + (d_final ? 1231 : 1237);
        result = prime * result
                + ((transitions == null) ? 0 : transitionsHashCode());

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
        if (transitions == null) {
            if (other.transitions != null)
                return false;
        }

        return transitions.equals(other.transitions);
    }

    public boolean isFinal() {
        return d_final;
    }

    public boolean hasOutgoing() {
        return transitions.size() != 0;
    }

    public State lastState() {
        Entry<Character, State> last = transitions.lastEntry();
        if (last == null)
            return null;

        return last.getValue();
    }

    public void setLastState(State s) {
        Entry<Character, State> entry = transitions.lastEntry();
        transitions.put(entry.getKey(), s);
        d_recomputeHash = true;
    }

    public TreeMap<Character, State> transitions() {
        return transitions;
    }

    public State move(Character c) {
        return transitions.get(c);
    }

    void setFinal(boolean finalState) {
        d_final = finalState;
        d_recomputeHash = true;
    }

    /**
     * Return the hashcode 'computed' by {@link Object#hashCode()}.
     * @return The hashcode.
     */
    private int objectHashCode() {
        return super.hashCode();
    }

    /**
     * Compute the hashcode for transitions. We do not rely on the{@link java.util.TreeMap#hashCode()},
     * because it will recursively compute the hashcodes of the to-states, which is not necessary since
     * two states are only identical when they have the same symbols leading to exactly the same objects.
     *
     * @return
     */
    private int transitionsHashCode() {
        int hc = 0;

        Iterator<Entry<Character, State>> iter = transitions.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Character, State> e = iter.next();
            hc += e.getKey().hashCode() + e.getValue().objectHashCode();
        }

        return hc;
    }
}
