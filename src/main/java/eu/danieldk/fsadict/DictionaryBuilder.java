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

import java.util.*;
import java.util.Map.Entry;

/**
 * This class is used to construct a dictionary in the form of a minimized
 * deterministic finite state automaton. This builder can create a normal
 * dictionary automaton ({@link Dictionary} or a perfect hash automaton
 * ({@link PerfectHashDictionary}).
 * <p/>
 * The workflow is simple:
 * <p/>
 * <ul>
 * <li>Create an instance of this class.</li>
 * <li>Add character sequences in lexicographic order using {@link DictionaryBuilder#add(String)}.</li>
 * <li>Construct the automaton with {@link DictionaryBuilder#build()} or {@link DictionaryBuilder#buildPerfectHash()}.</li>
 * </ul>
 * <p/>
 * Construction of the automaton finalizes the build process - it is not possible to add new character sequences
 * afterwards.
 * <p/>
 * The following construction algorithm is used:
 * </p>
 * <i>Incremental Construction of Minimal Acyclic Finite-State Automata</i>, Jan Daciuk, Bruce W. Watson,
 * Stoyan Mihov, and Robert E. Watson, 2000, Association for Computational Linguistics
 *
 * @author Daniel de Kok
 */
public class DictionaryBuilder {
    private State d_startState;
    private final Map<State, State> d_register;
    private int d_nSeqs;

    /**
     * Construct a {@link DictionaryBuilder}.
     */
    public DictionaryBuilder() {
        d_startState = new State();
        d_register = new HashMap<State, State>();
        d_nSeqs = 0;
    }

    /**
     * Add a character sequence.
     *
     * @param seq The sequence.
     */
    public DictionaryBuilder add(String seq) throws DictionaryBuilderException {
        if (contains(seq))
            return this;

        // New start state.
        State clonedStart = d_startState.clone();

        createCloneAndQueueStates(seq, clonedStart);

        removeUnreachableStates(seq, clonedStart);

        d_startState = clonedStart;

        replaceOrRegister(d_startState, seq);

        ++d_nSeqs;

        return this;
    }

    /**
     * Add all sequences from a lexicographically sorted collection.
     *
     * @param seqs A collection of sequences.
     * @throws DictionaryBuilderException
     */
    public DictionaryBuilder addAll(Collection<String> seqs) throws DictionaryBuilderException {
        for (String seq : seqs)
            add(seq);

        return this;
    }

    /**
     * Create a dictionary automaton.
     *
     * @return A finite state dictionary.
     */
    public Dictionary build() {
        return build(false);
    }

    /**
     * Create a perfect hash automaton. This also finalizes the {@link DictionaryBuilder}.
     *
     * @return A perfect hash automaton.
     */
    public PerfectHashDictionary buildPerfectHash() {
        return (PerfectHashDictionary) build(true);
    }

    /**
     * Check if the sequence is already in the automaton.
     *
     * @param seq The sequence.
     * @return <tt>true</tt> if the sequence is in the automaton, <tt>false</tt> otherwise.
     */
    private boolean contains(String seq) {
        State curState = d_startState;
        for (int i = 0; i < seq.length(); ++i)
            if ((curState = curState.move(seq.charAt(i))) == null)
                return false;

        return curState.isFinal();
    }

    private void createCloneAndQueueStates(String seq, State clonedStart) {
        State last = clonedStart;
        int i = 0;
        for (; i < seq.length(); i++) {
            State nextState = last.move(seq.charAt(i));
            if (nextState == null)
                break;

            // Replace transition to transition to the new cloned state.
            nextState = nextState.clone();
            last.addTransition(seq.charAt(i), nextState);

            last = nextState;
        }

        addSuffix(last, seq.substring(i));
    }

    private Set<State> getReachableStates(State start) {
        Queue<State> stateQueue = new LinkedList<State>();
        stateQueue.add(start);

        Set<State> reachableStates = new HashSet<State>();
        while (!stateQueue.isEmpty()) {
            State s = stateQueue.poll();
            if (!reachableStates.contains(s)) {
                reachableStates.add(s);
                stateQueue.addAll(s.transitions().values());
            }
        }

        return reachableStates;
    }

    /**
     * Remove unreachable states from the automaton.
     *
     * @param seq         The sequence being inserted.
     * @param clonedStart The new (cloned) start state.
     */
    private void removeUnreachableStates(String seq, State clonedStart) {
        State current = d_startState;
        Set<State> reachable = getReachableStates(clonedStart);

        for (int i = 0; i < seq.length() && current != null && !reachable.contains(current); ++i) {
            d_register.remove(current);
            current = current.move(seq.charAt(i));
        }

        if (!reachable.contains(current))
            d_register.remove(current);
    }

