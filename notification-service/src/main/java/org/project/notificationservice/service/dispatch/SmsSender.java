package org.project.notificationservice.service.dispatch;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsSender {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty()) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized");
        } else {
            log.warn("Twilio Account SID is missing. SMS sending will fail.");
        }
    }

    public void send(String to, String messageBody) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromPhoneNumber),
                    messageBody
            ).create();

            log.info("SMS sent to {}: SID {}", to, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage());
            // Optional: Throw exception if you want to retry
        }
    }
}