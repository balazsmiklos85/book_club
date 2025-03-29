package hu.bmiklos.bc.service.dto;

import java.util.Set;

import hu.bmiklos.bc.domain.entities.Suggestion;

/**
 * @deprecated use {@link Suggestion} instead.
 */
@Deprecated
public record BookAndSuggesterDto(BookDto book, Set<SuggestionDto> suggestions) {
}
