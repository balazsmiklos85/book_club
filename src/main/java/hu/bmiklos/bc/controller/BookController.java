package hu.bmiklos.bc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.service.SuggestionService;

@Controller
@RequestMapping("/book")
public class BookController {
    private final SuggestionService suggestionService;

    public BookController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/new")
    public ModelAndView newBookForm() {
        return new ModelAndView("book/new", "book", new CreateBookRequest());
    }

    @PostMapping
    public ModelAndView createBook(@ModelAttribute CreateBookRequest book) {
        suggestionService.createSuggestion(book);
        return new ModelAndView("redirect:/");
    }
}
