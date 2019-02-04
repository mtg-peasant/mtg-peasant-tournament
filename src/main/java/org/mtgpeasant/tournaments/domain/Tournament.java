package org.mtgpeasant.tournaments.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.mtgpeasant.tournaments.domain.exceptions.NotFoundException;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    @ApiModelProperty("The tournament's format")
    @Column
    private MtgFormat format;

    @ApiModelProperty("The tournament's name")
    @Column(nullable = false)
    private String name;

    @ApiModelProperty("The tournament's location")
    @Column(nullable = false)
    private String location;

    @ApiModelProperty("The tournament's location address")
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


//    @ApiModelProperty("The tournament participants")
//    @Singular
////    @ManyToMany(fetch = FetchType.EAGER)
////    @JoinTable(name = "tournament_players",
////            joinColumns = { @JoinColumn(name = "tournament_id", nullable = false) },
////            inverseJoinColumns = { @JoinColumn(name = "player_id", nullable = false) })
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tournament")
//    private Set<Participant> participants;
    @ApiModelProperty("The tournament players")
    @Singular
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tournament_players")
    private Set<Player> players;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id.tournament")
    @OrderColumn(name = "rank")
    @Builder.Default
    private List<Round> rounds = new ArrayList<>();


//    @ApiModelProperty("The tournament scores")
//    @Singular
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id.tournament")
//    private Set<Score> scores;

//    @ApiModelProperty("The tournament scores")
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id.tournament")
//    @MapKeyJoinColumn(name="id.player")
//    //@MapKey(name = "id.player")
//    @Builder.Default
//    private Map<String, Score> scores = new HashMap<>();
    @ApiModelProperty("The tournament scores")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id.tournament")
    @Builder.Default
    private List<Score> scores = new ArrayList<>();

    /**
     * Returns sorted scores (from highest to lowest)
     */
    public List<Score> getScores() {
        Collections.sort(scores, Collections.reverseOrder());
        return scores;
    }

    /**
     * Returns the score of given player
     */
    public Score getScore(Player player) {
        return getScore(player.getName());
    }

    /**
     * Returns the score of given player name
     */
    public Score getScore(String playerName) {
        return scores.stream()
                .filter(score -> playerName.equals(score.getId().getPlayer().getName()))
                .findFirst().orElseThrow(() -> new NotFoundException("No score found for player '"+playerName+"'"));
    }

    /**
     * Returns the player by name
     */
    public Player getPlayer(String playerName) {
        return players.stream()
                .filter(player -> playerName.equals(player.getName()))
                .findFirst().orElseThrow(() -> new NotFoundException("No player '"+playerName+"'s"));
    }
}
