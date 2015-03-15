package com.codepoetics.protonpack.collectors;

public class NonUniqueValueException extends RuntimeException {

    private final Object firstInstance;
    private final Object secondInstance;

    public NonUniqueValueException(Object firstInstance, Object secondInstance) {
        this.firstInstance = firstInstance;
        this.secondInstance = secondInstance;
    }

    @Override
    public String getMessage() {
        return String.format("Duplicate vales found: %s and %s", firstInstance, secondInstance);
    }
}
