package hu.bmiklos.bc.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.VoteRequest;
import hu.bmiklos.bc.service.VoteService;

@Controller
@RequestMapping("/vote")
public class VoteController {
    @Autowired
    private VoteService voteService;

    @PostMapping
    public ModelAndView handleVote(@ModelAttribute VoteRequest voteRequest) {
        String requestMethod = voteRequest.getMethod()
            .toUpperCase();
        switch (requestMethod) {
            case "PUT":
                return vote(voteRequest);
            case "DELETE":
                return unvote(voteRequest);
            default:
                throw new IllegalArgumentException("Invalid request method: " + voteRequest.getMethod());
        }
    }

    @PutMapping
    public ModelAndView vote(@ModelAttribute VoteRequest voteRequest) {
        UUID bookId = UUID.fromString(voteRequest.getBookId());
        voteService.vote(bookId);
        return new ModelAndView("redirect:/");
    }

    @DeleteMapping
    public ModelAndView unvote(@ModelAttribute VoteRequest voteRequest) {
        UUID bookId = UUID.fromString(voteRequest.getBookId());
        voteService.unvote(bookId);
        return new ModelAndView("redirect:/");
    }
}