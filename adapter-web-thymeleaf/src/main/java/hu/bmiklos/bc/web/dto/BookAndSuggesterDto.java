package hu.bmiklos.bc.web.dto;

import java.util.Set;

public record BookAndSuggesterDto(BookDto book, Set<SuggestionDto> suggestions) {
}
