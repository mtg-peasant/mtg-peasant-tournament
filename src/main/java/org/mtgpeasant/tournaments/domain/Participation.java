package org.mtgpeasant.tournaments.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@ApiModel(description = "A tournament player's participation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "participations")
public class Participation implements Comparable<Participation> {
    @Embeddable
    @Builder
    @Data
    @Setter(AccessLevel.NONE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class ParticipationId implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty("Participation's tournament")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "tournament_id")
        private Tournament tournament;

        @ApiModelProperty("Participation's player")
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "player_id")
        private Player player;
    }
    /**
     * composite identifier
     */
    @EmbeddedId
    @JsonUnwrapped
    ParticipationId id;

    @ApiModelProperty("Player match points")
    @Column
    private int matchPoints;

    @ApiModelProperty("Player match wins")
    @Column
    private int wins;

    @ApiModelProperty("Player match losses")
    @Column
    private int losses;

    @ApiModelProperty("Player match draws")
    @Column
    private int draws;

    @ApiModelProperty("Player match win percentage")
    @Column
    private int matchWinPercentage;

    @ApiModelProperty("Player game win percentage")
    @Column
    private int gameWinPercentage;

    @ApiModelProperty("Opponents match win percentage")
    @Column
    private int opponentsMatchWinPercentage;

    @ApiModelProperty("Opponents game win percentage")
    @Column
    private int opponentsGameWinPercentage;

    /**
     * Resets the score
     */
    public void reset() {
        matchPoints = 0;
        wins = 0;
        losses = 0;
        draws = 0;
        matchWinPercentage = 0;
        gameWinPercentage = 0;
        opponentsMatchWinPercentage = 0;
        opponentsGameWinPercentage = 0;
    }

    /**
     * Adds one win
     */
    public void incrWins() {
        wins++;
        matchPoints += 3;
    }

    /**
     * Adds one draw
     */
    public void incrDraws() {
        draws++;
        matchPoints += 1;
    }

    /**
     * Adds one loss
     */
    public void incrLosses() {
        losses++;
    }

    @Override
    public int compareTo(Participation other) {
        // 1: match points
        // 2: opponents’match-win percentages
        // 3: game-win percentages
        // 4: opponents’game-win percentages
        if (getMatchPoints() != other.getMatchPoints()) {
            return getMatchPoints() - other.getMatchPoints();
        }
        if (getOpponentsMatchWinPercentage() != other.getOpponentsMatchWinPercentage()) {
            return getOpponentsMatchWinPercentage() - other.getOpponentsMatchWinPercentage();
        }
        if (getGameWinPercentage() != other.getGameWinPercentage()) {
            return getGameWinPercentage() - other.getGameWinPercentage();
        }
        if (getOpponentsGameWinPercentage() != other.getOpponentsGameWinPercentage()) {
            return getOpponentsGameWinPercentage() - other.getOpponentsGameWinPercentage();
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participation participation = (Participation) o;
        return Objects.equals(id.getPlayer(), participation.id.getPlayer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.getPlayer());
    }

    @Override
    public String toString() {
        return "Participation{" +
                "'" + id.getPlayer().getName() + "'" +
                ": " + wins + "-" + losses + "-" + draws +
                ", match points=" + matchPoints +
                ", MW%=" + matchWinPercentage + "%" +
                ", GW%=" + gameWinPercentage + "%" +
                ", OMW%=" + opponentsMatchWinPercentage + "%" +
                ", OGW%=" + opponentsGameWinPercentage + "%" +
                '}';
    }
}
