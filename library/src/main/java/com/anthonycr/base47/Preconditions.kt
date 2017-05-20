package com.anthonycr.base47

/**
 * Preconditions checking.
 *
 *
 * Created by restainoa on 12/21/16.
 */
internal class Preconditions private constructor() {

    companion object {

        fun checkNotNull(`object`: Any?) {
            if (`object` == null) {
                throw NullPointerException("Object must not be null")
            }
        }
    }

}
