package hu.bmiklos.bc.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.VoteRequest;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.VoteService;

@Controller
@RequestMapping("/vote")
public class VoteController {
    private final VoteService voteService;

    private final ActiveUserService activeUserService;

    public VoteController(final VoteService voteService,
            final ActiveUserService activeUserService) {
        this.voteService = voteService;
        this.activeUserService = activeUserService;
    }

    @PostMapping
    public ModelAndView handleVote(@ModelAttribute final VoteRequest voteRequest) {
        final String requestMethod = voteRequest.getMethod()
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
    public ModelAndView vote(@ModelAttribute final VoteRequest voteRequest) {
        final UUID bookId = UUID.fromString(voteRequest.getBookId());
        voteService.vote(bookId);
        return new ModelAndView("redirect:/");
    }

    @DeleteMapping
    public ModelAndView unvote(@ModelAttribute final VoteRequest voteRequest) {
        final UUID bookId = UUID.fromString(voteRequest.getBookId());
        voteService.unvote(bookId);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/matrix")
    public ModelAndView getVoteMatrix() {
        if (!activeUserService.isAdmin()) {
            return new ModelAndView("redirect:/");
        }

        final ModelAndView modelAndView = new ModelAndView("matrix");
        modelAndView.addObject("matrix", voteService.getMatrix());
        return modelAndView;
    }
}
