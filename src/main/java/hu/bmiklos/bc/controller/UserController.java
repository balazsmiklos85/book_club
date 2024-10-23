package hu.bmiklos.bc.controller;

import hu.bmiklos.bc.controller.dto.PasswordResetRequest;
import hu.bmiklos.bc.controller.mapper.UserMapper;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.UserService;
import hu.bmiklos.bc.service.dto.UserDto;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
  private final ActiveUserService activeUserService;
  private final UserService userService;

  public UserController(ActiveUserService activeUserService, UserService userService) {
    this.activeUserService = activeUserService;
    this.userService = userService;
  }

  @PostMapping("/user/password/new")
  public ModelAndView passwordReset(@ModelAttribute PasswordResetRequest passwordResetRequest) {
    userService.changePassword(passwordResetRequest.getUser_id());
    return new ModelAndView("redirect:/users");
  }

  @GetMapping("/registration")
  public ModelAndView registrationForm() {
    return new ModelAndView("registration");
  }

  @PostMapping("/registration")
  // TODO Too many parameters. Create a DTO class for this.
  // I think this method was declared like this, because the DTO did not pick up the parameters with
  // snake case names. Try to not use snake case names in the template.
  public ModelAndView registration(
      @RequestParam("external_id") String externalId,
      @RequestParam String name,
      @RequestParam String email,
      @RequestParam("confirm_email") String confirmEmail,
      @RequestParam String password,
      @RequestParam("confirm_password") String confirmPassword) {
    userService.registerUser(externalId, name, email, confirmEmail, password, confirmPassword);
    return new ModelAndView("redirect:/login");
  }

  @GetMapping("/users")
  public ModelAndView users() {
    List<User> users = userService.findAll();
    List<UserDto> usersResponse = UserMapper.mapToUserDtos(users);
    ModelAndView modelAndView = new ModelAndView("user/list");
    modelAndView.addObject("users", usersResponse);
    modelAndView.addObject("isAdmin", activeUserService.isAdmin());
    return modelAndView;
  }
}
