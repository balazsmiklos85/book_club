package hu.bmiklos.bc.controller.dto;

import java.util.Optional;

import org.springframework.lang.Nullable;

public class ExternalId {
    private final String representation;

    public ExternalId(@Nullable Integer externalId) {
        this.representation = Optional.ofNullable(externalId)
            .map(String::valueOf)
            .orElse("N/A");
    }

    @Override
    public String toString() {
        return "[" + representation + "]";
    }
}