    /**
     * Get the number of sequences in the automaton builder.
     *
     * @return The number of sequences.
     */
    public int size() {
        return d_nSeqs;
    }

    /**
     * Obtain a Graphviz dot representation of the automaton. This finalizes the {@link DictionaryBuilder}.
     *
     * @return Dot representation of the automaton.
     */
    public String toDot() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("digraph G {\n");

        Map<State, Integer> stateNumbers = numberedStates();

        // We want to traverse states a fixed order, so that the output is predictable. We
        // could also store the numbered states in a TreeMap, but State doesn't implement
        // Comparable, and I wouldn't even know what that would mean ;).
        State[] states = new State[stateNumbers.size()];

        for (Entry<State, Integer> numberedState : stateNumbers.entrySet())
            states[numberedState.getValue()] = numberedState.getKey();

        for (int stateNumber = 0; stateNumber < states.length; ++stateNumber) {
            State s = states[stateNumber];

            if (s.isFinal())
                stringBuilder.append(String.format("%d [peripheries=2];\n", stateNumber));

            for (Entry<Character, State> trans : s.transitions().entrySet())
                stringBuilder.append(String.format("%d -> %d [label=\"%c\"];\n", stateNumber,
                        stateNumbers.get(trans.getValue()), trans.getKey()));
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private void addSuffix(State s, String suffix) {
        for (int i = 0; i < suffix.length(); i++) {
            State newState = new State();
            s.addTransition(suffix.charAt(i), newState);
            s = newState;
        }

        // s is now a final state.
        s.setFinal(true);
    }

    private Dictionary build(boolean perfectHash) {
        Map<State, Integer> stateNumbers = numberedStates();
        State[] sList = stateList(stateNumbers);

        // First compute the offsets of each state in the state table.
        int[] offsets = new int[stateNumbers.size()];
        for (int i = 1; i < stateNumbers.size(); i++)
            offsets[i] = offsets[i - 1] + sList[i - 1].transitions().size();

        // Create transition tables.
        int nTransitions = offsets[offsets.length - 1] + sList[offsets.length - 1].transitions().size();
        char[] transChars = new char[nTransitions];
        int[] transTo = new int[nTransitions];

        // Final state set.
        Set<Integer> finalStates = new HashSet<Integer>();

        // Construct the transition table.
        for (int i = 0; i < sList.length; i++) {
            int j = 0;
            for (Entry<Character, State> trans : sList[i].transitions().entrySet()) {
                transChars[offsets[i] + j] = trans.getKey();
                transTo[offsets[i] + j] = stateNumbers.get(trans.getValue());
                ++j;
            }

            if (sList[i].isFinal())
                finalStates.add(i);
        }

        if (perfectHash)
            return new PerfectHashDictionaryIntIntImpl(offsets, transChars, transTo, finalStates, d_nSeqs);
        else
            return new DictionaryIntIntImpl(offsets, transChars, transTo, finalStates, d_nSeqs);
    }

    private Map<State, Integer> numberedStates() {
        Map<State, Integer> states = new HashMap<State, Integer>();

        Queue<State> stateQueue = new LinkedList<State>();
        stateQueue.add(d_startState);
        while (!stateQueue.isEmpty()) {
            State s = stateQueue.poll();

            if (states.containsKey(s))
                continue;

            states.put(s, states.size());

            for (State to : s.transitions().values())
                stateQueue.add(to);
        }

        return states;
    }

    /**
     * Replace new states that are already in the automaton.
     *
     * @param s   The state to start replacement (the state itself will never be replaced).
     * @param seq The sequence.
     */
    private void replaceOrRegister(State s, String seq) {
        State next = s.move(seq.charAt(0));

        // If someone is constructing an empty lexicon, we don't have any outgoing
        // transitions on the start state.
        if (next == null)
            return;

        // Grandchildren may require replacement as well.
        if (seq.length() > 1)
            replaceOrRegister(next, seq.substring(1)); // substring should be O(1).

        State replacement = d_register.get(next);
        if (replacement != null)
            s.addTransition(seq.charAt(0), replacement);
        else
            d_register.put(next, next);
    }

    private State[] stateList(Map<State, Integer> numberedStates) {
        State[] r = new State[numberedStates.size()];

        for (Entry<State, Integer> numberedState : numberedStates.entrySet())
            r[numberedState.getValue()] = numberedState.getKey();

        return r;
    }
}
