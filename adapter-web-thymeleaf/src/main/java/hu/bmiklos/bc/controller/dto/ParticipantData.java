package hu.bmiklos.bc.controller.dto;

import java.util.Optional;

public class ParticipantData {
    private String name;
    private ExternalId externalId;

    public ParticipantData(String name, ExternalId externalId) {
        this.name = name;
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return Optional.ofNullable(name)
            .orElse(externalId.toString());
    }
}