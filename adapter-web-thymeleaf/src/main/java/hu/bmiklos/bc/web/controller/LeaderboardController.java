package hu.bmiklos.bc.web.controller;

import hu.bmiklos.bc.business.security.ActiveUserService;
import hu.bmiklos.bc.business.usecase.SortedBookQueryService;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.web.dto.LeaderboardBookData;
import hu.bmiklos.bc.web.mapper.LeaderboardElementConverter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class LeaderboardController {
  private final ActiveUserService activeUserService;

  private final SortedBookQueryService bookService;

  @GetMapping("/")
  public ModelAndView getRoot() {
    final User user = activeUserService.getUser();
    final var booksConverter = new LeaderboardElementConverter(user);
    final List<LeaderboardBookData> books =
        bookService.getLeaderboardBooks().stream().map(booksConverter::convert).toList();

    final ModelAndView modelAndView = new ModelAndView("leaderboard");
    modelAndView.addObject("books", books);
    modelAndView.addObject("isAdmin", user.isAdmin());
    return modelAndView;
  }

  @GetMapping("/leaderboard")
  public ModelAndView getLeaderboard() {
    return getRoot();
  }
}
