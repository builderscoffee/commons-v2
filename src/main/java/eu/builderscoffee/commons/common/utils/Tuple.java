package eu.builderscoffee.commons.common.utils;

import lombok.Data;

/**
 * A tuple consist of 2 anonymous values that can be stored in an instance
 * @param <X>
 * @param <Y>
 */
@Data
public class Tuple<X, Y> {
    private X left;
    private Y right;

    /**
     * Used only for reflection
     */
    public Tuple() {
    }

    public Tuple(X left, Y right) {
        this.left = left;
        this.right = right;
    }
}
