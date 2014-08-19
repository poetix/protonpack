package com.codepoetics.protonpack;

import java.util.Objects;

public class ZippedPair<L, R> {

    public static <L, R> ZippedPair<L, R> of(L left, R right) {
        return new ZippedPair<>(left, right);
    }

    private final L left;
    private final R right;

    private ZippedPair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ZippedPair other = (ZippedPair) obj;
        return Objects.equals(this.left, other.left) && Objects.equals(this.right, other.right);
    }
}
