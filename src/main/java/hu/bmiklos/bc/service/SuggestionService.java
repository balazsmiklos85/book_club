package hu.bmiklos.bc.service;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;

public interface SuggestionService {

    void createSuggestion(CreateBookRequest book);
    
}