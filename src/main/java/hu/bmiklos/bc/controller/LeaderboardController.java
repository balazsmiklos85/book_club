package hu.bmiklos.bc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.repository.BookRepository;

@Controller
public class LeaderboardController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public ModelAndView getLeaderboard() {
        List<Book> books = bookRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("leaderboard");
        modelAndView.addObject("books", books);
        return modelAndView;
    }
}
