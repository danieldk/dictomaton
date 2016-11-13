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

package eu.danieldk.dictomaton.levenshtein;

import java.util.*;
import java.util.Map.Entry;

/**
 * State representation of Levenshtein automata.
 */
class LevenshteinAutomatonState {
    private final TreeMap<Character, LevenshteinAutomatonState> transitions;
    private boolean d_final;
    private boolean d_recomputeHash;
    private int d_cachedHash;

    /**
     * Construct a state. The state will have no transitions and will be non-final.
     */
    public LevenshteinAutomatonState() {
        transitions = new TreeMap<>();
        d_final = false;
        d_recomputeHash = true;
    }

    /**
     * Add a transition to the state. If a transition with the provided character already
     * exists, it will be replaced.
     *
     * @param c The transition character.
     * @param s The to-state.
     */
    public void addTransition(Character c, LevenshteinAutomatonState s) {
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
        result = prime * result + ((transitions == null) ? 0 : transitionsHashCode());

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

        LevenshteinAutomatonState other = (LevenshteinAutomatonState) obj;
        if (d_final != other.d_final)
            return false;

        if (transitions == null)
            return other.transitions == null;

        return transitions.equals(other.transitions);
    }

    /**
     * Returns <tt>true</tt> if the state is a final state.
     *
     * @return <tt>true</tt> if the state is a final state, <tt>false</tt> otherwise.
     */
    public boolean isFinal() {
        return d_final;
    }

    /**
     * Obtain the transitions of this state. This method does not return a copy. Modifying the transition
     * map may make the internal state inconsistent.
     *
     * @return The transition map.
     */
    public TreeMap<Character, LevenshteinAutomatonState> transitions() {
        return transitions;
    }

    /**
     * Follow a transition.
     *
     * @param c The character.
     * @return The target state of the transition, <tt>null</tt> if there is no transition with the given character.
     */
    public LevenshteinAutomatonState move(Character c) {
        return transitions.get(c);
    }

    /**
     * Reduce the set of outgoing transitions by removing transitions that are also captured by the
     * 'other'-transition. The 'other'-transition are transitions with the given character.
     *
     * @param otherChar The character representing any other character.
     */
    public void reduce(Character otherChar) {
        Set<Integer> seen = new HashSet<>();
        Queue<LevenshteinAutomatonState> q = new LinkedList<>();
        q.add(this);

        while (!q.isEmpty()) {
            LevenshteinAutomatonState s = q.poll();
            if (seen.contains(System.identityHashCode(s)))
                continue;

            LevenshteinAutomatonState otherTo = s.transitions.get(otherChar);
            if (otherTo == null) {
                // There is no reduction possible in this state: queue the to-states, mark this state
                // as seen and continue with the next state.
                for (LevenshteinAutomatonState toState : s.transitions.values())
                    q.add(toState);

                seen.add(System.identityHashCode(s));

                continue;
            }

            // Find transitions that can be removed, because they are handled by an 'other' transition. This
            // is the case when a transition is not the 'other' transition, but has the same to-state as the
            // 'other' transition.
            Set<Character> remove = new HashSet<>();
            for (Entry<Character, LevenshteinAutomatonState> trans : s.transitions.entrySet())
                if (!trans.getKey().equals(otherChar) && trans.getValue() == otherTo)
                    remove.add(trans.getKey());

            // Remove the transitions that were found.
            for (Character c : remove)
                s.transitions.remove(c);

            // Recompute the hash if the state table has changed.
            if (remove.size() != 0)
                s.d_recomputeHash = true;

            // We have now seen this state.
            seen.add(System.identityHashCode(s));

            // Queue states that can be reached via this state.
            for (LevenshteinAutomatonState toState : s.transitions.values())
                q.add(toState);
        }
    }

    /**
     * Set the 'finalness' of the state.
     *
     * @param finalState If <tt>true</tt>, the state is set to be final. Otherwise, it is non-final.
     */
    public void setFinal(boolean finalState) {
        d_final = finalState;
        d_recomputeHash = true;
    }

    /**
     * Compute the hashcode for transitions. We do not rely on the{@link java.util.TreeMap#hashCode()},
     * because it will recursively compute the hashcodes of the to-states, which is not necessary since
     * two states are only identical when they have the same symbols leading to exactly the same objects.
     *
     * @return The hashcode of the transition table.
     */
    private int transitionsHashCode() {
        int hc = 0;

        for (Entry<Character, LevenshteinAutomatonState> e : transitions.entrySet())
            hc += e.getKey().hashCode() + System.identityHashCode(e.getValue());

        return hc;
    }
}
