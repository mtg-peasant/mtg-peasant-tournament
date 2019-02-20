package org.mtgpeasant.tournaments.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.mtgpeasant.tournaments.domain.exceptions.BadStateException;
import org.mtgpeasant.tournaments.domain.exceptions.NotFoundException;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApiModel(description = "A tournament")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity(name = "tournaments")
public class Tournament {
    @ApiModelProperty("Tournament's id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("The tournament's owner")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ApiModelProperty(value = "The tournament's format", example = "peasant")
    @Column
    private MtgFormat format;

    @ApiModelProperty(value = "The tournament's name", example = "Peasant Tournament in Toulouse")
    @Column(nullable = false)
    private String name;

    @ApiModelProperty(value = "The tournament's location", example = "BlastoDice")
    @Column(nullable = false)
    private String location;

    @ApiModelProperty(value = "The tournament's location address", example = "52 Avenue Honoré Serres, 31000 Toulouse")
    @Column
    private String locationAddress;

    @ApiModelProperty("The tournament's planned date and time")
    @Column(nullable = false)
    private java.time.LocalDateTime dateTime;

    @ApiModelProperty("The tournament's creation date")
    @Column
    @Builder.Default
    private java.time.Instant created = Instant.now();

    @ApiModelProperty("The tournament's start date")
    @Column
    private java.time.Instant started;

    @ApiModelProperty("The tournament's finish date")
    @Column
    private java.time.Instant finished;

    public enum Type {
        Direct,
        Swiss,
        SwissAndDirect;

    }
    @ApiModelProperty("The tournament's type")
    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Type type = Type.Swiss;

    public enum State {
        Pending,
        InProgress,
        Finished;

    }
    @ApiModelProperty("The tournament's state")
    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private State state = State.Pending;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id.tournament")
    @OrderColumn(name = "rank")
    @Builder.Default
    private List<Round> rounds = new ArrayList<>();

    @ApiModelProperty("The tournament participations")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id.tournament")
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();

    public void addPlayer(Player player) {
        if(state != State.Pending) {
            throw new BadStateException("Adding a player to a tournament is only allowed on a 'pending' tournament.");
        }
        Optional<Participation> existing = participations.stream().filter(participation -> participation.getId().getPlayer().equals(player)).findFirst();
        if (!existing.isPresent()) {
            participations.add(Participation.builder().id(Participation.ParticipationId.builder().tournament(this).player(player).build()).build());
        }
    }

    public void removePlayer(Player player) {
        if(state != State.Pending) {
            throw new BadStateException("Removing a player from a tournament is only allowed on a 'pending' tournament.");
        }
        Optional<Participation> existing = participations.stream().filter(participation -> participation.getId().getPlayer().equals(player)).findFirst();
        if (existing.isPresent()) {
            participations.remove(existing.get());
        }
    }

    /**
     * Returns sorted participations (from highest to lowest)
     */
    public List<Participation> getParticipations() {
        Collections.sort(participations, Collections.reverseOrder());
        return participations;
    }

    /**
     * Returns the participation of given player
     */
    public Participation getParticipation(Player player) {
        return getParticipation(player.getName());
    }

    /**
     * Returns the participation of given player name
     */
    public Participation getParticipation(String playerName) {
        return participations.stream()
                .filter(participation -> playerName.equals(participation.getId().getPlayer().getName()))
                .findFirst().orElseThrow(() -> new NotFoundException("No participation found for player '"+playerName+"'"));
    }

    /**
     * Returns the player by name
     */
    public Player getPlayer(String playerName) {
        return participations.stream()
                .map(participation -> participation.getId().getPlayer())
                .filter(player -> playerName.equals(player.getName()))
                .findFirst().orElseThrow(() -> new NotFoundException("No player '"+playerName+"'s"));
    }
}
