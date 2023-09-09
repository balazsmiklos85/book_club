package hu.bmiklos.bc.controller;

import org.springframework.beans.factory.annotation.Autowired;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.BookService;
import hu.bmiklos.bc.service.UserService;

// TODO Composition over inheritance. This should be a {@link Component} that is injected into the test classes. But that changes is needed to be done for all test classes at the same time.
public abstract class TestDataCreator {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;
    
    protected Book createBook() {
        var createBookRequest = new CreateBookRequest();
        createBookRequest.setAuthor("Robert C. Martin");
        createBookRequest.setTitle("Clean Code");
        createBookRequest.setUrl("https://moly.hu/konyvek/robert-c-martin-clean-code");
        return bookService.createBook(createBookRequest);
    }

    protected Email createUser(int externalId, String name, String email, String password) {
        return userService.registerUser("" + externalId, name, email, email, password, password);
    }

    protected void setAdmin(User user) {
        userService.setAsAdmin(user.getId());
    }
}
