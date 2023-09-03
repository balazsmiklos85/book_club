package hu.bmiklos.bc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.repository.BookRepository;

@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/new")
    public ModelAndView newBookForm() {
        return new ModelAndView("book/new", "book", new Book());
    }

    @PostMapping
    public ModelAndView createBook(@ModelAttribute Book book) {
        bookRepository.save(book);
        return new ModelAndView("redirect:/");
    }
}