package hu.bmiklos.bc.service.dto;

import java.util.Objects;
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

    @Override
    public int hashCode() {
        return Objects.hash(id, name, externalId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof UserDto))
            return false;
        UserDto other = (UserDto) obj;
        return Objects.equals(id, other.id) && Objects.equals(name, other.name) && externalId == other.externalId;
    }
}
