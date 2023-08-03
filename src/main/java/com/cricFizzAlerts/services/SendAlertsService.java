package com.cricFizzAlerts.services;

import com.cricFizzAlerts.bean.alert.AlertDetails;
import com.cricFizzAlerts.bean.mail.MailBean;
import com.cricFizzAlerts.bean.matchScoreCard.MatchScoreCard;
import com.cricFizzAlerts.client.MailClient;
import com.cricFizzAlerts.repository.AlertsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
public class SendAlertsService {

    private Logger logger = LoggerFactory.getLogger(SendAlertsService.class);

    @Value("${mail.subject}")
    private String finalSubject;

    @Value("${mail.score.body}")
    private String finalBody;

    @Value("${style.grey}")
    private String styleGrey;

    @Autowired
    private CricbuzzService cricbuzzService;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private AlertsRepository alertsRepository;

    @Async
    public void scheduleMailAlerts(AlertDetails alertDetails) {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(alertsRepository.findById(alertDetails.getAlertId()).get().getIsActive()) {
                    if (alertDetails.getAlertType().equalsIgnoreCase("score")) {

                        if(alertDetails.getMatchType().equalsIgnoreCase("live") ) {
                            sendMailAlert(alertDetails);
                        }
                    }
                }
                else{
                    cancel();
                }
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0,alertDetails.getTimePeriod() * 60000);

    }

    private void sendMailAlert(AlertDetails alertDetails) {

        MatchScoreCard matchesScoreCard = cricbuzzService.getMatchesScoreCard(alertDetails.getMatchId());
        StringBuffer mailId = new StringBuffer(alertDetails.getMailId());
        String subject = finalSubject;

        MailBean mailBean = new MailBean();
        mailBean.setToMailId(mailId.toString());
        mailBean.setSubject(subject.replace("alertType", alertDetails.getAlertType()));
        mailBean.setBody(getBody(finalBody, alertDetails, matchesScoreCard).toString());

        ResponseEntity<String> response = mailClient.sendMail(mailBean);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Alert {} sent successfully to {}", alertDetails.getAlertId(), alertDetails.getMailId());
        }
    }

    private StringBuffer getBody(String finalBody, AlertDetails alertDetails, MatchScoreCard matchesScoreCard) {

        StringBuffer body = new StringBuffer(finalBody);
        int matchSize = matchesScoreCard.getScoreCard().size();

        if (alertDetails.getMatchType().equalsIgnoreCase("live") && !matchesScoreCard.getScoreCard().isEmpty()) {

            body.append("\n <b><h3 style='colour: blue'>")
                .append(matchesScoreCard.getMatchHeader().getTeam1().getName()).append(" vs ")
                .append(matchesScoreCard.getMatchHeader().getTeam2().getName()).append("</h3></b>");

            body.append("\n <h3><b>")
                    .append(matchesScoreCard.getMatchHeader().getTeam1().getShortName())
                    .append("</b>  ")
                    .append("<label ").append(styleGrey+">")
                    .append(getScore(matchesScoreCard, 0));

            if (matchSize > 2) {
                body.append(" & ")
                        .append(getScore(matchesScoreCard, 2))
                        .append("</label>")
                        .append("</h3>");
            }
            else{
                body.append("</label>")
                        .append("</h3>");
            }


            if (matchSize > 1) {
                body.append("\n")
                        .append("<h3><b>")
                        .append(matchesScoreCard.getMatchHeader().getTeam2().getShortName())
                        .append("</b>  ")
                        .append("<label ").append(styleGrey+">")
                        .append(getScore(matchesScoreCard, 1));
            }

            if (matchSize > 3) {
                body.append(" & ")
                        .append(getScore(matchesScoreCard, 3))
                        .append("</label>")
                        .append("</h3>");
            }
            else{
                body.append("</label>")
                        .append("</h3>");
            }
        }
        return body;
    }

    private String getScore(MatchScoreCard matchesScoreCard, int index) {
        return matchesScoreCard.getScoreCard().get(index).getScoreDetails().getRuns()
                + "/" + matchesScoreCard.getScoreCard().get(index).getScoreDetails().getWickets()
                + "(" + matchesScoreCard.getScoreCard().get(index).getScoreDetails().getOvers() + ")";
    }
}
