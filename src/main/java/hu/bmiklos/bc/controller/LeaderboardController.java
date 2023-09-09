package hu.bmiklos.bc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.BookSortingService;
import hu.bmiklos.bc.service.VoteService;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.mapper.BookMapper;

@Controller
public class LeaderboardController {
    @Autowired
    private ActiveUserService activeUserService;
    
    @Autowired
    private BookSortingService bookSortingService;

    @Autowired
    private VoteService voteService;

    @GetMapping("/")
    public ModelAndView getLeaderboard() {
        List<BookDto> books = bookSortingService.getAll();
        List<BookDto> userVotedBooks = voteService.getVotedBooks();
        List<LeaderboardBookData> bookData = BookMapper.mapToLeaderboardBookData(books, userVotedBooks);

        ModelAndView modelAndView = new ModelAndView("leaderboard");
        modelAndView.addObject("books", bookData);
        modelAndView.addObject("isAdmin", activeUserService.isAdmin());
        return modelAndView;
    }
}
