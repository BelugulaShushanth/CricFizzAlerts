package com.cricFizzAlerts.bean.matchScoreCard;


import com.cricFizzAlerts.bean.matchScoreCard.batsmen.BatTeamDetails;
import com.cricFizzAlerts.bean.matchScoreCard.bowlers.BowlTeamDetails;
import com.cricFizzAlerts.bean.matchScoreCard.partnership.PartnershipsData;
import com.cricFizzAlerts.bean.matchScoreCard.wickets.WicketsData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
@NoArgsConstructor
public class ScoreCard implements Comparator {
    private int matchId;
    private int inningsId;
    private Object timeScore;
    private BatTeamDetails batTeamDetails;
    private BowlTeamDetails bowlTeamDetails;
    private ScoreDetails scoreDetails;
    private ExtrasData extrasData;
    private PpData ppData;
    private WicketsData wicketsData;
    private PartnershipsData partnershipsData;

    @Override
    public int compare(Object o1, Object o2) {
        ScoreCard sc1 = (ScoreCard) o1;
        ScoreCard sc2 = (ScoreCard) o2;
        if(sc1.getInningsId() > sc2.getInningsId()){
            return -1;
        }
        else if(sc1.getInningsId() < sc2.getInningsId()){
            return 1;
        }
        return 0;
    }
}
