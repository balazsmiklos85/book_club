package hu.bmiklos.bc.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.repository.BookRepository;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
class BookControllerIntegrationTest extends IntegrationTestWithUser {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookRepository bookRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void newBookForm() throws Exception {
        mockMvc.perform(get("/book/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<form action=\"/book\" method=\"post\">")))
                .andExpect(content().string(containsString("<input type=\"text\" name=\"title\"")))
                .andExpect(content().string(containsString("<input type=\"text\" name=\"author\"")))
                .andExpect(content().string(containsString("<input type=\"text\" name=\"url\"")))
                .andExpect(content().string(containsString("<input type=\"submit\" ")));
    }

    @Test
    @WithMockUser(username = "255832533@test.hu", password = "password", authorities = { "ROLE_USER "})
    void bookCreation() throws Exception {
        createUserForTest(-255832533, "Test User", "255832533@test.hu", "password");

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Clean Code")
                .param("author", "Robert C. Martin")
                .param("url", "https://moly.hu/konyvek/robert-c-martin-clean-code"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Optional<Book> bookInDb = bookRepository.findByUrl("https://moly.hu/konyvek/robert-c-martin-clean-code");

        assertTrue(bookInDb.isPresent(), "Book not saved to database.");
        Book book = bookInDb.get();
        assertEquals("Clean Code", book.getTitle(), "Book title is not saved correctly.");
        assertEquals("Robert C. Martin", book.getAuthor(), "Book author is not saved correctly.");
        assertEquals(-255832533, book.getRecommenderExternalId(), "Book recommender external ID is not saved correctly.");

        bookRepository.delete(book);
        deleteTestedUser();
    }
}