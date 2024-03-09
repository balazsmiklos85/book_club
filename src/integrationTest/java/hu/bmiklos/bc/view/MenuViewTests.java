package hu.bmiklos.bc.view;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@WebAppConfiguration
class MenuViewTests {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @BeforeEach
    void setup() {
        if (templateEngine.getLinkBuilders()
                .stream()
                .anyMatch(TestLinkBuilder.class::isInstance)) {
            return;
        } else {
            var linkBuilder = new TestLinkBuilder();
            templateEngine.setLinkBuilder(linkBuilder);
        }
    }

    @Test
    void adminsCanSeeTheVotingMatrix() {
        var context = new Context();
        context.setVariable("isAdmin", true);

        String html = templateEngine.process("fragments/menu", context);

        assertThat(html, containsString("<a href=\"/vote/matrix\">🔧"));
    }

    @Test
    void usersCannotSeeTheVotingMatrix() {
        var context = new Context();
        context.setVariable("isAdmin", false);

        String html = templateEngine.process("fragments/menu", context);

        assertThat(html, not(containsString("<a href=\"/vote/matrix\">🔧")));
    }
}

