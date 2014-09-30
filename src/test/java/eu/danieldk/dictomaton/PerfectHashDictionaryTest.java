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

import static org.junit.Assert.*;
import eu.danieldk.dictomaton.categories.Tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(Tests.class)
public class PerfectHashDictionaryTest {
    private List<String> d_words1;
    private List<String> d_words2;
    private List<String> d_words3;
    private PerfectHashDictionary d_dict;
    private PerfectHashDictionary d_dictTransitionCardinality;

    @SuppressWarnings("serial")
    @Before
    public void initialize() throws DictionaryBuilderException {
        d_words1 = new ArrayList<String>() {{
            add("al");
            add("alleen");
            add("avonden");
            add("zeemeeuw");
            add("zeker");
            add("zeven");
            add("zoeven");
        }};

        d_words2 = new ArrayList<String>() {{
            add("als");
            add("allen");
            add("avond");
            add("zeemeeuwen");
            add("zeer");
            add("zepen");
            add("zoef");
        }};

        d_words3 = new ArrayList<String>() {{
            add("groene");
            add("mooie");
            add("oude");
            add("rare");
        }};

        d_dict = new DictionaryBuilder().addAll(d_words1).buildPerfectHash();
        d_dictTransitionCardinality = new DictionaryBuilder().addAll(d_words1).buildPerfectHash(false);
    }

    @Test
    public void cycleTest() throws DictionaryBuilderException {
        // This test causes cycles when there is an off-by-one bug for the transition table.
        PerfectHashDictionary dict = new DictionaryBuilder().addAll(d_words3).buildPerfectHash();
        Assert.assertEquals(4, dict.size());
    }

    @Test
    public void emptyTest() {
        PerfectHashDictionary dict = new DictionaryBuilder().buildPerfectHash();
        Assert.assertEquals(-1, dict.number("foo"));
        Assert.assertNull(dict.sequence(1));
        Assert.assertEquals(0, dict.size());
    }

    @Test
    public void sizeTest() {
        Assert.assertEquals(7, d_dict.size());
    }

    @Test
    public void toNumberTest() {
        for (int i = 0; i < d_words1.size(); i++)
            Assert.assertEquals(i + 1, d_dict.number(d_words1.get(i)));

        for (int i = 0; i < d_words2.size(); i++)
            Assert.assertEquals(-1, d_dict.number(d_words2.get(i)));
    }
    
    @Test
    public void testStateInfo() throws Exception {
        for (int i = 0; i < d_words1.size(); i++) {
            StateInfo info = d_dict.getStateInfo(d_words1.get(i));
            assertTrue(info.isInKnownState());
            assertTrue(info.isInFinalState());
            Assert.assertEquals(i + 1, info.getHash());
        }
        
        for (int i = 0; i < d_words2.size(); i++) {
            StateInfo info = d_dict.getStateInfo(d_words2.get(i));
            if ("avond".equals(d_words2.get(i))) {
                assertTrue(d_words2.get(i) + ": transistion should end in known state",  info.isInKnownState());
            } else {
                assertFalse(d_words2.get(i) + ": transistion should end in unknown state",  info.isInKnownState());
            }
            
            assertFalse(d_words2.get(i) + ": transistion should end in non-final state", info.isInFinalState());
            
            try {
                info.getHash();
                fail();
            } catch (IllegalStateException e) {
                // expected
            }
        }

        for (int i = 0; i < d_words2.size(); i++)
            Assert.assertEquals(-1, d_dict.number(d_words2.get(i)));
    }

    @Test
    public void toNumberTransitionsTest() {
        for (int i = 0; i < d_words1.size(); i++)
            Assert.assertEquals(i + 1, d_dictTransitionCardinality.number(d_words1.get(i)));

        for (int i = 0; i < d_words2.size(); i++)
            Assert.assertEquals(-1, d_dictTransitionCardinality.number(d_words2.get(i)));
    }
    
    @Test
    public void testStateInfoForCard() throws Exception {
        for (int i = 0; i < d_words1.size(); i++) {
            StateInfo info = d_dictTransitionCardinality.getStateInfo(d_words1.get(i));
            assertTrue(info.isInKnownState());
            assertTrue(info.isInFinalState());
            Assert.assertEquals(i + 1, info.getHash());
        }
        
        for (int i = 0; i < d_words2.size(); i++) {
            
            StateInfo info = d_dictTransitionCardinality.getStateInfo(d_words2.get(i));
            if ("avond".equals(d_words2.get(i))) {
                assertTrue(d_words2.get(i) + ": transistion should end in known state",  info.isInKnownState());
            } else {
                assertFalse(d_words2.get(i) + ": transistion should end in unknown state",  info.isInKnownState());
            }
            
            assertFalse(d_words2.get(i) + ": transistion should end in non-final state", info.isInFinalState());
            
            try {
                info.getHash();
                fail();
            } catch (IllegalStateException e) {
                // expected
            }
        }

        for (int i = 0; i < d_words2.size(); i++)
            Assert.assertEquals(-1, d_dictTransitionCardinality.number(d_words2.get(i)));
    }
    
    @Test
    public void testResumeTransitionsAfterNonFinal() throws Exception {
        
        StateInfo info = d_dict.getStateInfo("avond");
        assertTrue(info.isInKnownState());
        assertFalse(info.isInFinalState());
        
        StateInfo info2 = d_dict.getStateInfo("en", info); // avonden
        assertTrue(info2.isInKnownState());
        assertTrue(info2.isInFinalState());
        
        
    }
    
    @Test
    public void testResumeTransitionsAfterFinal() throws Exception {
        
        StateInfo info = d_dict.getStateInfo("al"); // al
        assertTrue(info.isInKnownState());
        assertTrue(info.isInFinalState());
        
        StateInfo info2 = d_dict.getStateInfo("leen", info); // alleen
        assertTrue(info2.isInKnownState());
        assertTrue(info2.isInFinalState());
        
    }
    
    @Test
    public void testResumeTransitionsAfterNonFinalForCard() throws Exception {
        
        StateInfo info = d_dictTransitionCardinality.getStateInfo("avond");
        assertTrue(info.isInKnownState());
        assertFalse(info.isInFinalState());
        
        StateInfo info2 = d_dictTransitionCardinality.getStateInfo("en", info); // avonden
        assertTrue(info2.isInKnownState());
        assertTrue(info2.isInFinalState());
        
    }
    
    @Test
    public void testResumeTransitionsAfterFinalForCard() throws Exception {
        
        StateInfo info = d_dictTransitionCardinality.getStateInfo("al"); // al
        assertTrue(info.isInKnownState());
        assertTrue(info.isInFinalState());
        
        StateInfo info2 = d_dictTransitionCardinality.getStateInfo("leen", info); // alleen
        assertTrue(info2.isInKnownState());
        assertTrue(info2.isInFinalState());
        
    }
    

    @Test
    public void toWordTest() {
        for (int i = 0; i < d_words1.size(); i++)
            Assert.assertEquals(d_words1.get(i), d_dict.sequence(i + 1));
    }

    @Test
    public void toWordTransitionsTest() {
        for (int i = 0; i < d_words1.size(); i++)
            Assert.assertEquals(d_words1.get(i), d_dictTransitionCardinality.sequence(i + 1));
    }

    @Test
    public void unknownHashTest() {
        Assert.assertNull(d_dict.sequence(0));
        Assert.assertNull(d_dict.sequence(d_words1.size() + 1));
    }
}
