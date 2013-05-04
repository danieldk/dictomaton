package eu.danieldk.dictomaton.levenshtein;

class ParametricTransitions1 implements ParametricTransitions {

    @Override
    public int transition(int parametricState, int offset, int characteristicVector, int len) {
        int parToState = -1;
        int parToOffset = 0;

        if (offset == len) {
            if (parametricState < 2) {
                int pos = characteristicVector * 2 + parametricState;
                parToState = d_toStates[0][pos];
                parToOffset = d_increments[0][pos];
            }
        } else if (offset == len - 1) {
            if (parametricState < 3) {
                int pos = characteristicVector * 3 + parametricState;
                parToState = d_toStates[1][pos];
                parToOffset = d_increments[1][pos];
            }
        } else if (offset == len - 2) {
            if (parametricState < 5) {
                int pos = characteristicVector * 5 + parametricState;
                parToState = d_toStates[2][pos];
                parToOffset = d_increments[2][pos];
            }
        } else if (offset <= len - 3) {
            if (parametricState < 5) {
                int pos = characteristicVector * 5 + parametricState;
                parToState = d_toStates[3][pos];
                parToOffset = d_increments[3][pos];
            }
        }

        if (parToState == -1)
            return -1;

        return ((offset + parToOffset) * d_nStates) + parToState;
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
