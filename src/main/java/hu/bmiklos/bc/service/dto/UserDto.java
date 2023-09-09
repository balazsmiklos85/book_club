package hu.bmiklos.bc.service.dto;

import java.util.UUID;

public class UserDto {
    private final UUID id;
    private final String name;
    private final int externalId;

    public UserDto(UUID id, String name, int externalId) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getExternalId() {
        return externalId;
    }    
}
