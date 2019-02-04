package org.mtgpeasant.tournaments.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@ApiModel(description = "A deck")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "decks")
public class Deck {
    @ApiModelProperty("Deck id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("The deck's name")
    @Column(nullable = false)
    private String name;

    // TODO: archetypes should be in a table ?
    @ApiModelProperty("The deck's archetype")
    @Column(nullable = false)
    private String archetype;

    @ApiModelProperty("The deck's player")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @ApiModelProperty("The deck's creation date")
    @Column
    @Builder.Default
    private java.time.Instant created = Instant.now();

    @ApiModelProperty("The deck's format")
    @Column
    private MtgFormat format;

    // TODO
    @ApiModelProperty("The deck's cards")
    @Column
    private String cards;
}
