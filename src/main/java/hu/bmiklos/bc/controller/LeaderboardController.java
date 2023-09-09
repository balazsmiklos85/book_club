package hu.bmiklos.bc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.BookSortingService;
import hu.bmiklos.bc.service.dto.BookDto;

@Controller
public class LeaderboardController {
    @Autowired
    private ActiveUserService activeUserService;
    
    @Autowired
    private BookSortingService bookSortingService;

    @GetMapping("/")
    public ModelAndView getLeaderboard() {
        List<BookDto> books = bookSortingService.getAll();
        ModelAndView modelAndView = new ModelAndView("leaderboard");
        modelAndView.addObject("books", books);
        modelAndView.addObject("isAdmin", activeUserService.isAdmin());
        return modelAndView;
    }
}
