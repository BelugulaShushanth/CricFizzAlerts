package com.cricFizzAlerts.bean.matchScoreCard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatchTeamInfo{
    private int battingTeamId;
    private String battingTeamShortName;
    private int bowlingTeamId;
    private String bowlingTeamShortName;
}
