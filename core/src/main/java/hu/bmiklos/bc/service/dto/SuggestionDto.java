package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.UUID;

import org.springframework.lang.Nullable;

public class SuggestionDto {
    private final UUID id;
    private final Instant suggestedAt;
    private final UserDto suggester;
    private final String description;

    public SuggestionDto(@Nullable UUID id, Instant suggestedAt, UserDto suggester, String description) {
        this.id = id;
        this.suggestedAt = suggestedAt;
        this.suggester = suggester;
        this.description = description;
    }

    /**
     * @deprecated Use {@link #SuggestionDto(UUID, Instant, UserDto)} instead.
     */
    @Deprecated
    public SuggestionDto(Instant suggestedAt, UserDto suggester, String description) {
        this(null, suggestedAt, suggester, description);
    }

    @Nullable
    public UUID getId() {
        return id;
    }

    public Instant getSuggestedAt() {
        return suggestedAt;
    }

    public UserDto getSuggester() {
        return suggester;
    }

    public String getDescription() {
        return description;
    }
}