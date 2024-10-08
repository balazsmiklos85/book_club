package hu.bmiklos.bc.view;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@WebAppConfiguration
class LeaderboardViewTests {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @BeforeEach
    void setup() {
        var linkBuilder = new TestLinkBuilder();
        templateEngine.setLinkBuilder(linkBuilder);
    }

    @Test
    void leaderboardViewRendersVotersAsGravatars() {
        var book = new LeaderboardBookData(UUID.randomUUID(), "Test Author",
                "Test Title", "test://url.hu", List.of(),
                List.of("2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae"),
                false, false);
        List<LeaderboardBookData> books = List.of(book);

        var context = new Context();
        context.setVariable("books", books);

        String html = templateEngine.process("leaderboard", context);

        assertThat(html, containsString("<img src=" + '"'
                    + "https://gravatar.com/avatar/"
                    + "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae"));
    }
}

