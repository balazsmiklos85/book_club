package hu.bmiklos.bc.controller.dto;

import java.util.Collection;
import java.util.UUID;

public record LeaderboardBookData(UUID id, String author, String title,
        String url, Collection<SuggestionReference> suggestions,
        Collection<String> voterHashes, boolean userVoted, boolean isNew) {
}

