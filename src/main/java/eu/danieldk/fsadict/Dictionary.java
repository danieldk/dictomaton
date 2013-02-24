package eu.danieldk.fsadict;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Finite state dictionary interface. A dictionary provides the following
 * functionality:
 * <ul>
 *     <li>Check whether a character sequence is in a dictionary.</li>
 *     <li>Iterate over the character sequences in the dictionary.</li>
 *     <li>Get a Graphviz dot representation of the underlying automaton.</li>
 * </ul>
 *
 * @author Daniel de Kok
 */
public interface Dictionary extends Iterable<String>, Serializable {
    /**
     * Check whether the dictionary contains the given sequence.
     * @param seq The sequence to look up.
     * @return <tt>true</tt> if the dictionary contains the sequence.
     */
    public boolean contains(String seq);

    /**
     * Get an iterator over the character sequences in the dictionary.
     */
    @Override
    public Iterator<String> iterator();

    /**
     * Get the number of sequences in the automaton.
     * @return Number of sequences.
     */
    public int size();

    /**
     * Give the Graphviz dot representation of this automaton.
     * @return Dot representation of the automaton.
     */
    public String toDot();
}
