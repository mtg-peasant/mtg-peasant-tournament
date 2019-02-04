package org.mtgpeasant.tournaments.web.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "A match result")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MatchResult {
    @ApiModelProperty("Player one games win count")
    private int playerOneGamesWin;

    @ApiModelProperty("Player two games win count")
    private int playerTwoGamesWin;
}
