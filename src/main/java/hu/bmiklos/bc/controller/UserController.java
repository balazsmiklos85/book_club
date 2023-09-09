package hu.bmiklos.bc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.service.UserService;

@Controller
public class UserController {
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
}
