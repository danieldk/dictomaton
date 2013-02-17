package eu.danieldk.fsadict;

/**
 * Perfect hash dictionary interface. A perfect hash dictionary provides
 * the functionality of a {@link Dictionary}, plus:
 * <ul>
 *     <li>A hash code for each sequence in the dictionary ({@link #number(CharSequence)}).</li>
 *     <li>The character sequence of a given hash ({@link #sequence(int)}).</li>
 * </ul>
 */
public interface PerfectHashDictionary extends Dictionary {
    /**
     * Compute the perfect hash code of the given character sequence.
     *
     * @param seq
     * @return
     */
    public int number(CharSequence seq);

    /**
     * Compute the sequence corresponding to the given hash code.
     *
     * @param hashCode
     * @return
     */
    public CharSequence sequence(int hashCode);
}
