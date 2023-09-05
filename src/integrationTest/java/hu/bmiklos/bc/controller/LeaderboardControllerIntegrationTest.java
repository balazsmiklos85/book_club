package hu.bmiklos.bc.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.service.BookService;
import jakarta.transaction.Transactional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
@Transactional
class LeaderboardControllerIntegrationTest extends IntegrationTestWithUser {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookService bookService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void homePageCanAddNewBooks() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<button href=\"/book/new\"")));
    }

    @Test
    @WithMockUser(username = "531406113@test.hu", password = "password", authorities = { "ROLE_USER "})
    void newBookForm() throws Exception {
        createUserForTest(-531406113, "Test User", "531406113@test.hu", "password");

        var createBookRequest = new CreateBookRequest();
        createBookRequest.setAuthor("Robert C. Martin");
        createBookRequest.setTitle("Clean Code");
        createBookRequest.setUrl("https://moly.hu/konyvek/robert-c-martin-clean-code");
        bookService.createBook(createBookRequest);


        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<a href=\"https://moly.hu/konyvek/robert-c-martin-clean-code\">Robert C. Martin: Clean Code</a>")));
    }
}