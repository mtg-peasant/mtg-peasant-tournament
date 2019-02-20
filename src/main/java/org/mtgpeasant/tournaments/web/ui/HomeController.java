package org.mtgpeasant.tournaments.web.ui;

import org.mtgpeasant.tournaments.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public ModelAndView home(Authentication authentication) {
//        String email = AuthenticationHelper.userEmail(authentication);
//        User user = email == null ? null : userRepository.findByEmail(email).orElse(null);
        // show guest page
        return new ModelAndView("index");
    }

    @GetMapping("/about")
    public ModelAndView about() {
        return new ModelAndView("about");
    }

    @GetMapping("/privacypolicy")
    public ModelAndView privacypolicy() {
        return new ModelAndView("privacypolicy");
    }

    @GetMapping("/signin")
    public ModelAndView signin() {
        return new ModelAndView("signin");
    }
}
