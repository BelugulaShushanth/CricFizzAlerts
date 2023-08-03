package com.cricFizzAlerts.services;

import com.cricFizzAlerts.bean.matchScoreCard.MatchScoreCard;
import com.cricFizzAlerts.utils.CricAlertUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CricbuzzService {

    private final Logger logger = LoggerFactory.getLogger(CricbuzzService.class);

    @Value("${host.cricbuzz}")
    private String cricbuzzHost;

    @Value("${cricbuzz.call-api}")
    private String callCricbuzz;

    @Value("${endpoint.get-match}")
    private String matchEndpoint;

    @Value("${endpoint.get-match-scorecard}")
    private String matchScoreCardEndpoint;

    @Value("${cricbuzz.sample.matchesScoreCard.data.location}")
    private String matchScoreCardLoc;

    @Autowired
    private CricAlertUtils cricAlertUtils;

    @Autowired
    private RestTemplate restTemplate;

    public MatchScoreCard getMatchesScoreCard(Long matchId){
        logger.info("Started getMatchesScoreCard");
        MatchScoreCard matchScoreCard = null;
        try{
            HttpHeaders httpHeaders = cricAlertUtils.getHeaders();
            String cricbuzzURL = "https://" + cricbuzzHost + matchEndpoint + matchId + matchScoreCardEndpoint;
            logger.info("cricbuzzURL: {}", cricbuzzURL);
            logger.info("callCricbuzz: {}", callCricbuzz);
            HttpEntity<String> httpEntity = new HttpEntity<>(cricbuzzURL,httpHeaders);
            if(callCricbuzz.equalsIgnoreCase("true")){
                ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(cricbuzzURL, HttpMethod.GET, httpEntity, JsonNode.class);
                if (responseEntity.getBody() != null && responseEntity.getStatusCode().is2xxSuccessful()){
                    JsonNode jsonNode = responseEntity.getBody();
                    matchScoreCard = cricAlertUtils.objectMapper().treeToValue(jsonNode, MatchScoreCard.class);
                }
            }
            else{
                matchScoreCard = cricAlertUtils.objectMapper().treeToValue(cricAlertUtils.objectMapper().readTree(cricAlertUtils.readJsonFile(matchScoreCardLoc)), MatchScoreCard.class);
            }

        }
        catch (Exception e){
            logger.error("Exception in CricBuzzService:getMatches", e);
        }
        return matchScoreCard;
    }
}
