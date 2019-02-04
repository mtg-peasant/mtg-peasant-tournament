package org.mtgpeasant.tournaments.domain;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

//@ApiModel(description = "A tournament participant (a player + a deck)")
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Data
//@Entity(name = "participants")
public class Participant {
//    @Embeddable
//    @Data
//    @Setter(AccessLevel.NONE)
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    @AllArgsConstructor
//    public static class Id implements Serializable {
//
//        private static final long serialVersionUID = 1L;
//
//        @ManyToOne(fetch = FetchType.LAZY, optional = false)
//        @JoinColumn(name = "tournament_id")
//        private Tournament tournament;
//
//        @ManyToOne(fetch = FetchType.EAGER, optional = false)
//        @JoinColumn(name = "player_id")
//        private Player player;
//    }
//
//    /**
//     * composite identifier
//     */
//    @EmbeddedId
//    Id id;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "deck_id")
//    private Deck deck;
}
