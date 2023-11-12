package hu.bmiklos.bc.service.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.lang.Nullable;

public class UserDto {
    @Nullable
    private final UUID id;
    @Nullable
    private final String name;
    private final Integer externalId;
    private List<String> emails;

    public UserDto(UUID id, String name, Integer externalId) {
        this(id, name, externalId, List.of());
    }

    public UserDto(UUID id, String name, Integer externalId, List<String> emails) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.emails = emails;
    }

    @Nullable
    public UUID getId() {
        return id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public Integer getExternalId() {
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
