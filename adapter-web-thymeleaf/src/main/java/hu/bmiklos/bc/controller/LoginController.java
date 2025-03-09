package hu.bmiklos.bc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.LoginRequest;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @GetMapping
    public ModelAndView loginForm() {
        return new ModelAndView("login");
    }

    @PostMapping
    public ModelAndView login(@ModelAttribute LoginRequest loginRequest) {
        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new ModelAndView("redirect:/");
        } else {
            return new ModelAndView("login");
        }
    }
}
