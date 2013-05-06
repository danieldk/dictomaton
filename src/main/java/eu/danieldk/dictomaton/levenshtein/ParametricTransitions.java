package eu.danieldk.dictomaton.levenshtein;

interface ParametricTransitions
{
    /**
     * The number of edit operations this parametric transition table is for.
     * @return The number of edit operations.
     */
    public int nEditOperations();

    /**
     * The number of parametric states in the parametric transition table.
     * @return The number of parametric states.
     */
	public int nParametricStates();

    /**
     * The maximum allowed number of offset errors per parametric state.
     * @param parametricState The parametric state.
     * @return The allowed offset error.
     */
    public int maxOffsetErrors(int parametricState);

    /**
     * The transition function.
     * @param parametricState The parametric state.
     * @param offset The state/word offset.
     * @param characteristicVector The characteristic vector.
     * @param len The length of the word.
     * @return
     */
    public int transition(int parametricState, int offset, int characteristicVector, int len);
}
