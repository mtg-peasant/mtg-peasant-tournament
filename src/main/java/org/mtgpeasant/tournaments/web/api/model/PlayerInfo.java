package org.mtgpeasant.tournaments.web.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel(description = "Player information")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PlayerInfo {
    @ApiModelProperty(value = "The players's name", example = "deck2beat")
    @NotNull
    private String name;
}
