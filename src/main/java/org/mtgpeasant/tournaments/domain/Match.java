package org.mtgpeasant.tournaments.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel(description = "A tournament match")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "matches")
public class Match {
    @Embeddable
    @Builder
    @Data
    @Setter(AccessLevel.NONE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class MatchId implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty("The tournament round this match is part of")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumns({
                @JoinColumn(name = "tournament_id", referencedColumnName = "tournament_id"),
                @JoinColumn(name = "round_rank", referencedColumnName = "rank")
        })
        private Round round;

        @ApiModelProperty("The match's rank")
        @Column(name="rank")
        private Integer rank;

    }
    /**
     * composite identifier
     */
    @EmbeddedId
    @JsonUnwrapped
    MatchId id;

    @ApiModelProperty("Match player one")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_one_name")
    private Player playerOne;

    @ApiModelProperty("Match player two")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_two_name")
    private Player playerTwo;


    public enum State {
        Pending,
        InProgress,
        Finished
    }

    @ApiModelProperty("The match state")
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private State state = State.Pending;

    @ApiModelProperty("Player one games win count")
    @Column
    private Integer playerOneGamesWin;

    @ApiModelProperty("Player two games win count")
    @Column
    private Integer playerTwoGamesWin;

    /**
     * Determines whether this match was won by player one
     */
    @JsonIgnore
    public boolean isPlayerOneWin() {
        return playerOneGamesWin > playerTwoGamesWin;
    }

    /**
     * Determines whether this match was won by player two
     */
    @JsonIgnore
    public boolean isPlayerTwoWin() {
        return playerTwoGamesWin > playerOneGamesWin;
    }

    /**
     * Determines whether this match is a draw
     */
    @JsonIgnore
    public boolean isDraw() {
        return playerOneGamesWin == playerTwoGamesWin;
    }

    /**
     * Returns the player's games win
     */
    public Integer getGamesWin(Player player) {
        if (player.equals(getPlayerOne())) {
            return playerOneGamesWin;
        }
        if (player.equals(getPlayerTwo())) {
            return playerTwoGamesWin;
        }
        return null;
    }

    /**
     * Returns the player's opponent
     */
    public Player getOpponent(Player player) {
        if (player.equals(getPlayerOne())) {
            return getPlayerTwo();
        }
        if (player.equals(getPlayerTwo())) {
            return getPlayerOne();
        }
        return null;
    }

    /**
     * Determines whether this match is a bye
     */
    @JsonIgnore
    public boolean isNotBye() {
        return getPlayerOne() != null && getPlayerTwo() != null;
    }

    @Override
    public String toString() {
        return "Match{" +
                "(" + state + ")"
                + " " + (getPlayerOne() == null ? "bye" : "'" + getPlayerOne() + "'")
                + (state == State.Finished ? " " + playerOneGamesWin : "")
                + " vs "
                + (state == State.Finished ? " " + playerTwoGamesWin : "")
                + (getPlayerTwo() == null ? "bye" : "'" + getPlayerTwo() + "'")
                + '}';
    }
}
