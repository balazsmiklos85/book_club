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

import hu.bmiklos.bc.model.Book;
import jakarta.transaction.Transactional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
@Transactional
class LeaderboardControllerIntegrationTest extends TestDataCreator {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "695103999@test.hu", password = "password", authorities = { "ROLE_USER "})
    void hasVoteButtons() throws Exception {
        createUser(-695103999, "Test User", "695103999@test.hu", "password");
        Book book = createBook();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<form action=\"/vote\"")))
                .andExpect(content().string(containsString("<input type=\"hidden\" name=\"_method\" value=\"put\" />")))
                .andExpect(content().string(containsString("<input type=\"hidden\" value=\"" + book.getId() + "\" name=\"bookId\" />")))
                .andExpect(content().string(containsString("<button type=\"submit\">")));
    }

    @Test
    void homePageCanAddNewBooks() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<a href=\"/book/new\"")));
    }

    @Test
    @WithMockUser(username = "531406113@test.hu", password = "password", authorities = { "ROLE_USER "})
    void showsLeaderboard() throws Exception {
        createUser(-531406113, "Test User", "531406113@test.hu", "password");
        createBook();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<a href=\"https://moly.hu/konyvek/robert-c-martin-clean-code\">Robert C. Martin: Clean Code</a>")));
    }
}