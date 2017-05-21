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

import java.util.concurrent.atomic.AtomicInteger

/**
 * A base encoding algorithm that takes in an array of bytes and outputs a string of emoji.
 * The details of implementation are as follows. First, the binary array is converted to a
 * binary string. This binary string is then converted to base 47. The special change here
 * is that instead of using the normal ASCII charset, an emoji charset of 47 characters is
 * used. This results in an interesting encoded string.
 *
 *
 * This class has 2 methods:
 *
 *
 * [Base47.encode] - which encodes a byte array, returning an emoji string in base 47.
 *
 *
 * [Base47.decode] - which takes an emoji base 47 encoded string and returns the
 * original byte array.
 *
 *
 * This base conversion algorithm was adapted from:
 * http://www.codinghelmet.com/?path=exercises/converting-number-bases
 *
 *
 * Created by restainoa on 12/21/16.
 */
object Base47 {

    private val BASE_2 = 2
    private val BASE_47 = 47

    private val CHARACTERS_2_ARRAY = arrayOf("0", "1")

    private val CHARACTERS_ARRAY = arrayOf(
            "\uD83D\uDC36", "\uD83D\uDC31", "\uD83D\uDC2D", "\uD83D\uDC39",
            "\uD83D\uDC30", "\uD83D\uDC3B", "\uD83D\uDC3C", "\uD83D\uDC28",
            "\uD83D\uDC2F", "\uD83E\uDD81", "\uD83D\uDC2E", "\uD83D\uDC37",
            "\uD83D\uDC38", "\uD83D\uDC19", "\uD83D\uDC35", "\uD83D\uDE48",
            "\uD83D\uDE49", "\uD83D\uDE4A", "\uD83D\uDC12", "\uD83D\uDC14",
            "\uD83D\uDC27", "\uD83D\uDC26", "\uD83D\uDC24", "\uD83D\uDC23",
            "\uD83D\uDC25", "\uD83D\uDC3A", "\uD83D\uDC17", "\uD83D\uDC34",
            "\uD83E\uDD84", "\uD83D\uDC1D", "\uD83D\uDC1B", "\uD83D\uDC0C",
            "\uD83D\uDC1E", "\uD83E\uDD80", "\uD83D\uDC0D", "\uD83D\uDC22",
            "\uD83D\uDC20", "\uD83D\uDC1F", "\uD83D\uDC21", "\uD83D\uDC2C",
            "\uD83D\uDC33", "\uD83D\uDC18", "\uD83D\uDC16", "\uD83D\uDD4A",
            "\uD83D\uDC3F", "\uD83E\uDD8D", "\uD83E\uDD8C"
    )

    private val PADDING_BYTE = "11111111"

    private fun byteToString(bite: Byte) = Integer.toBinaryString((bite.toInt() and 0xFF) + 0x100).substring(1)

    private fun byteArrayToString(bytes: ByteArray): String {
        val stringBuilder = StringBuilder(bytes.size * 8)
        for (bite in bytes) {
            stringBuilder.append(byteToString(bite))
        }

        return stringBuilder.toString()
    }

    private fun bitStringToByteArray(bytes: String): ByteArray {
        if (bytes.length % 8 != 0) {
            throw RuntimeException("Unsupported string length")
        }

        val newBytes = ByteArray(bytes.length / 8)

        var index = 0

        val inner = StringBuilder(8)

        for (n in 0..bytes.length - 8 step 8) {
            inner.setLength(0)

            for (i in 0..7) {
                inner.append(bytes[n + i])
            }
            newBytes[index] = Integer.parseInt(inner.toString(), 2).toByte()
            index++
        }

        if (index != newBytes.size) {
            throw UnsupportedOperationException("Byte array was not completely filled, length was " +
                    newBytes.size + ", last filled index was " + (index - 1))
        }

        return newBytes
    }

    /**
     * Encodes a byte array as a string of emojis.
     *
     * @param bytes the bytes to encode in base 47.
     *
     * @return the encoded string of emojis.
     */
    fun encode(bytes: ByteArray) = convertNumber(PADDING_BYTE + byteArrayToString(bytes), BASE_2, BASE_47).toString()

