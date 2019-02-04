package org.mtgpeasant.tournaments.web.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtgpeasant.tournaments.domain.MtgFormat;
import org.mtgpeasant.tournaments.domain.Tournament;

import javax.validation.constraints.NotNull;

@ApiModel(description = "Tournament information")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TournamentInfo {
    @ApiModelProperty(value = "The tournament's name", example = "Peasant Tournament in Toulouse")
    @NotNull
    private String name;

    @ApiModelProperty("The tournament's type")
    @NotNull
    private Tournament.Type type;

    @ApiModelProperty(value = "The tournament's format", example = "peasant")
    @NotNull
    private MtgFormat format;

    @ApiModelProperty(value = "The tournament's location", example = "BlastoDice")
    @NotNull
    private String location;

    @ApiModelProperty(value = "The tournament's location address", example = "52 Avenue Honoré Serres, 31000 Toulouse")
    private String locationAddress;

    @ApiModelProperty("The tournament's planned date and time")
    @NotNull
    private java.time.LocalDateTime dateTime;
}
