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
    @ApiModelProperty("The deck's name")
    @NotNull
    private String name;

    @ApiModelProperty("The deck's archetype")
    @NotNull
    private String archetype;

    @ApiModelProperty("The deck's cards")
    private String cards;
}
