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
package com.anthonycr.base47;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A base encoding algorithm that takes in an array of bytes and outputs a string of emoji.
 * The details of implementation are as follows. First, the binary array is converted to a
 * binary string. This binary string is then converted to base 47. The special change here
 * is that instead of using the normal ASCII charset, an emoji charset of 47 characters is
 * used. This results in an interesting encoded string.
 * <p>
 * This class has 2 methods:
 * <p>
 * {@link #encode(byte[])} - which encodes a byte array, returning an emoji string in base 47.
 * <p>
 * {@link #decode(String)} - which takes an emoji base 47 encoded string and returns the
 * original byte array.
 * <p>
 * This base conversion algorithm was adapted from:
 * http://www.codinghelmet.com/?path=exercises/converting-number-bases
 * <p>
 * Created by restainoa on 12/21/16.
 */
public final class Base47 {

    private static final int BASE_2 = 2;
    private static final int BASE_47 = 47;

    private static final String[] CHARACTERS_2_ARRAY = {
        "0", "1"
    };

    private static final String[] CHARACTERS_ARRAY = {
        "\uD83D\uDC36", "\uD83D\uDC31", "\uD83D\uDC2D", "\uD83D\uDC39", "\uD83D\uDC30", "\uD83D\uDC3B",
        "\uD83D\uDC3C", "\uD83D\uDC28", "\uD83D\uDC2F", "\uD83E\uDD81", "\uD83D\uDC2E", "\uD83D\uDC37",
        "\uD83D\uDC38", "\uD83D\uDC19", "\uD83D\uDC35", "\uD83D\uDE48", "\uD83D\uDE49", "\uD83D\uDE4A",
        "\uD83D\uDC12", "\uD83D\uDC14", "\uD83D\uDC27", "\uD83D\uDC26", "\uD83D\uDC24", "\uD83D\uDC23",
        "\uD83D\uDC25", "\uD83D\uDC3A", "\uD83D\uDC17", "\uD83D\uDC34", "\uD83E\uDD84", "\uD83D\uDC1D",
        "\uD83D\uDC1B", "\uD83D\uDC0C", "\uD83D\uDC1E", "\uD83E\uDD80", "\uD83D\uDC0D", "\uD83D\uDC22",
        "\uD83D\uDC20", "\uD83D\uDC1F", "\uD83D\uDC21", "\uD83D\uDC2C", "\uD83D\uDC33", "\uD83D\uDC18",
        "\uD83D\uDC16", "\uD83D\uDD4A", "\uD83D\uDC3F", "\uD83E\uDD8D", "\uD83E\uDD8C"
    };

    private Base47() {
        throw new UnsupportedOperationException("Class is not instantiable");
    }

    @NotNull
    private static String byteToString(byte bite) {
        return Integer.toBinaryString((bite & 0xFF) + 0x100).substring(1);
    }

    @NotNull
    private static String byteArrayToString(@NotNull byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 8);
        for (byte bite : bytes) {
            stringBuilder.append(byteToString(bite));
        }

        return stringBuilder.toString();
    }

    @NotNull
    private static byte[] bitStringToByteArray(@NotNull String bytes) {
        if (bytes.length() % 8 != 0) {
            throw new RuntimeException("Unsupported string length");
        }

        byte[] newBytes = new byte[bytes.length() / 8];

        int index = 0;

        StringBuilder inner = new StringBuilder(8);

        for (int n = 0; n <= bytes.length() - 8; n += 8) {
            inner.setLength(0);

            for (int i = 0; i < 8; i++) {
                inner.append(bytes.charAt(n + i));
            }
            newBytes[index] = (byte) Integer.parseInt(inner.toString(), 2);
            index++;
        }

        if (index != newBytes.length) {
            throw new UnsupportedOperationException(
                "Byte array was not completely filled, length was " + newBytes.length +
                    ", last filled index was " + (index - 1));
        }

        return newBytes;
    }

    /**
     * Encodes a byte array as a string of emojis.
     *
     * @param bytes the bytes to encode in base 47.
     * @return the encoded string of emojis.
     */
    @NotNull
    public static String encode(@NotNull byte[] bytes) {
        Preconditions.checkNotNull(bytes);

        return convertNumber(byteArrayToString(bytes), BASE_2, BASE_47).toString();
    }

    /**
     * Decodes a string of previously encoded emojis
     * back into the original byte array.
     *
     * @param string the base 47 emoji string to decode.
     * @return the original decoded bytes.
     */
    @NotNull
    public static byte[] decode(@NotNull String string) {
        Preconditions.checkNotNull(string);

        StringBuilder preConversion = convertNumber(string, BASE_47, BASE_2);

        int prependZero = 8 - (preConversion.length() % 8);

        if (prependZero == 8) {
            prependZero = 0;
        }

        StringBuilder postConversion = new StringBuilder(preConversion.length() + prependZero);

        for (int n = 0; n < prependZero; n++) {
            postConversion.append('0');
        }

        postConversion.append(preConversion);

        return bitStringToByteArray(postConversion.toString());
    }

    @NotNull
    private static String zeroAsBase(int base) {
        return valueToDigit(0, base);
    }

    @NotNull
    private static StringBuilder convertNumber(@NotNull String number, int oldBase, int newBase) {
        // Calculating the character length of the new number using
        // http://stackoverflow.com/a/962327/1499541
        int size = (int) Math.round(number.length() * Math.log(oldBase) / Math.log(newBase) + 1);

        StringBuilder newNumber = new StringBuilder(size);

        AtomicInteger remainder = new AtomicInteger();

        StringBuilder reusableResultBuilder = new StringBuilder(number.length());
        StringBuilder reusableValueBuilder = new StringBuilder(2);

        while (!zeroAsBase(oldBase).equals(number)) {
            remainder.set(0);
            number = divideNumber(number, oldBase, newBase, remainder, reusableResultBuilder, reusableValueBuilder);
            String newDigit = valueToDigit(remainder.get(), newBase);

            newNumber.insert(0, newDigit);
        }

        if (newNumber.length() == 0) {
            return new StringBuilder(zeroAsBase(newBase));
        }

        return newNumber;
    }

    @NotNull
    private static String divideNumber(@NotNull String number,
                                       int base,
                                       int divisor,
                                       @NotNull AtomicInteger remainder,
                                       @NotNull StringBuilder resultBuilder,
                                       @NotNull StringBuilder valueBuilder) {
        remainder.set(0);
        resultBuilder.setLength(0);

        boolean hasCharacters = false;

        for (int n = 0; n < number.length(); ) {
            int digitValue;

            final int codePoint = number.codePointAt(n);

            int codePointCount = Character.charCount(codePoint);
            valueBuilder.setLength(0);

            for (int i = n, max = n + codePointCount; i < max; i++) {
                valueBuilder.append(number.charAt(i));
            }

            n += codePointCount;

            digitValue = digitToValue(base, valueBuilder.toString());

            int newRemainder = base * remainder.get() + digitValue;
            int newDigitValue = newRemainder / divisor;
            remainder.set(newRemainder % divisor);

            if (newDigitValue > 0 || hasCharacters) {
                String newDigits = valueToDigit(newDigitValue, base);
                hasCharacters = true;
                resultBuilder.append(newDigits);
            }
        }

        if (resultBuilder.length() == 0) {
            return zeroAsBase(base);
        }

        return resultBuilder.toString();
    }

    private static int digitToValue(int base, @NotNull String digit) {
        int index;
        switch (base) {
            case BASE_2:
                index = indexOf(CHARACTERS_2_ARRAY, digit);
                break;
            case BASE_47:
                index = indexOf(CHARACTERS_ARRAY, digit);
                break;
            default:
                throw new RuntimeException("Unsupported base: " + base);

        }

        if (index < 0) {
            throw new UnsupportedOperationException(
                "Unable to find string charset for base " + base + ": " + digit);
        } else {
            return index;
        }
    }

    @NotNull
    private static String valueToDigit(int value, int base) {
        if (value >= base) {
            throw new IndexOutOfBoundsException("Index was " + value + ", must not be greater than " + base);
        }

        switch (base) {
            case BASE_2:
                return CHARACTERS_2_ARRAY[value];
            case BASE_47:
                return CHARACTERS_ARRAY[value];
            default:
                throw new RuntimeException("Unsupported base: " + base);
        }
    }

    private static int indexOf(@NotNull String[] array, @NotNull String string) {
        for (int n = 0; n < array.length; n++) {
            if (string.equals(array[n])) {
                return n;
            }
        }
        return -1;
    }

}
