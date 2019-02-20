package org.mtgpeasant.tournaments.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = RandomStringIdGenerator.NAME)
    @GenericGenerator(strategy = "org.mtgpeasant.tournaments.domain.RandomStringIdGenerator", name = RandomStringIdGenerator.NAME)
    private String id;

    @ApiModelProperty(value = "User's pseudo", example = "m@ster")
    @Column(name="pseudo", unique = true)
    private String pseudo;

    @JsonIgnore
    @Column(name="email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name="password")
    private String password;

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
