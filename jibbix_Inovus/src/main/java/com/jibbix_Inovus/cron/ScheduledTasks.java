package com.jibbix_Inovus.cron;

import com.jibbix_Inovus.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class ScheduledTasks {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(cron="00 00,20,40 9-17 ? * SUN,MON,TUE,WED,THU,FRI")
    //@Scheduled(cron="*/10 * 9-17 ? * SUN,MON,TUE,WED,THU,FRI")
    public void run() {
        try {
            logger.info("JIBBIX_INOVUS {}", new Date());
            Service service = new Service();
            service.rests();
            service.after();
            logger.info("end.............\n");
        } catch (Exception x) {
            logger.error(x.getMessage());
        }
    }
}