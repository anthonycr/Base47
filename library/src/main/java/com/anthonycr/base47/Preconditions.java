package com.anthonycr.base47;


import org.jetbrains.annotations.Nullable;

/**
 * Preconditions checking.
 * <p>
 * Created by restainoa on 12/21/16.
 */
final class Preconditions {

    private Preconditions() {
        throw new UnsupportedOperationException("Class is not instantiable");
    }

    static void checkNotNull(@Nullable Object object) {
        if (object == null) {
            throw new NullPointerException("Object must not be null");
        }
    }

}
