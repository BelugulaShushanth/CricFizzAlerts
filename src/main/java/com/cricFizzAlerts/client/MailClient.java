package com.cricFizzAlerts.client;


import com.cricFizzAlerts.bean.mail.MailBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "api-gateway")
public interface MailClient {

    @PostMapping("/mailing-service/api/mail/sendMail")
    ResponseEntity<String> sendMail(@RequestBody MailBean mailBean);
}
