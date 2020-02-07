package com.jibbix.cron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jibbix.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Date;

@Component
public class ScheduledTasks {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    public ScheduledTasks(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        this.mapper = new ObjectMapper();
    }

    @Scheduled(cron = "00 40 9-17 ? * MON,TUE,WED,THU,FRI")
    //@Scheduled(cron = "*/5 * 9-17 ? * MON,TUE,WED,THU,FRI")
    public void run() {
        try {
            logger.info("JIBBIX {}", new Date());
            Service service = new Service();
            service.rests();
            service.after();
            logger.info("end.............\n");
        } catch (Exception x) {
            logger.error(x.getMessage());
        }
    }
}