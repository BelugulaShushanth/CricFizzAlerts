package com.cricFizzAlerts.services;

import com.cricFizzAlerts.bean.alert.AlertDetails;
import com.cricFizzAlerts.bean.mail.MailBean;
import com.cricFizzAlerts.bean.matchScoreCard.MatchScoreCard;
import com.cricFizzAlerts.client.MailClient;
import com.cricFizzAlerts.repository.AlertsRepository;
import com.cricFizzAlerts.utils.CricAlertUtils;
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

    @Autowired
    private CricAlertUtils cricAlertUtils;

    @Async
    public void scheduleMailAlerts(AlertDetails alertDetails) {

        try {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (alertsRepository.findById(alertDetails.getAlertId()).get().getIsActive()) {
                        if (alertDetails.getAlertType().equalsIgnoreCase("score")) {

                            if (alertDetails.getMatchType().equalsIgnoreCase("live")) {
                                if(!sendMailAlert(alertDetails)){
                                    logger.info("Alert: {} deactivated",alertDetails.getAlertId());
                                    cancel();
                                }
                            } else if (alertDetails.getMatchType().equalsIgnoreCase("upcoming")) {
                                if(!sendMailAlert(alertDetails)){
                                    logger.info("Alert: {} deactivated",alertDetails.getAlertId());
                                    cancel();
                                }
                            }
                        }
                    } else {
                        sendAlertCancelMail(alertDetails);
                        logger.info("Alert: {} deactivated",alertDetails.getAlertId());
                        cancel();
                    }
                }
            };

            Timer timer = new Timer();
            if (alertDetails.getMatchType().equalsIgnoreCase("upcoming")) {

                long timeDifference = cricAlertUtils.findTimeDifference(alertDetails.getMatchStartDT());

                timer.scheduleAtFixedRate(task, timeDifference, alertDetails.getTimePeriod() * 60000);
                String dateTime = cricAlertUtils.mapMillsToDateTime(alertDetails.getMatchStartDT());
                sendAlertScheduledMail(alertDetails,dateTime);
                logger.info("Alert Scheduled for alertId {} and it starts at Date {} In Mills {}",alertDetails.getAlertId()
                        ,dateTime,timeDifference);
            } else {
                timer.scheduleAtFixedRate(task, 0, alertDetails.getTimePeriod() * 60000);
                logger.info("Alert Scheduled for alertId {} and it starts now", alertDetails.getAlertId());
            }

        }catch (Exception e){
            logger.error("Exception in SendAlertsService:scheduleMailAlerts() : {}",e.getMessage());
        }

    }

    private void sendAlertScheduledMail(AlertDetails alertDetails, String dateTime) {
        MailBean mailBean = new MailBean();
        mailBean.setToMailId(alertDetails.getMailId());
        mailBean.setSubject(finalSubject.replace("alertType", alertDetails.getAlertType()));
        mailBean.setBody("<h4>Dear user, this is from cricfizz</h4>\n"+
                        "<h3>Alert Scheduled for</h3><br> <label><b>Series: </b> "+alertDetails.getSeriesName()+ "</label>"+
                        "<br><label><b>Match: </b> "+alertDetails.getMatchName()+"</label>"+
                        "<br><label><b>Starts At: </b> "+dateTime+"</label>");
        mailClient.sendMail(mailBean);
    }

    private void sendAlertCancelMail(AlertDetails alertDetails) {
        MailBean mailBean = new MailBean();
        mailBean.setToMailId(alertDetails.getMailId());
        mailBean.setSubject(finalSubject.replace("alertType", alertDetails.getAlertType()));
        mailBean.setBody("<h4>Dear user, this is from cricfizz</h4>\n"+
                "<h3>Alert Deactivated for</h3><br> <label><b>Series: </b> "+alertDetails.getSeriesName()+ "</label>"+
                "<br><label><b>Match: </b> "+alertDetails.getMatchName()+"</label>");
        mailClient.sendMail(mailBean);
    }

    private boolean sendMailAlert(AlertDetails alertDetails) {

        MatchScoreCard matchesScoreCard = cricbuzzService.getMatchesScoreCard(alertDetails.getMatchId());
        if(matchesScoreCard.getMatchHeader().getState().equalsIgnoreCase("In Progress")) {
            StringBuffer mailId = new StringBuffer(alertDetails.getMailId());
            String subject = finalSubject;

            MailBean mailBean = new MailBean();
            mailBean.setToMailId(mailId.toString());
            mailBean.setSubject(subject.replace("alertType", alertDetails.getAlertType()));

            StringBuffer mailBody = getBody(finalBody, matchesScoreCard);

            if (mailBody != null) {
                mailBean.setBody(mailBody.toString());
                ResponseEntity<String> response = mailClient.sendMail(mailBean);
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("Alert {} sent successfully to {}", alertDetails.getAlertId(), alertDetails.getMailId());
                }
            }
            return true;
        }

        return false;
    }

    private StringBuffer getBody(String finalBody, MatchScoreCard matchesScoreCard) {

        StringBuffer body = new StringBuffer(finalBody);
        int matchSize = matchesScoreCard.getScoreCard().size();

        if (!matchesScoreCard.getScoreCard().isEmpty()
            && matchesScoreCard.getScoreCard().get(0).getScoreDetails() != null
            && matchesScoreCard.getScoreCard().get(0).getScoreDetails().getOvers() != 0) {

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
            return body;
        }
        else{
            return null;
        }

    }

    private String getScore(MatchScoreCard matchesScoreCard, int index) {
        return matchesScoreCard.getScoreCard().get(index).getScoreDetails().getRuns()
                + "/" + matchesScoreCard.getScoreCard().get(index).getScoreDetails().getWickets()
                + "(" + matchesScoreCard.getScoreCard().get(index).getScoreDetails().getOvers() + ")";
    }
}
