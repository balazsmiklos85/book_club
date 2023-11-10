package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.UUID;

import org.springframework.lang.Nullable;

public class SuggestionDto {
    private final UUID id;
    private final Instant suggestedAt;
    private final UserDto suggester;

    public SuggestionDto(@Nullable UUID id, Instant suggestedAt, UserDto suggester) {
        this.id = id;
        this.suggestedAt = suggestedAt;
        this.suggester = suggester;
    }

    /**
     * @deprecated Use {@link #SuggestionDto(UUID, Instant, UserDto)} instead.
     */
    @Deprecated
    public SuggestionDto(Instant suggestedAt, UserDto suggester) {
        this(null, suggestedAt, suggester);
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
}