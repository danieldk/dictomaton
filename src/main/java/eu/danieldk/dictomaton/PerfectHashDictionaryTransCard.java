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
 * A finite state dictionary with perfect hashing, that puts right language cardinalities in transitions.
 * Dictionaries of this type can are constructed using
 * {@link eu.danieldk.dictomaton.DictionaryBuilder#buildPerfectHash()}.
 *
 * @author Daniel de Kok
 */
class PerfectHashDictionaryTransCard extends DictionaryImpl implements PerfectHashDictionary {
    private static final long serialVersionUID = 1L;

    private final CompactIntArray d_transitionNSuffixes;

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
            info.trans = findTransition(info.state, seq.charAt(i));

            if (!info.isInKnownState()) {
                return info;
            }

            // Count the number of preceding suffixes in the preceding transitions.
            info.num += d_transitionNSuffixes.get(info.trans);

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
        if (hashCode > d_nSeqs)
            return null;

        StringBuilder wordBuilder = new StringBuilder();

        // Stop if we are in a state where we cannot add more characters.
        while (d_stateOffsets.get(state) != transitionsUpperBound(state)) {

            // Obtain the next transition, decreasing the hash code by the number of
            // preceding suffixes.
            int trans = d_transitionNSuffixes.binarySearch(d_stateOffsets.get(state), transitionsUpperBound(state), hashCode);
            if (trans >= 0)
                --trans;
            else
                trans = -trans - 2;

            hashCode -= d_transitionNSuffixes.get(trans);

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
                dotBuilder.append(String.format("%d -> %d [label=\"%c (%d)\"]\n",
                        state, d_transitionTo.get(trans), d_transitionChars[trans], d_transitionNSuffixes.get(trans)));

            if (d_finalStates.get(state))
                dotBuilder.append(String.format("%d [peripheries=2,label=\"%d\"];\n", state, state));
            else
                dotBuilder.append(String.format("%d [label=\"%d\"];\n", state, state));
        }

        dotBuilder.append("}");

        return dotBuilder.toString();
    }

    /**
     * @see DictionaryImpl#DictionaryImpl(eu.danieldk.dictomaton.CompactIntArray, char[], eu.danieldk.dictomaton.CompactIntArray, java.util.BitSet, int)
     */
    protected PerfectHashDictionaryTransCard(CompactIntArray stateOffsets, char[] transitionChars,
                                             CompactIntArray transitionTo, BitSet finalStates,
                                             int nSeqs) {
        super(stateOffsets, transitionChars, transitionTo, finalStates, nSeqs);

        // Marker that indicates that the number of suffixes of a state is not yet computed. We cannot
        // use -1, since CompactIntArray would then require 32-bit per value.
        final int magicMarker = nSeqs + 1;

        CompactIntArray stateNSuffixes = new CompactIntArray(d_stateOffsets.size(), CompactIntArray.width(magicMarker));
        for (int i = 0; i < stateNSuffixes.size(); ++i)
            stateNSuffixes.set(i, magicMarker);

        computeStateSuffixes(0, magicMarker, stateNSuffixes);

        d_transitionNSuffixes = transitionPrecedingTable(magicMarker, stateNSuffixes);
    }

    private int computeStateSuffixes(final int state, final int magicMarker, CompactIntArray stateNSuffixes) {

        int suffixes = stateNSuffixes.get(state);
        if (suffixes != magicMarker)
            return suffixes;

        suffixes = d_finalStates.get(state) ? 1 : 0;

        for (int trans = d_stateOffsets.get(state); trans < transitionsUpperBound(state); ++trans)
            suffixes += computeStateSuffixes(d_transitionTo.get(trans), magicMarker, stateNSuffixes);

        stateNSuffixes.set(state, suffixes);

        return suffixes;
    }

    private CompactIntArray transitionPrecedingTable(int magicMarker, CompactIntArray stateRightCard)
    {
        CompactIntArray table = new CompactIntArray(d_transitionTo.size(), CompactIntArray.width(magicMarker - 1));

        for (int state = 0; state < d_stateOffsets.size(); ++state) {
            int preceding = 0;
            for (int trans = d_stateOffsets.get(state); trans < transitionsUpperBound(state); ++trans)
            {
                table.set(trans, preceding);
                preceding += stateRightCard.get(d_transitionTo.get(trans));
            }
        }

        return table;
    }
}
