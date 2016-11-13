package eu.danieldk.dictomaton;

import java.io.Serializable;
import java.util.Set;

/**
 * Finite state dictionary interface. A dictionary provides the following
 * functionality:
 * <ul>
 * <li>Check whether a character sequence is in a dictionary.</li>
 * <li>Iterate over the character sequences in the dictionary.</li>
 * <li>Get a Graphviz dot representation of the underlying automaton.</li>
 * </ul>
 *
 * @author Daniel de Kok
 */
public interface Dictionary extends Set<String>, Serializable {
    /**
     * Give the Graphviz dot representation of this automaton.
     *
     * @return Dot representation of the automaton.
     */
    String toDot();

    /**
     * Returns <tt>true</tt> if the state is a final state and <tt>false</tt> otherwise.
     * @param state The state.
     * @return Finalness of the state.
     */
    boolean isFinalState(int state);

    /**
     * Get the next state, given a character.
     *
     * @param state
     * @param c
     * @return
     */
    int next(int state, char c);

    /**
     * Get the start state.
     *
     * @return The start state.
     */
    int startState();

    /**
     * Get the transition characters on outgoing transitions of the current state.
     *
     * @param state
     * @return
     */
    Set<Character> transitionCharacters(int state);
}
