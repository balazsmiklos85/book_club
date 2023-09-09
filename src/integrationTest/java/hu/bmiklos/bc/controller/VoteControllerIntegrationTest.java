package hu.bmiklos.bc.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.service.VoteService;
import jakarta.transaction.Transactional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
@Transactional
class VoteControllerIntegrationTest extends TestDataCreator {
    @Autowired
    private VoteService voteService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "975032506@test.hu", password = "password", authorities = { "ROLE_USER "})
    void voteAndUnvote() throws Exception {
        createUser(-975032506, "Test User", "975032506@test.hu", "password");
        Book book = createBook();

        mockMvc.perform(put("/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("bookId", book.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        List<Vote> userVotes = voteService.getUserVotes();
        assertEquals(1, userVotes.size(), "There should be a vote in the database.");
        assertEquals(book.getId(), userVotes.get(0).getBookId(), "The vote should belong to the voted book.");

        mockMvc.perform(delete("/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("bookId", book.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        userVotes = voteService.getUserVotes();
        assertEquals(0, userVotes.size(), "The vote should be deleted from the database.");
    }
}