    /**
     * Decodes a string of previously encoded emojis
     * back into the original byte array.
     *
     * @param string the base 47 emoji string to decode.
     *
     * @return the original decoded bytes.
     */
    fun decode(string: String): ByteArray {
        val preConversion = convertNumber(string, BASE_47, BASE_2)

        var prependZero = 8 - preConversion.length % 8

        if (prependZero == 8) {
            prependZero = 0
        }

        val postConversion = StringBuilder(preConversion.length + prependZero)

        for (n in 0..prependZero - 1) {
            postConversion.append('0')
        }

        postConversion.append(preConversion)

        return bitStringToByteArray(postConversion.substring(PADDING_BYTE.length))
    }

    private fun zeroAsBase(base: Int) = valueToDigit(0, base)

    private fun convertNumber(number: String, oldBase: Int, newBase: Int): StringBuilder {

        // Calculating the character length of the new number using
        // http://stackoverflow.com/a/962327/1499541
        val size = Math.round(number.length * Math.log(oldBase.toDouble()) / Math.log(newBase.toDouble()) + 1).toInt()

        val newNumber = StringBuilder(size)

        val remainder = AtomicInteger()

        val reusableResultBuilder = StringBuilder(number.length)
        val reusableValueBuilder = StringBuilder(2)

        val zero = zeroAsBase(oldBase)
        var tempNumber = number
        while (zero != tempNumber) {
            remainder.set(0)
            tempNumber = divideNumber(tempNumber, oldBase, newBase, remainder, reusableResultBuilder, reusableValueBuilder)
            val newDigit = valueToDigit(remainder.get(), newBase)

            newNumber.insert(0, newDigit)
        }

        if (newNumber.isEmpty()) {
            return StringBuilder(zeroAsBase(newBase))
        }

        return newNumber
    }

    private fun divideNumber(number: String,
                             base: Int,
                             divisor: Int,
                             remainder: AtomicInteger,
                             resultBuilder: StringBuilder,
                             valueBuilder: StringBuilder): String {
        remainder.set(0)
        resultBuilder.setLength(0)

        var hasCharacters = false

        var n = 0
        while (n < number.length) {

            val codePoint = number.codePointAt(n)

            val codePointCount = Character.charCount(codePoint)

            valueBuilder.setLength(0)

            val max = n + codePointCount

            for (i in n..max - 1) {
                valueBuilder.append(number[i])
            }

            n += codePointCount

            val digitValue = digitToValue(base, valueBuilder.toString())

            val newRemainder = base * remainder.get() + digitValue
            val newDigitValue = newRemainder / divisor
            remainder.set(newRemainder % divisor)

            if (newDigitValue > 0 || hasCharacters) {
                val newDigits = valueToDigit(newDigitValue, base)
                hasCharacters = true
                resultBuilder.append(newDigits)
            }
        }

        if (resultBuilder.isEmpty()) {
            return zeroAsBase(base)
        }

        return resultBuilder.toString()
    }

    private fun digitToValue(base: Int, digit: String): Int {
        val index: Int
        when (base) {
            BASE_2 -> index = indexOf(CHARACTERS_2_ARRAY, digit)
            BASE_47 -> index = indexOf(CHARACTERS_ARRAY, digit)
            else -> throw RuntimeException("Unsupported base: " + base)
        }

        if (index < 0) {
            throw UnsupportedOperationException("Unable to find string charset for base $base: $digit")
        } else {
            return index
        }
    }

    private fun valueToDigit(value: Int, base: Int): String {
        if (value >= base) {
            throw IndexOutOfBoundsException("Index was $value, must not be greater than $base")
        }

        when (base) {
            BASE_2 -> return CHARACTERS_2_ARRAY[value]
            BASE_47 -> return CHARACTERS_ARRAY[value]
            else -> throw RuntimeException("Unsupported base: " + base)
        }
    }

    private fun indexOf(array: Array<String>, string: String) = array.indices.firstOrNull { string == array[it] } ?: -1

}
