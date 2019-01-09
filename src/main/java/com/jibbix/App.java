package com.jibbix;

import com.jibbix.jira.map.TriggerMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

import static com.jibbix.ReadProperties.printProperties;
import static com.jibbix.ReadProperties.readPropertiesFromConfigFile;
import static com.jibbix.ReadProperties.readPropertiesFromEnvFile;
import static com.jibbix.Work.startWork;

public class App {
    public static ArrayList<TriggerMap> triggerMaped = new ArrayList<>();
    public static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.debug("start at " + new Date());
        //readPropertiesFromConfigFile();
        readPropertiesFromEnvFile();
        startWork();
    }
}
