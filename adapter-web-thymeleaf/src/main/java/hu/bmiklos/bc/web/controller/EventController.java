package hu.bmiklos.bc.web.controller;

import hu.bmiklos.bc.business.usecase.EventCreationService;
import hu.bmiklos.bc.business.usecase.SuggestionDetailsService;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.web.mapper.DateFormatter;
import hu.bmiklos.bc.web.mapper.TimeFormatter;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

  private final SuggestionDetailsService suggestionService;
  private final EventCreationService eventCreationService;

  @GetMapping("/new")
  public ModelAndView newBookForm(@RequestParam String bookId) {
    Suggestion suggestion =
        suggestionService
            .findOldestByBookId(UUID.fromString(bookId))
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Suggestion not found.")); // FIXME rename //TODO create dedicated exception
    Optional<Instant> proposedDateTime =
        eventCreationService
            .findLastEvent()
            .map(event -> event.getTime())
            .map(lastTime -> lastTime.proposeNewDate());
    Collection<User> users = eventCreationService.getAllUsers();
    ModelAndView modelAndView = new ModelAndView("event/new");
    modelAndView.addObject("author", suggestion.getBook().getAuthor());
    modelAndView.addObject("title", suggestion.getBook().getTitle());
    modelAndView.addObject("bookId", bookId);

    if (proposedDateTime.isPresent()) {
      DateFormatter dateFormatter = new DateFormatter();
      TimeFormatter timeFormatter = new TimeFormatter();
      modelAndView.addObject("proposedDate", dateFormatter.convert(proposedDateTime.get()));
      modelAndView.addObject("proposedTime", timeFormatter.convert(proposedDateTime.get()));
    }
    Optional.of(suggestion)
        .map(Suggestion::getSuggester)
        .map(User::getId)
        .ifPresent(hostId -> modelAndView.addObject("host", hostId));
    modelAndView.addObject("users", users);
    return modelAndView;
  }
}
