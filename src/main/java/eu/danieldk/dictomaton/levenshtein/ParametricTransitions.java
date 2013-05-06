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

interface ParametricTransitions {
    /**
     * The number of edit operations this parametric transition table is for.
     *
     * @return The number of edit operations.
     */
    public int nEditOperations();

    /**
     * The number of parametric states in the parametric transition table.
     *
     * @return The number of parametric states.
     */
    public int nParametricStates();

    /**
     * The maximum allowed number of offset errors per parametric state.
     *
     * @param parametricState The parametric state.
     * @return The allowed offset error.
     */
    public int maxOffsetErrors(int parametricState);

    /**
     * The transition function.
     *
     * @param parametricState      The parametric state.
     * @param offset               The state/word offset.
     * @param characteristicVector The characteristic vector.
     * @param len                  The length of the word.
     * @return
     */
    public int transition(int parametricState, int offset, int characteristicVector, int len);
}
