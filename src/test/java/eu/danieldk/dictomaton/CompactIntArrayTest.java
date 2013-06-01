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

import eu.danieldk.dictomaton.categories.Tests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Unit tests for {@link eu.danieldk.dictomaton.CompactIntArray}.
 */
@Category(Tests.class)
public class CompactIntArrayTest {
    private final int NUMBER_TESTS = 500;
    private final int MAX_ARRAY_LEN = 2000;

    private Random rng;

    @Before
    public void initialize() {
        rng = new Random(42);
    }

    @Test
    public void binarySearchTest() {
        for (int bits = 8; bits < 16; ++bits) {
            for (int n = 0; n < NUMBER_TESTS; ++n) {
                int l = rng.nextInt(MAX_ARRAY_LEN);

                CompactIntArray test = new CompactIntArray(l, bits);
                List<Integer> check = randomList(rng, bits, l);
                Collections.sort(check);

                for (int i = 0; i < l; ++i)
                    test.set(i, check.get(i));

                List<Integer> needles = randomList(rng, bits, l);
                for (Integer needle : needles)
                    Assert.assertEquals(Collections.binarySearch(check, needle), test.binarySearch(0, test.size(), needle));
            }
        }
    }

    @Test
    public void fullWidthTest() {
        List<Integer> check = new ArrayList<Integer>(42);
        CompactIntArray test = new CompactIntArray(MAX_ARRAY_LEN, 32);

        for (int i = 0; i < MAX_ARRAY_LEN; ++i) {
            int num = rng.nextInt();
            check.add(num);
            test.set(i, num);
        }

        for (int i = 0; i < MAX_ARRAY_LEN; ++i) {
            Assert.assertEquals(check.get(i).intValue(), test.get(i));
        }
    }

    @Test
    public void randomizedTest() {
        for (int bits = 1; bits < 32; ++bits) {
            for (int n = 0; n < NUMBER_TESTS; ++n) {
                int l = rng.nextInt(MAX_ARRAY_LEN);

                CompactIntArray test = new CompactIntArray(l, bits);
                List<Integer> check = randomList(rng, bits, l);

                for (int i = 0; i < l; ++i) {
                    test.set(i, check.get(i));
                }

                for (int i = 0; i < l; ++i) {
                    Assert.assertEquals(check.get(i).intValue(), test.get(i));
                }
            }
        }
    }

    /**
     * Unit test to ensure that previous values are properly cleared when they are overwritten.
     */
    @Test
    public void randomizedOverwriteTest() {
        for (int bits = 1; bits < 32; ++bits) {
            for (int n = 0; n < NUMBER_TESTS; ++n) {
                int l = rng.nextInt(MAX_ARRAY_LEN);

                CompactIntArray test = new CompactIntArray(l, bits);
                List<Integer> first = randomList(rng, bits, l);
                List<Integer> check = randomList(rng, bits, l);

                for (int i = 0; i < l; ++i) {
                    test.set(i, first.get(i));
                }

                for (int i = 0; i < l; ++i) {
                    test.set(i, check.get(i));
                }

                for (int i = 0; i < l; ++i) {
                    Assert.assertEquals(check.get(i).intValue(), test.get(i));
                }
            }
        }
    }

    private List<Integer> randomList(Random rng, int bits, int length) {
        int upper = upperNum(bits);
        List<Integer> check = new ArrayList<Integer>(42);

        for (int i = 0; i < length; ++i) {
            int num = rng.nextInt(upper);
            check.add(num);
        }
        return check;
    }

    public int upperNum(int bits) {
        return (1 << bits) - 1;
    }
}
