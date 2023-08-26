package hu.bmiklos.bc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LeaderboardController {

    @GetMapping("/")
    public String getLeaderboard() {
        return "leaderboard.html";
    }
}
