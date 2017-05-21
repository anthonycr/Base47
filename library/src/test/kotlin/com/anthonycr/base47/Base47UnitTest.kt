/*
 * MIT License
 * <p>
 * Copyright (c) 2017 Anthony Restaino
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.anthonycr.base47

import org.junit.Assert
import org.junit.Test

import java.math.BigInteger
import java.util.Random

/**
 * Sanity test for Base47 encoding.
 *
 *
 * Created by restainoa on 12/21/16.
 */
class Base47UnitTest {

    private val mRandom = Random()

    @Test
    @Throws(Exception::class)
    fun testEncode_Decode() {
        val start = System.currentTimeMillis()

        // Make the test deterministic
        mRandom.setSeed(0)

        for (n in 0..ITERATIONS - 1) {
            val randomString = randomString()

            val encoded = Base47.encode(randomString)
            Assert.assertNotNull(randomString)
            Assert.assertNotNull(encoded)
            Assert.assertNotEquals(randomString, encoded)

            val decoded = Base47.decode(encoded)

            Assert.assertArrayEquals(randomString, decoded)
        }
        println("Encode/Decode took: " + (System.currentTimeMillis() - start) + " ms")
    }

    private fun randomString(): ByteArray {
        val randomBytes = ByteArray(16)
        mRandom.nextBytes(randomBytes)

        return randomBytes
    }

}

private val ITERATIONS = 2000
