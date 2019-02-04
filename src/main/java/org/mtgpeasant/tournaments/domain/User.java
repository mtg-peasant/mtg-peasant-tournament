package org.mtgpeasant.tournaments.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

/**
 * Informations supplémentaires:
 * - avatar icon
 */
@ApiModel(description = "A user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "users")
@EqualsAndHashCode(of = "id")
@ToString(exclude = "password")
public class User {
    @ApiModelProperty("User's id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO: generated 10 chars hex string
    private Long id;

    @ApiModelProperty(value = "User's pseudo", example = "organizer")
    @Column(name="pseudo", nullable = false, unique = true)
    private String pseudo;

    @JsonIgnore
    @Column(name="email", nullable = false, unique = true)
    private String email;

    @ApiModelProperty(value = "User's full name", example = "Jean Dupont")
    @Column(name="full_name", nullable = false)
    private String fullName;

    @ApiModelProperty(value = "User's picture url", example = "https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50")
    @Column(name="picture")
    private String picture;

    @ApiModelProperty("User account creation date")
    @JsonIgnore
    @Column(name="created", nullable = false)
    private Instant created = Instant.now();

    @Column(name="last_connected", nullable = false)
    @JsonIgnore
    private Instant lastConnected = created;
}
