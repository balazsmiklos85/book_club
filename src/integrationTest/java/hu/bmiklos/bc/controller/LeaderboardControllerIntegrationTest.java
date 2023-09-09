package hu.bmiklos.bc.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
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
import hu.bmiklos.bc.model.Email;
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
    @WithMockUser(username = "1254112040@test.hu", password = "password", authorities = { "ROLE_USER ", "ROLE_ADMIN"})
    void adminHasNewEventForm() throws Exception {
        Email email = createUser(-1254112040, "Test Admin", "1254112040@test.hu", "password");
        setAdmin(email.getUser());
        createBook(email.getUser());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<form action=\"/event/new\"")));
    }

    @Test
    @WithMockUser(username = "695103999@test.hu", password = "password", authorities = { "ROLE_USER "})
    void hasVoteButtons() throws Exception {
        Email userEmail = createUser(-695103999, "Test User", "695103999@test.hu", "password");
        Book book = createBook(userEmail.getUser());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<form action=\"/vote\"")))
                .andExpect(content().string(containsString("<input type=\"hidden\" name=\"method\" value=\"put\" />")))
                .andExpect(content().string(containsString("<input type=\"hidden\" value=\"" + book.getId() + "\" name=\"bookId\" />")))
                .andExpect(content().string(containsString("<button type=\"submit\">")));
    }

    @Test
    @WithMockUser(username = "360919753@test.hu", password = "password", authorities = { "ROLE_USER "})
    void homePageCanAddNewBooks() throws Exception {
        createUser(-360919753, "Test User", "360919753@test.hu", "password");

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<form action=\"/book/new\" method=\"get\"")));
    }

    @Test
    @WithMockUser(username = "531406113@test.hu", password = "password", authorities = { "ROLE_USER "})
    void showsLeaderboard() throws Exception {
        Email userEmail = createUser(-531406113, "Test User", "531406113@test.hu", "password");
        createBook(userEmail.getUser());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<a href=\"https://moly.hu/konyvek/robert-c-martin-clean-code\">Robert C. Martin: Clean Code</a>")));
    }

    @Test
    @WithMockUser(username = "943409260@test.hu", password = "password")
    void userHasNoNewEventForm() throws Exception {
        Email userEmail = createUser(-943409260, "Test User", "943409260@test.hu", "password");
        createBook(userEmail.getUser());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<form action=\"/event/new\""))));
    }
}