package hu.bmiklos.bc.controller.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ProfileInformation {
    private final List<String> emails;
    private final Integer externalId;
    private final String name;

    public ProfileInformation(String name, Integer externalId, List<String> emails) {
        this.name = name;
        this.externalId = externalId;
        this.emails = Collections.unmodifiableList(emails);
    }

    public List<String> getEmails() {
        return emails;
    }

    public Integer getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(emails, externalId, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ProfileInformation))
            return false;
        ProfileInformation other = (ProfileInformation) obj;
        return Objects.equals(emails, other.emails) && Objects.equals(externalId, other.externalId)
                && Objects.equals(name, other.name);
    }
}