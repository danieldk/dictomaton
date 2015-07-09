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

import java.lang.reflect.Array;
import java.util.*;

/**
 * <p>
 * A finite state dictionary. Dictionaries of this type can are constructed
 * using {@link eu.danieldk.dictomaton.DictionaryBuilder#build()}.
 * </p>
 * <p>
 * This class uses integers (int) for transition and state numbers.
 * </p>
 * @author Daniel de Kok
 */
class DictionaryImpl extends AbstractSet<String> implements Dictionary {
    private static final long serialVersionUID = 2L;

    // Offset in the transition table of the given state. E.g. d_stateOffsets[3] = 10
    // means that state 3 starts at index 10 in the transition table.
    protected final CompactIntArray d_stateOffsets;

    // Note: we do not use an array of transition instances to represent the
    //       transition table, since this would require an additional pointer
    //       for each transition. Instead, we maintain the table as two parallel
    //       arrays.

    protected final char[] d_transitionChars;
    protected final CompactIntArray d_transitionTo;
    protected final BitSet d_finalStates;
    protected final int d_nSeqs;

    @Override
    public boolean add(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof String))
            return false;

        String seq = (String) o;

        return containsSeq(seq);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!contains(o))
                return false;

        return true;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean isFinalState(int state) {
        return d_finalStates.get(state);
    }

    @Override
    public Iterator<String> iterator() {
        return new DictionaryIterator();
    }

    @Override
    public int next(int state, char c) {
        int trans = findTransition(state, c);

        if (trans == -1)
            return -1;

        return d_transitionTo.get(trans);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return d_nSeqs;
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        T[] r = a.length >= d_nSeqs ? a : (T[]) Array.newInstance(a.getClass().getComponentType(), d_nSeqs);

        Iterator<String> iter = iterator();
        int idx = 0;

        while (iter.hasNext()) {
            r[idx] = (T) iter.next();
            ++idx;
        }

        if (r.length > d_nSeqs)
            r[size()] = null;

        return r;
    }

    /**
     * Give the Graphviz dot representation of this automaton.
     *
     * @return
     */
    public String toDot() {
        StringBuilder dotBuilder = new StringBuilder();

        dotBuilder.append("digraph G {\n");

        for (int state = 0; state < d_stateOffsets.size(); ++state) {
            for (int trans = d_stateOffsets.get(state); trans < transitionsUpperBound(state); ++trans)
                dotBuilder.append(String.format("%d -> %d [label=\"%c\"]\n",
                        state, d_transitionTo.get(trans), d_transitionChars[trans]));

            if (d_finalStates.get(state))
                dotBuilder.append(String.format("%d [peripheries=2];\n", state));
        }

        dotBuilder.append("}");

        return dotBuilder.toString();
    }

    @Override
    public int startState() {
        return 0;
    }

    @Override
    public Set<Character> transitionCharacters(int state) {
        Set<Character> transChars = new HashSet<Character>();

        for (int i = d_stateOffsets.get(state); i < transitionsUpperBound(state); ++i)
            transChars.add(d_transitionChars[i]);

        return transChars;
    }

    private class DictionaryIterator implements Iterator<String> {
        private final Stack<StateStringPair> d_stack;

        public DictionaryIterator() {
            d_stack = new Stack<StateStringPair>();
            d_stack.push(new StateStringPair(0, ""));
        }

        @Override
        public boolean hasNext() {
            if (d_stack.isEmpty() || d_nSeqs == 0)
                return false;

            return true;
        }

        @Override
        public String next() {
            if (d_stack.isEmpty() || d_nSeqs == 0)
                throw new NoSuchElementException();

            StateStringPair pair;
            while (d_stack.size() != 0) {
                pair = d_stack.pop();
                int state = pair.getState();
                String string = pair.getString();

                // Put states reachable through outgoing transitions on the stack.
                for (int trans = transitionsUpperBound(state) - 1; trans >= d_stateOffsets.get(state); --trans)
                    d_stack.push(new StateStringPair(d_transitionTo.get(trans), string + d_transitionChars[trans]));

                if (d_finalStates.get(state))
                    return string;
            }

            // Impossible to reach.
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private class StateStringPair {
        private final int d_state;

        private final String d_string;

        public StateStringPair(int state, String string) {
            d_state = state;
            d_string = string;
        }

        public int getState() {
            return d_state;
        }

        public String getString() {
            return d_string;
        }

    }

    /**
     * Construct a dictionary.
     *
     * @param stateOffsets    Per-state offset in the transition table.
     * @param transitionChars Transition table (characters).
     * @param transitionTo    Transition table (to-transitions).
     * @param finalStates     Set of final states.
     */
    protected DictionaryImpl(CompactIntArray stateOffsets, char[] transitionChars,
                             CompactIntArray transitionTo, BitSet finalStates,
                             int nSeqs) {
        d_stateOffsets = stateOffsets;
        d_transitionChars = transitionChars;
        d_transitionTo = transitionTo;
        d_finalStates = finalStates;
        d_nSeqs = nSeqs;
    }

    /**
     * Calculate the upper bound for this state in the transition table.
     *
     * @param state
     * @return
     */
    protected int transitionsUpperBound(int state) {
        return state + 1 < d_stateOffsets.size() ? d_stateOffsets.get(state + 1) :
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
    protected int findTransition(int state, char c) {
        int start = d_stateOffsets.get(state);
        int end = transitionsUpperBound(state) - 1;

        // Binary search
        while (end >= start) {
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
     * Check whether the dictionary contains the given sequence.
     *
     * @param seq
     * @return
     */
    private boolean containsSeq(String seq) {
        int state = 0;
        for (int i = 0; i < seq.length(); i++) {
            state = next(state, seq.charAt(i));

            if (state == -1)
                return false;
        }

        return d_finalStates.get(state);
    }

}
