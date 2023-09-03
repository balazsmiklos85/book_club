package hu.bmiklos.bc.service;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.model.Book;

public interface BookService {

    Book createBook(CreateBookRequest book);
    
}
