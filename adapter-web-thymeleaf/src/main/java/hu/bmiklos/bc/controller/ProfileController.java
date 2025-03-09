package hu.bmiklos.bc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.ProfileInformation;
import hu.bmiklos.bc.controller.mapper.UserMapper;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.UserService;
import hu.bmiklos.bc.service.dto.UserDto;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private UserService userService;

    @GetMapping
    // TODO add integration test
    public ModelAndView profile() {
        UserDto user = activeUserService.getUser();
        ProfileInformation userData = UserMapper.mapToProfileInformation(user);

        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", userData);
        modelAndView.addObject("isAdmin", activeUserService.isAdmin());
        return modelAndView;
    }

    @PostMapping("/email")
    // TODO add integration test
    public ModelAndView handleEmailRequest(@RequestParam String email, @RequestParam String method) {
        String requestMethod = method.toUpperCase();
        switch (requestMethod) {
            case "PUT":
                return addEmail(email);
            case "DELETE":
                return deleteEmail(email);
            default:
                throw new IllegalArgumentException("Invalid request method: " + method);
        }
    }

    @PutMapping("/email")
    // TODO add integration test
    public ModelAndView addEmail(@RequestParam String email) {
        userService.addEmail(email);
        return new ModelAndView("redirect:/profile");
    }

    @DeleteMapping("/email")
    // TODO add integration test
    public ModelAndView deleteEmail(@RequestParam String email) {
        userService.deleteEmail(email);
        return new ModelAndView("redirect:/profile");
    }

    @PostMapping("/password")
    // TODO add integration test
    public ModelAndView changePassword(@RequestParam String oldPassword, @RequestParam String password, @RequestParam String confirmPassword) {
        userService.changePassword(oldPassword, password, confirmPassword);
        return new ModelAndView("redirect:/profile");
    }
}