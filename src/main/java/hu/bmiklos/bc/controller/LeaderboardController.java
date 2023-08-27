package hu.bmiklos.bc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LeaderboardController {

    @GetMapping("/")
    public ModelAndView getLeaderboard() {
        return new ModelAndView("leaderboard");
    }
}
