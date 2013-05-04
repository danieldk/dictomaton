package eu.danieldk.dictomaton.levenshtein;

class ParametricTransitions1 implements ParametricTransitions {

    @Override
    public int transition(int fromState, int position, int characteristicVector, int len) {
        int parFromState = fromState % d_nStates;
        int parOffset = fromState / d_nStates;
        int parToState = -1;
        int parToOffset = 0;

        if (position == len) {
            if (parFromState < 2) {
                int pos = characteristicVector * 2 + parFromState;
                parToState = d_toStates[0][pos];
                parToOffset = d_increments[0][pos];
            }
        } else if (position == len - 1) {
            if (parFromState < 3) {
                int pos = characteristicVector * 3 + parFromState;
                parToState = d_toStates[1][pos];
                parToOffset = d_increments[1][pos];
            }
        } else if (position == len - 2) {
            if (parFromState < 5) {
                int pos = characteristicVector * 5 + parFromState;
                parToState = d_toStates[2][pos];
                parToOffset = d_increments[2][pos];
            }
        } else if (position <= len - 3) {
            if (parFromState < 5) {
                int pos = characteristicVector * 5 + parFromState;
                parToState = d_toStates[3][pos];
                parToOffset = d_increments[3][pos];
            }
        }

        if (parToState == -1)
            return -1;

        return ((parOffset + parToOffset) * d_nStates) + parToState;
    }

    @Override
    public int nParametricStates() {
        return d_nStates;
    }

    @Override
    public int maxOffsetErrors(int parametricState) {
        return d_maxOffsetErrors[parametricState];
    }

    @Override
    public int nEditOperations() {
        return 1;
    }

    private final int d_toStates[][] = {{1, -1}, {2, -1, -1, 0, 1, 1}, {2, -1, -1, -1, -1, 4, -1, 1, -1, 1, 0, 1, 1, 1, 1, 0, 1, 2, 1, 2}, {2, -1, -1, -1, -1, 2, -1, -1, 1, 1, 4, -1, 1, -1, 1, 4, -1, 1, 1, 2, 0, 1, 1, 1, 1, 0, 1, 1, 3, 3, 0, 1, 2, 1, 2, 0, 1, 2, 3, 4}};
    private final int d_increments[][] = {{0, 0}, {0, 0, 0, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 2, 0, 2, 0, 0, 2, 3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
    private final int d_maxOffsetErrors[] = {1, 0, 1, 2, 2};
    private final int d_nStates = 5;
}
