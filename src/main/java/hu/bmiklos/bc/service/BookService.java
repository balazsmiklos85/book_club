package hu.bmiklos.bc.service;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.dto.BookDto;

public interface BookService {

    //TODO don't use the entity class as a return type
    Book createBook(CreateBookRequest book);

    BookDto getBookById(String bookId);    
}
