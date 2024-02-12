package hu.bmiklos.bc.view;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import hu.bmiklos.bc.controller.dto.SuggestionFormData;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class ShowSuggestionViewTests {
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Test
    void suggestionShowViewRendersValuesButNotAsInput() {
        var context = new Context();
        var suggestion = new SuggestionFormData("test book ID", "test suggestion ID", "Test Author", "Test Title",
                "test://url.hu", "Test Description");
        context.setVariable("suggestion", suggestion);

        String html = templateEngine.process("suggestion/show", context);

        assertThat(html, not(containsString("<input")));
        assertThat(html, containsString("Test Author"));
        assertThat(html, containsString("Test Title"));
        assertThat(html, containsString("test://url.hu"));
        assertThat(html, containsString("Test Description"));
    }
}

