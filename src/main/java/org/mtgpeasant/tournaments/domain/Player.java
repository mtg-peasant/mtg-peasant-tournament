package org.mtgpeasant.tournaments.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@ApiModel(description = "A tournament player")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "players")
public class Player {
    // TODO: est-ce une bonne idée d'utiliser le signin/name comme clé primaire?
    @ApiModelProperty(value = "The player nickname", example = "deck2beat")
    @Id
    private String name;

    @ApiModelProperty("The user account linked to this player (optional)")
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ApiModelProperty("The player's creation date")
    @Column
    @Builder.Default
    private java.time.Instant created = Instant.now();

    @ApiModelProperty("The user account that created this player")
    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonIgnore
    private User createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Player{'" +
                name +
                "'}";
    }
}
