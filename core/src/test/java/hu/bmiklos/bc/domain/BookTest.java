package hu.bmiklos.bc.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookTest {
	// TODO this is just a test to see if coverage reports are aggregated, remove this when there will be meaningful tests for the Book class
  @Test
  void testGetter() {
    var book =
        new Book(
            UUID.randomUUID(), "Test Author", "Test Title", "https://test.bmiklos.hu/test_book");

    String actualTitle = book.getTitle();

    assertEquals("Test Title", actualTitle, "The title should be fetched fine.");
  }
}
