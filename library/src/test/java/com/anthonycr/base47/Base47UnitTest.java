package com.anthonycr.base47;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Sanity test for Base47 encoding.
 * <p>
 * Created by restainoa on 12/21/16.
 */
public class Base47UnitTest {

    private static final int ITERATIONS = 300;

    private final SecureRandom mRandom = new SecureRandom();

    @Test
    public void testEncode_Decode() throws Exception {
        for (int n = 0; n < ITERATIONS; n++) {
            String randomString = randomString(n);
            String encoded = Base47.encode(randomString.getBytes());
            Assert.assertNotNull(randomString);
            Assert.assertNotNull(encoded);
            Assert.assertNotEquals(randomString, encoded);

            byte[] decoded = Base47.decode(encoded);

            Assert.assertEquals(randomString, new String(decoded));
        }
    }

    private String randomString(int number) {
        return new BigInteger(number, mRandom).toString(32);
    }

}
