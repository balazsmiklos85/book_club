package hu.bmiklos.bc.controller.dto;

import java.util.List;
import java.util.UUID;

public record LeaderboardBookData(UUID id, String author, String title,
        String url, List<SuggestionReference> suggestions, boolean userVoted,
        boolean isNew) {
}

