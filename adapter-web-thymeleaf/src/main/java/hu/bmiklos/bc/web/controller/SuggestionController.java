package hu.bmiklos.bc.web.controller;

import hu.bmiklos.bc.business.security.ActiveUserService;
import hu.bmiklos.bc.business.usecase.SuggestionRemovalService;
import hu.bmiklos.bc.domain.entities.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/suggestion")
@RequiredArgsConstructor
public class SuggestionController {
  private final ActiveUserService activeUserService;

  private final SuggestionRemovalService suggestionRemovalService;

  @PostMapping("/{id}/deletion")
  public ModelAndView deleteSuggestion(@PathVariable UUID id) {
    User user = activeUserService.getUser();
    suggestionRemovalService.removeSuggestion(user, id);
    return new ModelAndView("redirect:/");
  }
}
