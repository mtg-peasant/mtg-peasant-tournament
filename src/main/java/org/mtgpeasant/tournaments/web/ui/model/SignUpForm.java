package org.mtgpeasant.tournaments.web.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Data
@ToString(exclude = "password")
public class SignUpForm {
    @NotEmpty
    @Size(min = 4, message = "must be at least 4 characters")
    private String pseudo;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String fullName;

//    private String picture;

    @Size(min = 8, message = "must be at least 8 characters")
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}", message = "8 characters with at least 1 digit, 1 lowercase and 1 uppercase letters")
    private String password;
}
