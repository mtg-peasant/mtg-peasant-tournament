package org.mtgpeasant.tournaments.web.ui;

import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.domain.exceptions.NotFoundException;
import org.mtgpeasant.tournaments.respository.UserRepository;
import org.mtgpeasant.tournaments.service.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/view")
    public ModelAndView view(Authentication authentication) {
        String email = AuthenticationHelper.getUserEmail(authentication);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Account with email '"+email+"' not found"));
        ModelAndView mv = new ModelAndView("account/view");
        mv.addObject("user", user);
        return mv;
    }

    @GetMapping("/delete")
    public ModelAndView showDeleteView() {
        return new ModelAndView("account/delete");
    }

    @PostMapping("/delete")
    public ModelAndView doDelete(HttpSession session, Authentication authentication) {
        String email = AuthenticationHelper.getUserEmail(authentication);
        log.info("Delete account: {}", email);
        // TODO: cannot delete ! we must invalidate it
        userRepository.deleteByEmail(email); // will cascade

        // programmatic logout
        SecurityContextHolder.clearContext();
        session.invalidate();

        // then redirect to home
        return new ModelAndView("redirect:/");
    }

}
