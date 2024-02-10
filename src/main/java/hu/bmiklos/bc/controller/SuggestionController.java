package hu.bmiklos.bc.controller;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.controller.dto.SuggestionFormData;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.BookService;
import hu.bmiklos.bc.service.SuggestionService;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.dto.UserDto;
import hu.bmiklos.bc.service.mapper.SuggestionDtoToSuggestionFormDataConverter;

@Controller
@RequestMapping("/suggestion")
public class SuggestionController {
    private final ActiveUserService activeUserService;
    private final SuggestionService suggestionService;

    public SuggestionController(ActiveUserService activeUserService, SuggestionService suggestionService) {
        this.activeUserService = activeUserService;
        this.suggestionService = suggestionService;
    }

    @GetMapping("/new")
    public ModelAndView newBookForm() {
        return new ModelAndView("suggestion/new", "book", new CreateBookRequest());
    }

    @GetMapping("/{id}")
    public ModelAndView getSuggestion(@PathVariable UUID id) {
        BookAndSuggesterDto bookAndSuggestion = suggestionService.getBookBySuggestionId(id);
        Optional<SuggestionDto> suggestion = getSuggestion(bookAndSuggestion, id);
        if (suggestion.isEmpty()) {
            throw new IllegalArgumentException("No suggestion found with ID " + id);
        }
        SuggestionFormData suggestionData = new SuggestionDtoToSuggestionFormDataConverter().convert(bookAndSuggestion);
        UUID suggesterId = suggestion.map(SuggestionDto::getSuggester)
                        .map(UserDto::getId)
                        .orElse(null);
        String viewName = "suggestion/" + (activeUserService.isCurrentUser(suggesterId) ? "edit" : "show");
        return new ModelAndView(viewName, "suggestion", suggestionData);
    }

    @PostMapping("/{id}")
    public ModelAndView updateSuggestion(@PathVariable UUID id, @ModelAttribute SuggestionFormData suggestionData) {
        SuggestionDto suggestion = suggestionService.getSuggestion(id);

        if (!activeUserService.isCurrentUser(suggestion.getSuggester().getId())) {
            throw new IllegalArgumentException("You can only edit your own suggestions.");
        }

        suggestionService.updateSuggestion(id, suggestionData);
        return new ModelAndView("redirect:/");
    }

    @PostMapping
    public ModelAndView createBook(@ModelAttribute CreateBookRequest book) {
        suggestionService.createSuggestion(book);
        return new ModelAndView("redirect:/");
    }

    private Optional<SuggestionDto> getSuggestion(BookAndSuggesterDto bookAndSuggestion, UUID id) {
        return bookAndSuggestion.getSuggesters()
                .stream()
                .filter(suggestion -> id.equals(suggestion.getId()) || suggestion.getId() == null && id.equals(bookAndSuggestion.getBook().getId()))
                .findFirst();
    }
}
