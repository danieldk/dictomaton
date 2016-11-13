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

import eu.danieldk.dictomaton.Dictionary;

/**
 * A Levenshtein automaton is an automaton that accepts a string and all strings within
 * a given edit distance. This class constructs the automaton from parametric state/transition
 * tables, allowing for the construction of the automaton in <i>O(l*n)</i> time, where <i>l</i>
 * is the word length and *n* the number of edit operations. In other words, the construction
 * time grows linearly with the length of a word for a given edit distance.
 */
public class LevenshteinAutomaton {
    private final static ParametricTransitions[] d_parametricTransitions = {new ParametricTransitions1(),
            new ParametricTransitions2()};
    private final LevenshteinAutomatonState d_startState;
    private final Set<Character> d_alphabet;
    private final char d_otherChar;

    /**
     * Construct a Levenshtein automaton for a word with a maximumum permitted Levenshtein
     * distance. The maximum distance can currently be 1 or 2.
     *
     * @param word        The word.
     * @param maxDistance The maximum distance.
     */
    public LevenshteinAutomaton(String word, int maxDistance) {
        if (maxDistance > d_parametricTransitions.length || maxDistance < 1)
            throw new IllegalArgumentException(String.format("The maximum supported edit distance is: %d",
                    d_parametricTransitions.length));

        d_alphabet = extractAlphabet(word);
        d_otherChar = findAnyChar(d_alphabet);
        d_alphabet.add(d_otherChar);

        d_startState = createAutomaton(d_parametricTransitions[maxDistance - 1], word);
        d_startState.reduce(d_otherChar);
    }

    /**
     * Return the automaton in Graphviz dot format.
     *
     * @return Dot representation.
     */
    public String toDot() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("digraph G {\n");

        Map<LevenshteinAutomatonState, Integer> stateNumbers = numberedStates(d_startState);

        // We want to traverse states a fixed order, so that the output is predictable. We
        // could also store the numbered states in a TreeMap, but LevenshteinAutomatonState doesn't implement
        // Comparable, and I wouldn't even know what that would mean ;).
        LevenshteinAutomatonState[] states = new LevenshteinAutomatonState[stateNumbers.size()];

        for (Map.Entry<LevenshteinAutomatonState, Integer> numberedState : stateNumbers.entrySet())
            states[numberedState.getValue()] = numberedState.getKey();

        for (int stateNumber = 0; stateNumber < states.length; ++stateNumber) {
            LevenshteinAutomatonState s = states[stateNumber];

            if (s.isFinal())
                stringBuilder.append(String.format("%d [peripheries=2];\n", stateNumber));

            for (Map.Entry<Character, LevenshteinAutomatonState> trans : s.transitions().entrySet())
                stringBuilder.append(String.format("%d -> %d [label=\"%c\"];\n", stateNumber,
                        stateNumbers.get(trans.getValue()), trans.getKey()));
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    /**
     * Compute the intersection language of a dictionary and the automaton. This amounts
     * to finding the strings in the dictionary that are within the edit distance allowed
     * by the {@link LevenshteinAutomaton}.
     *
     * @param dictionary
     * @return The intersection language.
     */
    public Set<String> intersectionLanguage(Dictionary dictionary) {
        Set<String> language = new HashSet<>();

        Queue<StatePair> q = new LinkedList<>();
        q.add(new StatePair(dictionary.startState(), d_startState, ""));

        while (!q.isEmpty()) {
            StatePair pair = q.poll();
            int dictState = pair.getDictionaryState();
            LevenshteinAutomatonState laState = pair.getLevenshteinAutomatonState();
            String string = pair.getString();

            for (Character c : dictionary.transitionCharacters(dictState)) {
                LevenshteinAutomatonState laNewState = laState.move(c);

                if (laNewState == null && (laNewState = laState.move(d_otherChar)) == null)
                    continue;

                int dictNewState = dictionary.next(dictState, c);

                String newString = string + c;

                if (laNewState.isFinal() && dictionary.isFinalState(dictNewState))
                    language.add(newString);

                q.add(new StatePair(dictNewState, laNewState, newString));
            }

        }

        return language;
    }

    /**
     * Create the Levenshtein automaton for a word.
     *
     * @param transitions The parametric transition table.
     * @param word        The word.
     * @return
     */
    private LevenshteinAutomatonState createAutomaton(ParametricTransitions transitions, String word) {
        int n = transitions.nEditOperations();

        LevenshteinAutomatonState[] states = new LevenshteinAutomatonState[(word.length() + 1)
                * transitions.nParametricStates()];
        for (int i = 0; i < states.length; ++i)
            states[i] = new LevenshteinAutomatonState();

        final int nParametricStates = transitions.nParametricStates();
        final int lastPosition = states.length / nParametricStates;

        // Fill state table.
        for (int i = 0; i < states.length; ++i) {
            int offset = i / nParametricStates;
            int parametricState = i % nParametricStates;

            for (Character c : d_alphabet) {
                int vec = characteristicVector(word, offset, c, n);

                // Will be handled by the 'other' transition.
                if (vec == 0 && c != d_otherChar)
                    continue;

                int toState = transitions.transition(parametricState, offset, vec, word.length());
                if (toState != -1)
                    states[i].addTransition(c, states[toState]);
            }

            if (lastPosition - 1 - offset <= transitions.maxOffsetErrors(parametricState))
                states[i].setFinal(true);
        }

        return states[0];
    }

    private int characteristicVector(String word, int offset, char c, int n) {
        int vlen = Math.min(2 * n + 1, word.length() - offset);

        int vec = 0;
        for (int i = 0; i < vlen; ++i) {
            vec <<= 1;
            vec |= word.charAt(offset + i) == c ? 1 : 0;
        }

        return vec;
    }

    private Set<Character> extractAlphabet(String word) {
        Set<Character> alphabet = new HashSet<>();

        for (int i = 0; i < word.length(); ++i)
            alphabet.add(word.charAt(i));

        return alphabet;
    }

    private char findAnyChar(Set<Character> alphabet) {
        for (char c = 0; c < Character.MAX_VALUE; ++c) {
            if (!alphabet.contains(c))
                return c;
        }

        throw new IllegalArgumentException("At least one character that is not in the alphabet is required.");
    }

    private Map<LevenshteinAutomatonState, Integer> numberedStates(LevenshteinAutomatonState startState) {
        Map<LevenshteinAutomatonState, Integer> states = new HashMap<>();

        Queue<LevenshteinAutomatonState> stateQueue = new LinkedList<>();
        stateQueue.add(startState);
        while (!stateQueue.isEmpty()) {
            LevenshteinAutomatonState s = stateQueue.poll();

            if (states.containsKey(s))
                continue;

            states.put(s, states.size());

            for (LevenshteinAutomatonState to : s.transitions().values())
                stateQueue.add(to);
        }

        return states;
    }

    /**
     * This class stores a pair of states from a {@link LevenshteinAutomaton} and a {@link Dictionary}.
     */
    private class StatePair {
        private final int d_dictionaryState;
        private final LevenshteinAutomatonState d_laState;
        private final String d_string;

        private StatePair(int dictionaryState, LevenshteinAutomatonState laState, String string) {
            d_dictionaryState = dictionaryState;
            d_laState = laState;
            d_string = string;
        }

        private int getDictionaryState() {
            return d_dictionaryState;
        }

        private LevenshteinAutomatonState getLevenshteinAutomatonState() {
            return d_laState;
        }

        private String getString() {
            return d_string;
        }
    }

}
