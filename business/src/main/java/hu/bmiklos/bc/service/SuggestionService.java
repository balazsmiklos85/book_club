package hu.bmiklos.bc.service;

import java.util.UUID;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.controller.dto.SuggestionFormData;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;

public interface SuggestionService {

    void createSuggestion(CreateBookRequest book);

    SuggestionDto getSuggestion(UUID id);

    BookAndSuggesterDto getBookBySuggestionId(UUID id);

    void updateSuggestion(UUID id, SuggestionFormData suggestionData);

    void removeForBook(UUID bookId);
}

