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

package eu.danieldk.fsadict.collections;

/**
 * This class provides a compact integer array for applications where integers
 * are stored for which the width is known beforehand and that are not (necessarily)
 * 8, 16, or 32 bits wide.
 * <p/>
 * {@link #get(int)} and {@link #set(int, int)} are in O(1) time.
 */
class CompactIntArray {
    private int INT_SIZE = 32;

    private final int d_size;
    private final int d_bitsPerElem;
    private final int[] d_data;

    /**
     * Construct an array of the given number of elements and (maximum) bit width per element.
     * @param nElems The number of elements.
     * @param bitsPerElem The number of bits per element.
     */
    public CompactIntArray(int nElems, int bitsPerElem) {
        d_size = nElems;
        d_bitsPerElem = bitsPerElem;

        int arrSize = (nElems * bitsPerElem) / INT_SIZE;
        if ((nElems * bitsPerElem) % INT_SIZE != 0)
            ++arrSize;

        d_data = new int[arrSize];
    }

    /**
     * Get the integer at the given index.
     * @param index The index.
     * @return An integer.
     */
    public int get(int index) {
        int startIdx = (index * d_bitsPerElem) / INT_SIZE;
        int startBit = (index * d_bitsPerElem) % INT_SIZE;

        int result = (d_data[startIdx] & mask(startBit, d_bitsPerElem)) >>> startBit;

        if ((startBit + d_bitsPerElem) > 32) {
            int done = INT_SIZE - startBit;
            result |= (d_data[startIdx + 1] & mask(0, d_bitsPerElem - done)) << done;
        }

        return result;
    }

    /**
     * Set the integer at the given index. <b>Warning:</b> if you attempt to store an integer that
     * is wider than the width given to the constructor {@link #CompactIntArray(int, int)}, the integer
     * is truncated.
     * @param index The index.
     * @param value The value to store.
     */
    public void set(int index, int value) {
        int startIdx = (index * d_bitsPerElem) / INT_SIZE;
        int startBit = (index * d_bitsPerElem) % INT_SIZE;

        // Clear data
        d_data[startIdx] &= ~mask(startBit, d_bitsPerElem);

        // And set.
        d_data[startIdx] |= value << startBit;

        // If the integer didn't have enough bits available, write the rest in the next integer.
        if ((startBit + d_bitsPerElem) > 32) {
            int done = INT_SIZE - startBit;
            d_data[startIdx + 1] &= ~mask(0, d_bitsPerElem - done);
            d_data[startIdx + 1] |= value >>> done;
        }

    }

    private int mask(int startBit, int nBits) {
        // Get appropriate number of bits.
        int mask = ~0 >>> (INT_SIZE - nBits);

        //System.out.println(String.format("start: %d, nbits: %d, mask %s", startBit, nBits, Integer.toBinaryString(mask << startBit)));

        // Shift to the right position.
        return mask << startBit;

    }
}
