package eu.danieldk.dictomaton.levenshtein;

import java.util.*;

public class LevenshteinAutomaton {
    private final ParametricTransitions[] d_parametricTransitions = {new ParametricTransitions1(), new ParametricTransitions2()};
    private final LevenshteinAutomatonState d_startState;
    private final Set<Character> d_alphabet;
    private final char d_otherChar;

    public LevenshteinAutomaton(String word, int maxDistance) {
        if (maxDistance > d_parametricTransitions.length)
            throw new IllegalArgumentException(String.format("The maximum supported edit distance is: %d", d_parametricTransitions.length));

        d_alphabet = extractAlphabet(word);
        d_otherChar = findAnyChar(d_alphabet);
        d_alphabet.add(d_otherChar);

        d_startState = createAutomaton(d_parametricTransitions[maxDistance - 1], word);
        d_startState.reduce(d_otherChar);
    }

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

    private LevenshteinAutomatonState createAutomaton(ParametricTransitions transitions, String word) {
        int n = transitions.nEditOperations();

        LevenshteinAutomatonState[] states = new LevenshteinAutomatonState[(word.length() + 1) * transitions.nParametricStates()];
        for (int i = 0; i < states.length; ++i)
            states[i] = new LevenshteinAutomatonState();

        // Fill state table.
        for (int i = 0; i < states.length; ++i) {
            int position = i / transitions.nParametricStates();
            int parametricState = i % transitions.nParametricStates();
            int lastPosition = states.length / transitions.nParametricStates();

            for (Character c : d_alphabet) {
                int vec = characteristicVector(word.substring(position), c, n);

                // Will be handled by the 'other' transition.
                if (vec == 0 && c != d_otherChar)
                    continue;

                int toState = transitions.transition(i, position, vec, word.length());
                if (toState != -1)
                    states[i].addTransition(c, states[toState]);
            }

            if (lastPosition - 1 - position <= transitions.maxOffsetErrors(parametricState))
                states[i].setFinal(true);
        }

        return states[0];
    }

    private int characteristicVector(String rest, char c, int n) {
        int vlen = Math.min(2 * n + 1, rest.length());

        int vec = 0;
        for (int i = 0; i < vlen; ++i) {
            vec <<= 1;
            vec |= rest.charAt(i) == c ? 1 : 0;
        }

        return vec;
    }

    private Set<Character> extractAlphabet(String word) {
        Set<Character> alphabet = new HashSet<Character>();

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
        Map<LevenshteinAutomatonState, Integer> states = new HashMap<LevenshteinAutomatonState, Integer>();

        Queue<LevenshteinAutomatonState> stateQueue = new LinkedList<LevenshteinAutomatonState>();
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

}
