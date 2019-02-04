package org.mtgpeasant.tournaments.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.mtgpeasant.tournaments.domain.exceptions.NotFoundException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "A tournament round")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "rounds")
public class Round {
    @Embeddable
    @Builder
    @Data
    @Setter(AccessLevel.NONE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class RoundId implements Serializable {

        private static final long serialVersionUID = 1L;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "tournament_id")
        private Tournament tournament;

        @ApiModelProperty("The round's rank")
        @Column(name="rank")
        private Integer rank;

    }
    /**
     * composite identifier
     */
    @EmbeddedId
    @JsonUnwrapped
    RoundId id;

    public enum State {
        Pending,
        InProgress,
        Finished;
    }

    @ApiModelProperty("The round state")
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private State state = State.Pending;

    public enum Type {
        Direct,
        Swiss
    }

    @ApiModelProperty("The round's type")
    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Type type = Type.Swiss;

    @ApiModelProperty("The round's start date")
    @Column
    private java.time.Instant started;

    @ApiModelProperty("The round's finish date")
    @Column
    private java.time.Instant finished;

    @ApiModelProperty("The round's matches")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id.round")
    @Builder.Default
    @OrderColumn(name = "rank")
    private List<Match> matches = new ArrayList<>();

    /**
     * Finds the match involving the given player
     */
    public Match findMatch(Player player) {
        return findMatch(player.getName());
    }

    /**
     * Finds the match involving the given player name
     */
    public Match findMatch(String playerName) {
        return matches.stream()
                .filter(match -> (match.getPlayerOne() != null && playerName.equals(match.getPlayerOne().getName()))
                    || (match.getPlayerTwo() != null && playerName.equals(match.getPlayerTwo().getName())))
                .findFirst().orElseThrow(() -> new NotFoundException("No match found for player '"+playerName+"'"));
    }

    /**
     * Finds the opponent of the given player in this round
     */
    public Player findOpponent(Player player) {
        return findMatch(player).getOpponent(player);
    }

    @Override
    public String toString() {
        return "Round{" +
                "round #" + id.getRank() +
                ", state=" + state +
                ", started=" + started +
                ", finished=" + finished +
                ", matches=" + matches +
                '}';
    }
}
