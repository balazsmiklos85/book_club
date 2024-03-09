package hu.bmiklos.bc.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.service.ActiveUserService;

@ExtendWith(MockitoExtension.class)
public class VoteControllerTest {
    @Mock
    private ActiveUserService activeUserService;

    @InjectMocks
    private VoteController controller;

    @Test
    void nonAdminUsersCannotNavigateToTheVotingMatrix() {
        when(activeUserService.isAdmin()).thenReturn(false);

        ModelAndView result = controller.getVoteMatrix();

        assertEquals("redirect:/", result.getViewName(),
                "Simple users should be redirected back to the leaderboard instead of the voting matrix.");
    }
}

