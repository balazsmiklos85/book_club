package hu.bmiklos.bc.view;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import hu.bmiklos.bc.web.dto.LeaderboardBookData;
import hu.bmiklos.bc.web.dto.SuggestionReference;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@WebAppConfiguration
class LeaderboardViewTests {

  @Autowired private SpringTemplateEngine templateEngine;

  @BeforeEach
  void setup() {
    var linkBuilder = new TestLinkBuilder();
    templateEngine.setLinkBuilder(linkBuilder);
  }

  @Test
  void leaderboardViewRendersDeleteButtonForOwnedSuggestion() {
    var suggestion = new SuggestionReference(UUID.randomUUID(), "Test User", true);
    var book =
        new LeaderboardBookData(
            UUID.randomUUID(),
            "Test Author",
            "Test Title",
            "test://url.hu",
            List.of(suggestion),
            List.of(),
            false,
            false);
    List<LeaderboardBookData> books = List.of(book);
    var context = new Context();
    context.setVariable("books", books);

    String html = templateEngine.process("leaderboard", context);

    assertThat(
        html,
        containsString("<form action=" + '"' + "/suggestion/" + suggestion.getId() + "/deletion"));
  }
}
