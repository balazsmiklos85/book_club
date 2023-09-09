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
import hu.bmiklos.bc.model.Email;
import jakarta.transaction.Transactional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
@Transactional
class EventControllerIntegrationTest extends TestDataCreator {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    @WithMockUser(username = "319705020@test.hu", password = "password", authorities = { "ROLE_USER "})
    void eventFormShown() throws Exception {
        Email userEmail = createUser(-319705020, "Test User", "319705020@test.hu", "password");
        Book book = createBook(userEmail.getUser());

        mockMvc.perform(get("/event/new?bookId=" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<form action=\"/event\" method=\"post\">")));
    }
}
