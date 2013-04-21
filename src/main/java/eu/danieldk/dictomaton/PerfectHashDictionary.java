package eu.danieldk.dictomaton;

/**
 * Perfect hash dictionary interface. A perfect hash dictionary provides
 * the functionality of a {@link Dictionary}, plus:
 * <ul>
 * <li>A hash code for each sequence in the dictionary ({@link #number(String)}).</li>
 * <li>The character sequence of a given hash ({@link #sequence(int)}).</li>
 * </ul>
 */
public interface PerfectHashDictionary extends Dictionary {
    /**
     * Compute the perfect hash code of the given character sequence.
     *
     * @param seq The sequence to compute the perfect hash value for.
     * @return The perfect hash value of the sequence or <tt>-1</tt> if the sequence is
     *         not in the automaton.
     */
    public int number(String seq);

    /**
     * Compute the sequence corresponding to the given hash code.
     *
     * @param hashCode The hash code.
     * @return The sequence corresponding to the hash code or <tt>null</tt> if there is
     *         no sequence with that code in the automaton.
     */
    public String sequence(int hashCode);
}
