package com.cricFizzAlerts.utils;

import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CricAlertUtils {

    private final Logger logger = LoggerFactory.getLogger(CricAlertUtils.class);

    @Value("${host.cricbuzz}")
    private String cricbuzzHost;
    @Value("${rapidapi.Key}")
    private String rapiAPIKey;

    public static Boolean isKeyExpired = false;
    public static Integer keyIndex = 0;
    public static Integer maxKeys;

    public HttpHeaders getHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        List<String> keyList = Arrays.stream(rapiAPIKey.split(",")).collect(Collectors.toList());
        maxKeys = keyList.size();
        logger.info("RapidAPI KEY Index: {} Key:{}", keyIndex, keyList.get(keyIndex));
        httpHeaders.add("X-RapidAPI-Key", keyList.get(keyIndex));
        httpHeaders.add("X-RapidAPI-Host", cricbuzzHost);
        return httpHeaders;
    }

    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        return objectMapper;
    }

    public String readJsonFile(String jsonFileLoc){
        StringBuilder jsonString = new StringBuilder();
        try {
            List<String> list = Files.lines(Paths.get(jsonFileLoc))
                    .collect(Collectors.toList());
            for (String s : list){
                jsonString.append(s);
            }
        } catch (IOException e) {
            logger.error("Exception in CricUtils:readJsonFile", e);
        }
        return jsonString.toString();
    }

    public Long findTimeDifference(Long matchStartDT){
        Instant instant = Instant.ofEpochMilli(matchStartDT);

        long matchStartMills = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Kolkata"))
                .toInstant().toEpochMilli();
        long currentMills = LocalDateTime.now().atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant().toEpochMilli();

        return matchStartMills - currentMills;
    }

    public String mapMillsToDateTime(Long timeMills) {
        String dateTime = null;
        Instant instant = Instant.ofEpochMilli(timeMills);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Kolkata"));
        dateTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).format(zonedDateTime);
        dateTime = dateTime.replace(":00 ","");
        return dateTime;
    }



}
