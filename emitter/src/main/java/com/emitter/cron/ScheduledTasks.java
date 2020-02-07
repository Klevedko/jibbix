package com.emitter.cron;

import com.emitter.service.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Date;

@Component
public class ScheduledTasks {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private HttpEntity requestEntity;
    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    public ScheduledTasks(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        this.mapper = new ObjectMapper();
    }

    //@Scheduled(cron="10 * 9-23 ? * SUN,MON,TUE,WED,THU,FRI")
    @Scheduled(cron="00 */20 9-18 ? * SUN,MON,TUE,WED,THU,FRI")
    public void run() {
        try {
            logger.info("EMITTER {}", new Date());
            Service service = new Service();
            service.rests(jdbcTemplate);
            service.after();
            logger.info("end.............\n");
        } catch (Exception x) {
            logger.error(x.getMessage());
        }
    }
}