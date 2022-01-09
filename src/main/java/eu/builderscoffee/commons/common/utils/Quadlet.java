package eu.builderscoffee.commons.common.utils;

import lombok.Data;

/**
 * A Quadlet consist of 4 anonymous values that can be stored in an instance
 * @param <W>
 * @param <X>
 * @param <Y>
 * @param <Z>
 */
@Data
public class Quadlet<W, X, Y, Z> {
    private W first;
    private X second;
    private Y third;
    private Z fourth;

    /**
     * Used only for reflection
     */
    public Quadlet() {
    }

    public Quadlet(W first, X second, Y third, Z fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }
}
