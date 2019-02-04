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
    @ApiModelProperty("The tournament's name")
    @NotNull
    private String name;

    @ApiModelProperty("The tournament's type")
    @NotNull
    private Tournament.Type type;

    @ApiModelProperty("The tournament's format")
    @NotNull
    private MtgFormat format;

    @ApiModelProperty("The tournament's location")
    @NotNull
    private String location;

    @ApiModelProperty("The tournament's location address")
    private String locationAddress;

    @ApiModelProperty("The tournament's planned date and time")
    @NotNull
    private java.time.LocalDateTime dateTime;
}
