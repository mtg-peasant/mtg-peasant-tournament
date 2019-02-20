package org.mtgpeasant.tournaments.service;

import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

/**
 * Creates/updates users database after a successful SSO authentication
 */
@Service
@Slf4j
public class SsoAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("onAuthenticationSuccess: {}", authentication);
        Authentication userAuthentication = ((OAuth2Authentication) authentication).getUserAuthentication();
        Map<String, Object> details = (Map<String, Object>) userAuthentication.getDetails();
        String email = email(details);

        // update user profile
        // TODO: manage case where email is null
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = userRepository.save(User.builder()
                    .email(email)
                    .created(Instant.now())
                    .fullName(fullName(details))
                    .picture(pictureUrl(details))
                    .created(Instant.now())
//                    .pseudo()
                    .build());
            log.info("new user has connected: {}", user);
        } else {
            // TODO: update info ?
            log.info("existing user has connected: {}", user);
        }

        // replace Authentication principal with ExtendedUser
        ExtendedUsersDetailsService.ExtendedUser extendedUser = ExtendedUsersDetailsService.ExtendedUser.from(user);
        SecurityContextHolder.getContext().setAuthentication(new OAuth2Authentication(
                ((OAuth2Authentication) authentication).getOAuth2Request(),
                new UsernamePasswordAuthenticationToken(extendedUser, null, extendedUser.getAuthorities())));

        // then proceed with authentication
        super.onAuthenticationSuccess(request, response, authentication);
    }

//    Facebook:
//    {
//        "email":"pierre.smeyers\u0040gmail.com",
//        "name":"Pierre Smeyers",
//        "first_name":"Pierre",
//        "last_name":"Smeyers",
//        "picture":{"data":{"height":50,"is_silhouette":false,"url":"https:\/\/platform-lookaside.fbsbx.com\/platform\/profilepic\/?asid=10213458593560950&height=50&width=50&ext=1552469067&hash=AeS3jOucWRcSBoF6","width":50}},
//        "short_name":"Pierre",
//        "id":"10213458593560950"
//    }

//    Google:
//    {
//        "sub": "111599535581185938842",
//        "name": "Pierre SMEYERS",
//        "given_name": "Pierre",
//        "family_name": "SMEYERS",
//        "profile": "https://plus.google.com/111599535581185938842",
//        "picture": "https://lh3.googleusercontent.com/-2H91jVcbQKg/AAAAAAAAAAI/AAAAAAAAm34/HQIqRkQW90A/photo.jpg",
//        "email": "pierre.smeyers@gmail.com",
//        "email_verified": true,
//        "gender": "male",
//        "locale": "fr"
//    }

    String pictureUrl(Map<String, Object> details) {
        Object picture = details.get("picture");
        if(picture instanceof String) {
            // Google
            return (String) picture;
        }
        if(picture instanceof Map) {
            // facebook
            return ((Map<String, String>)((Map<String, Object>) picture).get("data")).get("url");
        }
        return null;
    }

    String lastName(Map<String, Object> details) {
        return (String) details.getOrDefault("family_name", details.get("last_name"));
    }

    String fullName(Map<String, Object> details) {
        return (String) details.get("name");
    }

    String email(Map<String, Object> details) {
        return (String) details.get("email");
    }

    String firstName(Map<String, Object> details) {
        return (String) details.getOrDefault("given_name", details.get("first_name"));
    }

}
