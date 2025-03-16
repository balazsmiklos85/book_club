package hu.bmiklos.bc.web.controller;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/suggestion")
public class SuggestionController {
  @PostMapping("/{id}/deletion")
  public String deleteSuggestion(@PathVariable UUID id) {
    // TODO implement
    throw new UnsupportedOperationException();
  }
}
