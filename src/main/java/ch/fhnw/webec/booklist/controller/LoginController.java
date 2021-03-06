package ch.fhnw.webec.booklist.controller;

import ch.fhnw.webec.booklist.model.User;
import ch.fhnw.webec.booklist.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

@Controller
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login(Authentication authentication, @RequestParam("error") Optional<String> error, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        } else {
            model.addAttribute("hasLoginError", error.isPresent());

            return "login/login";
        }
    }

    @RequestMapping(path = "/registration", method = RequestMethod.GET)
    public String register() {
        return "login/registration";
    }

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public String register(@Valid User user, BindingResult bindingResult, Model model) {
        if (this.userService.usernameAlreadyExists(user.getUsername())) {
            bindingResult.addError(new FieldError("user", "username", "Username already exists"));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);

            return "login/registration";
        } else {
            this.userService.addUser(user.getUsername(), user.getPassword(), Set.of("ROLE_USER"));

            return "redirect:/login";
        }
    }
}
