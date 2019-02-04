package org.mtgpeasant.tournaments.web.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel(description = "A deck")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DeckInfo {
    @ApiModelProperty(value = "The deck's name", example = "Cool burn v2.1")
    @NotNull
    private String name;

    @ApiModelProperty(value = "The deck's archetype", example = "burn")
    @NotNull
    private String archetype;

    @ApiModelProperty("The deck's cards")
    private String cards;
}
