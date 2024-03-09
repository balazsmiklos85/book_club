package hu.bmiklos.bc.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.controller.mapper.LeaderboardElementsConverter;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.BookSortingService;
import hu.bmiklos.bc.service.VoteService;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.UserDto;

@Controller
public class LeaderboardController {
    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private BookSortingService bookSortingService;

    @Autowired
    private VoteService voteService;

    @GetMapping("/")
    public ModelAndView getRoot() {
        List<BookAndSuggesterDto> books = bookSortingService.getAll();
        List<BookAndSuggesterDto> userVotedBooks = voteService.getVotedBooks();
        Map<UUID, Collection<UserDto>> votersByBooks = voteService.getAllVotersByBooks();

        var booksConverter = new LeaderboardElementsConverter(userVotedBooks, votersByBooks);
        Collection<LeaderboardBookData> bookData = booksConverter.convert(books);
        ModelAndView modelAndView = new ModelAndView("leaderboard");
        modelAndView.addObject("books", bookData);
        modelAndView.addObject("isAdmin", activeUserService.isAdmin());
        return modelAndView;
    }

    @GetMapping("/leaderboard")
    public ModelAndView getLeaderboard() {
        return getRoot();
    }
}
