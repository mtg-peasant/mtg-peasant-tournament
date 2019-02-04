package org.mtgpeasant.tournaments.domain;

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

    @Column(name="pseudo", nullable = false, unique = true)
    private String pseudo;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="full_name", nullable = false)
    private String fullName;

    @Column(name="picture")
    private String picture;

    @Column(name="created", nullable = false)
    private Instant created = Instant.now();

    @Column(name="last_connected", nullable = false)
    private Instant lastConnected = created;
}
