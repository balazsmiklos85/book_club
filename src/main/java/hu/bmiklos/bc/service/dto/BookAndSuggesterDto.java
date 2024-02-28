package hu.bmiklos.bc.service.dto;

import java.util.Set;

public record BookAndSuggesterDto(BookDto book, Set<SuggestionDto> suggestions) {
}
