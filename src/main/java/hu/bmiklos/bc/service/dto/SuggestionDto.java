package hu.bmiklos.bc.service.dto;

import java.time.Instant;

public class SuggestionDto {
    private final Instant suggestedAt;
    private final UserDto suggester;

    public SuggestionDto(Instant suggestedAt, UserDto suggester) {
        this.suggestedAt = suggestedAt;
        this.suggester = suggester;
    }

    public Instant getSuggestedAt() {
        return suggestedAt;
    }

    public UserDto getSuggester() {
        return suggester;
    }
}