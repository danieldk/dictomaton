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

package eu.danieldk.dictomaton;

import java.util.BitSet;

/**
 * A finite state dictionary with perfect hashing, that puts right language cardinalities in states.
 * Dictionaries of this type can are constructed using
 * {@link eu.danieldk.dictomaton.DictionaryBuilder#buildPerfectHash()}.
 *
 * @author Daniel de Kok
 */
class PerfectHashDictionaryStateCard extends DictionaryImpl implements PerfectHashDictionary {
    private static final long serialVersionUID = 2L;

    private final CompactIntArray d_stateNSuffixes;

    /**
     * Compute the perfect hash code of the given character sequence.
     *
     * @param seq
     * @return
     */
    public int number(CharSequence seq) {
        StateInfo info = getStateInfo(seq);
        return info.isInFinalState() ? info.getHash() : -1;
    }
    
    public StateInfo getStateInfo(CharSequence seq) {
        return getStateInfo(seq, null);
    }
    
    public StateInfo getStateInfo(CharSequence seq, StateInfo startInfo) {
        
        StateInfo info;
        
        if (startInfo != null) {
            if (!startInfo.isInKnownState()) {
                throw new IllegalStateException("Cannot resume transitions from unknown state. Sequence: " + seq);
            }
            info = new StateInfo(startInfo.num, startInfo.state, startInfo.trans, startInfo.inFinalState);
        } else {
            info = new StateInfo(0, 0, -1, false);
        }
        
        for (int i = 0; i < seq.length(); i++) {
            char ch = seq.charAt(i);
            info.trans = findTransition(info.state, ch);

            if (!info.isInKnownState()) {
                return info;
            }

            // Count the number of preceding suffixes in the preceding transitions.
            for (int j = d_stateOffsets.get(info.state); j < info.trans; j++)
                info.num += d_stateNSuffixes.get(d_transitionTo.get(j));

            // A final state is another suffix.
            if (d_finalStates.get(info.state))
                ++info.num;

            info.state = d_transitionTo.get(info.trans);
        }

        info.inFinalState = d_finalStates.get(info.state);
        
        return info;
    }

    /**
     * Compute the sequence corresponding to the given hash code.
     *
     * @param hashCode
     * @return
     */
    public String sequence(int hashCode) {
        if (hashCode <= 0)
            return null;

        int state = 0;

        // If the hash code is larger than the number of suffixes in the start state,
        // the hash code does not correspond to a sequence.
        if (hashCode > d_stateNSuffixes.get(state))
            return null;

        StringBuilder wordBuilder = new StringBuilder();

        // Stop if we are in a state where we cannot add more characters.
        while (d_stateOffsets.get(state) != transitionsUpperBound(state)) {

            // Obtain the next transition, decreasing the hash code by the number of
            // preceding suffixes.
            int trans;
            for (trans = d_stateOffsets.get(state); trans < transitionsUpperBound(state); ++trans) {
                int stateNSuffixes = d_stateNSuffixes.get(d_transitionTo.get(trans));

                if (hashCode - stateNSuffixes <= 0)
                    break;

                hashCode -= stateNSuffixes;
            }

            // Add the character on the given transition and move.
            wordBuilder.append(d_transitionChars[trans]);
            state = d_transitionTo.get(trans);

            // If we encounter a final state, decrease the hash code, since it represents a
            // suffix. If our hash code is reduced to zero, we have found the sequence.
            if (d_finalStates.get(state)) {
                --hashCode;

                if (hashCode == 0)
                    return wordBuilder.toString();
            }
        }

        // Bad luck, we cannot really get here!
        return null;
    }

    @Override
    public int size() {
        return d_nSeqs;
    }

    /**
     * Give the Graphviz dot representation of this automaton. States will also list the
     * number of suffixes 'under' that state.
     *
     * @return
     */
    @Override
    public String toDot() {
        StringBuilder dotBuilder = new StringBuilder();

        dotBuilder.append("digraph G {\n");

        for (int state = 0; state < d_stateOffsets.size(); ++state) {
            for (int trans = d_stateOffsets.get(state); trans < transitionsUpperBound(state); ++trans)
                dotBuilder.append(String.format("%d -> %d [label=\"%c\"]\n",
                        state, d_transitionTo.get(trans), d_transitionChars[trans]));

            if (d_finalStates.get(state))
                dotBuilder.append(String.format("%d [peripheries=2,label=\"%d (%d)\"];\n", state, state, d_stateNSuffixes.get(state)));
            else
                dotBuilder.append(String.format("%d [label=\"%d (%d)\"];\n", state, state, d_stateNSuffixes.get(state)));
        }

        dotBuilder.append("}");

        return dotBuilder.toString();
    }

    /**
     * @see DictionaryImpl#DictionaryImpl(CompactIntArray, char[], CompactIntArray, java.util.BitSet, int)
     */
    protected PerfectHashDictionaryStateCard(CompactIntArray stateOffsets, char[] transitionChars,
                                             CompactIntArray transitionTo, BitSet finalStates,
                                             int nSeqs) {
        super(stateOffsets, transitionChars, transitionTo, finalStates, nSeqs);

        // Marker that indicates that the number of suffixes of a state is not yet computed. We cannot
        // use -1, since CompactIntArray would then require 32-bit per value.
        final int magicMarker = nSeqs + 1;

        d_stateNSuffixes = new CompactIntArray(d_stateOffsets.size(), CompactIntArray.width(magicMarker));
        for (int i = 0; i < d_stateNSuffixes.size(); ++i)
            d_stateNSuffixes.set(i, magicMarker);

        computeStateSuffixes(0, magicMarker);
    }

    private int computeStateSuffixes(final int state, final int magicMarker) {
        int suffixes = d_stateNSuffixes.get(state);
        if (suffixes != magicMarker)
            return suffixes;

        suffixes = d_finalStates.get(state) ? 1 : 0;

        for (int trans = d_stateOffsets.get(state); trans < transitionsUpperBound(state); ++trans)
            suffixes += computeStateSuffixes(d_transitionTo.get(trans), magicMarker);

        d_stateNSuffixes.set(state, suffixes);

        return suffixes;
    }
}
