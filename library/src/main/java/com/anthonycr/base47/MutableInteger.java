package com.anthonycr.base47;

/**
 * A mutable equivalent to Java's {@link Integer} class.
 * <p>
 * Created by restainoa on 12/21/16.
 */
final class MutableInteger {

    private int number;

    void setNumber(int number) {
        this.number = number;
    }

    int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
               o != null && getClass() == o.getClass() && number == ((MutableInteger) o).getNumber();
    }

    @Override
    public int hashCode() {
        return number;
    }
}
