package hu.bmiklos.bc.service.dto;

import hu.bmiklos.bc.domain.entities.Suggestion;
import java.util.Set;

/**
 * @deprecated use {@link Suggestion} instead.
 */
@Deprecated
public record BookAndSuggesterDto(BookDto book, Set<SuggestionDto> suggestions) {}
