package hu.bmiklos.bc.service.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserDto {
    private final UUID id;
    private final String name;
    private final int externalId;
    private List<String> emails;

    public UserDto(UUID id, String name, int externalId) {
        this(id, name, externalId, List.of());
    }

    public UserDto(UUID id, String name, int externalId, List<String> emails) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.emails = emails;
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

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = List.copyOf(emails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, externalId, emails);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof UserDto))
            return false;
        UserDto other = (UserDto) obj;
        return Objects.equals(id, other.id) && Objects.equals(name, other.name) && externalId == other.externalId
                && Objects.equals(emails, other.emails);
    }
}
