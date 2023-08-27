package hu.bmiklos.bc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.service.UserService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class UserController {
    private static final String PROFILE_VIEW_NAME = "profile";

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public ModelAndView registrationForm() {
        return new ModelAndView("registration");
    }

    @PostMapping("/registration")
    public ModelAndView registration(@RequestParam("external_id") String externalId, @RequestParam String name, @RequestParam String email, @RequestParam("confirm_email") String confirmEmail, @RequestParam String password, @RequestParam("confirm_password") String confirmPassword) {
        userService.registerUser(externalId, name, email, confirmEmail, password, confirmPassword);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/profile")
    public ModelAndView profile() {
        // TODO implement
        return new ModelAndView(PROFILE_VIEW_NAME);
    }

    @PutMapping("/profile/password")
    public ModelAndView updatePassword(@RequestBody UpdatePasswordRequest profileRequest) {
        // TODO implement
        return new ModelAndView(PROFILE_VIEW_NAME);
    }

    @PostMapping(value="/profile/email")
    public ModelAndView addEmail(@RequestBody AddEmailRequest emailRequest) {
        // TODO implement
        return new ModelAndView(PROFILE_VIEW_NAME);
    }

    @DeleteMapping(value="/profile/email/{email}")
    public ModelAndView deleteEmail(@PathVariable String email) {
        // TODO implement
        return new ModelAndView(PROFILE_VIEW_NAME);
    }
}
