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

import java.io.Serializable;

/**
 * <p>
 * This class provides a compact integer array for applications where integers
 * are stored for which the width is known beforehand and that are not (necessarily)
 * 8, 16, or 32 bits wide.
 * </p>
 * <p>
 * {@link #get(int)} and {@link #set(int, int)} are in O(1) time.
 * </p>
 */
class CompactIntArray implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int INT_SIZE = 32;
    private static final int MASK[] = { 0x0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff,
            0x3fff, 0x7fff, 0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff,
            0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff };

    private final int d_size;
    private final int d_bitsPerElem;
    private final int[] d_data;

    /**
     * Construct an array of the given number of elements and (maximum) bit width per element.
     *
     * @param nElems      The number of elements.
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
     * Search a value in the array, the subarray <i>(fromIndex, toIndex]</i> should be sorted.
     * @param fromIndex The index of the first element to be searched.
     * @param toIndex The index of the last element to be searched (exclusive).
     * @param value The value to be searched.
     * @return
     */
    public int binarySearch(int fromIndex, int toIndex, int value)
    {
        --toIndex;

        while (toIndex >= fromIndex)
        {
            int mid = (int) (((long) toIndex + (long) fromIndex) / 2L);
            int midVal = get(mid);

            if (midVal > value)
                toIndex = mid - 1;
            else if (midVal < value)
                fromIndex = mid + 1;
            else
                return mid;
        }

        return -(fromIndex + 1);
    }

    /**
     * Get the integer at the given index.
     *
     * @param index The index.
     * @return An integer.
     */
    public int get(int index) {
        if (d_bitsPerElem == 0)
            return 0;

        int startIdx = (index * d_bitsPerElem) / INT_SIZE;
        int startBit = (index * d_bitsPerElem) % INT_SIZE;

        int result = (d_data[startIdx] >>> startBit) & MASK[d_bitsPerElem];

        if ((startBit + d_bitsPerElem) > 32) {
            int done = INT_SIZE - startBit;
            result |= (d_data[startIdx + 1] & MASK[d_bitsPerElem - done]) << done;
        }

        return result;
    }

    /**
     * Set the integer at the given index. <b>Warning:</b> if you attempt to store an integer that
     * is wider than the width given to the constructor {@link #CompactIntArray(int, int)}, the integer
     * is truncated.
     *
     * @param index The index.
     * @param value The value to store.
     */
    public void set(int index, int value) {
        if (d_bitsPerElem == 0)
            return;

        int startIdx = (index * d_bitsPerElem) / INT_SIZE;
        int startBit = (index * d_bitsPerElem) % INT_SIZE;

        // Clear data
        d_data[startIdx] &= ~(MASK[d_bitsPerElem] << startBit);

        // And set.
        d_data[startIdx] |= value << startBit;

        // If the integer didn't have enough bits available, write the rest in the next integer.
        if ((startBit + d_bitsPerElem) > 32) {
            int done = INT_SIZE - startBit;
            d_data[startIdx + 1] &= ~MASK[d_bitsPerElem - done];
            d_data[startIdx + 1] |= value >>> done;
        }

    }

    /**
     * Get the size of the array.
     *
     * @return The size.
     */
    public int size() {
        return d_size;
    }

    public static int width(int n) {
        return INT_SIZE - Integer.numberOfLeadingZeros(n);
    }
}
