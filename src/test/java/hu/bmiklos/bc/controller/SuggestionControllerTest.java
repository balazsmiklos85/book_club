package hu.bmiklos.bc.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.SuggestionService;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.dto.UserDto;

class SuggestionControllerTest {
    private ActiveUserService activeUserService;
    private SuggestionController controller;
    private SuggestionService suggestionService;

    @BeforeEach
    void setup() {
        activeUserService = mock(ActiveUserService.class);
        when(activeUserService.getUserId()).thenReturn(UUID.randomUUID());
        suggestionService = mock(SuggestionService.class);
        controller = new SuggestionController(activeUserService, suggestionService);
    }

    @Test
    void getSuggestionEditsOwnSuggestions() {
        var testSuggestionId = UUID.randomUUID();
        var testUserId = UUID.randomUUID();
        var suggester = new UserDto(testUserId, "User", -1);
        var suggestion = new SuggestionDto(testSuggestionId, Instant.now(), suggester, "Description");
        var book = new BookAndSuggesterDto(
                new BookDto(UUID.randomUUID(), "Author", "Title", "url://"),
                Set.of(suggestion));
        when(suggestionService.getBookBySuggestionId(testSuggestionId)).thenReturn(book);
        when(activeUserService.isCurrentUser(testUserId)).thenReturn(true);

        ModelAndView result = controller.getSuggestion(testSuggestionId);

        assertEquals("suggestion/edit", result.getViewName(), "The users should be able to edit their own suggestions.");
    }

    @Test
    void getSuggestionShowsSuggestionsOfOthers() {
        var testSuggestionId = UUID.randomUUID();
        var testUserId = UUID.randomUUID();
        var suggester = new UserDto(testUserId, "User", -1);
        var suggestion = new SuggestionDto(testSuggestionId, Instant.now(), suggester, "Description");
        var book = new BookAndSuggesterDto(
                new BookDto(UUID.randomUUID(), "Author", "Title", "url://"),
                Set.of(suggestion));
        when(suggestionService.getBookBySuggestionId(testSuggestionId)).thenReturn(book);
        when(activeUserService.isCurrentUser(testUserId)).thenReturn(false);

        ModelAndView result = controller.getSuggestion(testSuggestionId);

        assertEquals("suggestion/show", result.getViewName(), "The users should be able to edit their own suggestions.");
    }
}

