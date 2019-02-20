package org.mtgpeasant.tournaments.web.ui;

import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.domain.exceptions.NotFoundException;
import org.mtgpeasant.tournaments.respository.UserRepository;
import org.mtgpeasant.tournaments.service.AuthenticationHelper;
import org.mtgpeasant.tournaments.service.UserService;
import org.mtgpeasant.tournaments.web.ui.model.SignUpForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping
@Slf4j
public class AccountController {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/users/me")
    public ModelAndView viewMyProfile(Authentication authentication) {
        String email = AuthenticationHelper.userEmail(authentication);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Account with email '" + email + "' not found"));
        ModelAndView mv = new ModelAndView("account/view");
        mv.addObject("user", user);
        return mv;
    }

    @GetMapping("/users/{id}")
    public ModelAndView viewUserProfile(@PathVariable("id") String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Account with ID '" + id + "' not found"));
        ModelAndView mv = new ModelAndView("account/view");
        mv.addObject("user", user);
        return mv;
    }

    @GetMapping("/users/me/delete")
    public ModelAndView deleteView() {
        return new ModelAndView("account/delete");
    }

    @PostMapping("/users/me/delete")
    public ModelAndView doDelete(HttpSession session, Authentication authentication) {
        String email = AuthenticationHelper.userEmail(authentication);
        log.info("Delete account: {}", email);
        // TODO: cannot delete ! we must invalidate it
        userRepository.deleteByEmail(email); // will cascade

        // programmatic logout
        SecurityContextHolder.clearContext();
        session.invalidate();

        // then redirect to home
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/signup")
    public ModelAndView signupView() {
        ModelAndView mv = new ModelAndView("account/signup");
        mv.addObject("form", SignUpForm.builder().build());
        return mv;
    }

    @PostMapping("/signup")
    public ModelAndView doSignup(@ModelAttribute("form") @Valid SignUpForm form, BindingResult formBinding, HttpServletRequest request,
                                 HttpServletResponse response) {
        if (formBinding.hasErrors()) {
            // comes back to the view
            return new ModelAndView("account/signup", formBinding.getModel());
        }
        try {
            User user = userService.create(User.builder()
                    .email(form.getEmail())
                    .fullName(form.getFullName())
                    .pseudo(form.getPseudo())
                    .password(form.getPassword())
                    .build());

            // success: automatically signin, and follow target uri
            doSignIn(user);
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            String targetUrl = "/";
            if (savedRequest != null) {
                targetUrl = savedRequest.getRedirectUrl();
            }
            return new ModelAndView("redirect:" + targetUrl);
        } catch (UserService.PseudoAlreadyInUseException e) {
            formBinding.rejectValue("pseudo", "user.pseudo", "pseudo already in use");
        } catch (UserService.EmailAlreadyInUseException e) {
            formBinding.rejectValue("email", "user.email", "email already in use");
        }
        // there are errors
        return new ModelAndView("account/signup", formBinding.getModel());
    }

    private void doSignIn(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null, null));
    }

}
