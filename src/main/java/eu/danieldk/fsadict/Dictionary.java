package eu.danieldk.fsadict;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

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
public interface Dictionary extends Set<String>, Serializable {
    /**
     * Give the Graphviz dot representation of this automaton.
     * @return Dot representation of the automaton.
     */
    public String toDot();
}
