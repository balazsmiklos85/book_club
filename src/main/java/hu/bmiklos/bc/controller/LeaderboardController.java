package hu.bmiklos.bc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.repository.BookRepository;
import hu.bmiklos.bc.service.ActiveUserService;

@Controller
public class LeaderboardController {
    @Autowired
    private ActiveUserService activeUserService;
    
    // TODO The controller should not depend on the repository. Instead, it should depend on a service that uses the repository.
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public ModelAndView getLeaderboard() {
        List<Book> books = bookRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("leaderboard");
        modelAndView.addObject("books", books);
        modelAndView.addObject("isAdmin", activeUserService.isAdmin());
        return modelAndView;
    }
}
