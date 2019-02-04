package org.mtgpeasant.tournaments.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Map;

@Slf4j
public class AuthenticationHelper {
    @Value
    @AllArgsConstructor
    @Builder
    public static class AuthentInfo {
        public enum Type {INTERACTIVE, OAUTH}

        private final Type type;
        private final String email;
        private final String fullname;
        private final String firstname;
        private final String lastname;
        private final String picture;
    }

    //    {
//        "sub": "111599535581185938842",
//            "name": "Pierre SMEYERS",
//            "given_name": "Pierre",
//            "family_name": "SMEYERS",
//            "profile": "https://plus.google.com/111599535581185938842",
//            "picture": "https://lh3.googleusercontent.com/-2H91jVcbQKg/AAAAAAAAAAI/AAAAAAAAm34/HQIqRkQW90A/photo.jpg",
//            "email": "pierre.smeyers@gmail.com",
//            "email_verified": true,
//            "gender": "male",
//            "locale": "fr"
//    }
    public static AuthentInfo extractInfo(Authentication authentication) {
        log.debug("Extract info from {}", authentication);
        if (authentication instanceof OAuth2Authentication) {
            Authentication userAuthentication = ((OAuth2Authentication) authentication).getUserAuthentication();
            if (userAuthentication instanceof UsernamePasswordAuthenticationToken) {
                // that can be either an interactive SSO authent, or an OAuth token authent
                // in first case, details are in the userAuthentication details
                if (userAuthentication.getDetails() != null) {
                    Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();
                    return AuthentInfo.builder()
                            .type(AuthentInfo.Type.INTERACTIVE)
                            .email(details.get("email"))
                            .fullname(details.get("name"))
                            .firstname(details.get("given_name"))
                            .lastname(details.get("family_name"))
                            .picture(details.get("picture"))
                            .build();
                }
                return AuthentInfo.builder()
                        .type(AuthentInfo.Type.OAUTH)
                        // email is the principal name
                        .email(authentication.getName())
                        .build();
            }
        }
        return null;
    }
}
