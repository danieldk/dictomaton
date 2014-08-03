/**
 * 
 */
package eu.danieldk.dictomaton;

/**
 * <p>This class provides information about the state in which transitions for a given character sequence 
 * ended up when computing the hash code of this sequence in a {@link eu.danieldk.dictomaton.PerfectHashDictionary}.</p>
 * <p>It is possible to resume a transition using a StateInfo object to define the start state in {@link PerfectHashDictionary#getStateInfo(CharSequence, StateInfo)} 
 * <p>Example:</p>
 * <pre>
 * {@code
 * 
 * PerfectHashDictionary dict; // dictionary containing sequences "abc" and "abc def" 
 * String seq = "abc def";
 * 
 * StateInfo info1 = dict.getStateInfo(seq.subSequence(0, 3));
 * 
 * if (info1.isInFinalState()) {
 *   int hash1 = info1.getHash(); // hash1 contains the hash for "abc"
 * }
 * 
 * if (info1.isInKnownState()) {
 * 
 *   // regardless of whether we are in a final state after "abc", as long as
 *   // we are in a known state (= the sequence is contained in the dictionary) 
 *   // we can resume the transition after "abc"
 *   
 *   StateInfo info2 = dict.getStateInfo(seq.subSequence(3, seq.length()), info1);
 *   if (info2.isInFinalState()) {
 *      int hash2 = info2.getHash(); // hash2 contains the hash for "abc def"
 *   }
 * }
 * 
 * }
 * </pre>
 * 
 * @author René Kriegler
 *
 */
public class StateInfo {
    
    int num = 0;
    int state = 0;
    int trans = -1;
    boolean inFinalState = false;
    
    StateInfo(int num, int state, int trans, boolean inFinalState) {
        this.num = num;
        this.state = state;
        this.trans = trans;
        this.inFinalState = inFinalState;
    }
    
    /**
     * 
     * @return true iff the character sequence that lead to this state is a character sequence or a prefix of a character sequence in the dictionary
     */
    public boolean isInKnownState() {
        return trans > -1;
    }
    
    /**
     * 
     * @return true iff the character sequence that lead to this state is a character sequence in the dictionary
     */
    public boolean isInFinalState() {
        return isInKnownState() && inFinalState;
    }
    
    /**
     * 
     * @return the hash code of the character sequence that lead to this state
     * @throws IllegalStateException if the current state is not a final state.
     */
    public int getHash() {
        if (!isInFinalState()) {
            throw new IllegalStateException("Not in final state");
        }
        return num + 1;
    }
    
    

}
