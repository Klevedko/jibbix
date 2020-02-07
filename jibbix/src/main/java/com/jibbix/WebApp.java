package com.jibbix;

import com.jibbix.cron.ScheduledTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class WebApp {

    public static com.jibbix.Properties properties;
    public static Logger logger = LoggerFactory.getLogger(WebApp.class);

    public static void main(String[] args) {
        try {
            readPropertiesFromEnvFile();
        } catch (Exception e) {
            logger.error(e.getMessage());
            readPropertiesFromConfigFile();
        } finally {
            try {
                SpringApplication.run(WebApp.class);
            } catch (Exception spr) {
                logger.error(spr.getLocalizedMessage());
            }
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ScheduledTasks scheduledTasks(RestTemplate restTemplate) {
        return new ScheduledTasks(restTemplate);
    }

    protected static void readPropertiesFromEnvFile() {
        logger.info("readPropertiesFromEnvFile");
        properties = new com.jibbix.Properties();
        properties.setZabbixAddress(System.getenv("ZABBIX_ADDRESS"));
        properties.setZabbixUser(System.getenv("ZABBIX_USER"));
        properties.setZabbixPassword(System.getenv("ZABBIX_PASSWORD"));
        properties.setJiraServerUri(URI.create(System.getenv("JIRA_ADDRESS")));
        properties.setJiraUSER(System.getenv("JIRA_USER"));
        properties.setJiraPASS(System.getenv("JIRA_PASSWORD"));
        properties.setJiraINovusProject(System.getenv("JIRA_INOVUS_PROJECT").replaceAll("'", ""));
        properties.setJiraProjectKey(System.getenv("JIRA_PROJECT_KEY"));
        properties.setJiraComponent(System.getenv("JIRA_COMPONENT"));
        properties.setTriggerIdExclude(System.getenv("TRIGGER_ID_EXCLUDE").split(","));
        properties.setTriggers_assignee_ofv(System.getenv("TRIGGERS_ASSIGNEE_OFV").split(","));
        properties.setZabbixEventDetail(properties.getZabbixAddress().replace("api_jsonrpc.php", "tr_events.php?"));
        properties.setAssigneeOTV(System.getenv("ASSIGNEE_OTV"));
        properties.setAssigneeOFV(System.getenv("ASSIGNEE_OFV"));

        printProperties();
    }

    protected static void readPropertiesFromConfigFile() {
        try (InputStream input = WebApp.class.getClassLoader().getResourceAsStream("config.properties")) {
            logger.info("readPropertiesFromConfigFile...");
            java.util.Properties prop = new java.util.Properties();
            InputStreamReader reader = null;
            reader = new InputStreamReader(input, "UTF-8");
            prop.load(reader);
            properties = new Properties();
            properties.setZabbixAddress(prop.getProperty("ZabbixAddress"));
            properties.setZabbixUser(prop.getProperty("ZabbixUser"));
            properties.setZabbixPassword(prop.getProperty("ZabbixPassword"));
            properties.setJiraServerUri(URI.create(prop.getProperty("JiraAddress")));
            properties.setJiraUSER(prop.getProperty("JiraUser"));
            properties.setJiraPASS(prop.getProperty("JiraPassword"));
            properties.setJiraINovusProject(prop.getProperty("JiraINovusProject").replaceAll("'", ""));
            properties.setJiraProjectKey(prop.getProperty("JiraProjectKey"));
            properties.setJiraComponent(prop.getProperty("JiraComponent"));
            properties.setTriggerIdExclude(prop.getProperty("TriggerIdexclude").split(","));
            properties.setTriggers_assignee_ofv(prop.getProperty("triggerAssigneedOFV").split(","));
            properties.setZabbixEventDetail(properties.getZabbixAddress().replace("api_jsonrpc.php", "tr_events.php?"));
            properties.setAssigneeOTV(prop.getProperty("ASSIGNEE_OTV"));
            properties.setAssigneeOFV(prop.getProperty("ASSIGNEE_OFV"));
            printProperties();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    protected static void printProperties() {
        try {
            logger.info("printProperties...");
            logger.info("zabbixAddress= {}", properties.getZabbixAddress());
            logger.info("zabbixUser= {}", properties.getZabbixUser());
            logger.info("zabbixPassword= {}", properties.getZabbixPassword());
            logger.info("jiraServerUri= {}", properties.getJiraServerUri());
            logger.info("jiraUSER= {}", properties.getJiraUSER());
            logger.info("jiraPASS= {}", properties.getJiraPASS());
            logger.info("jiraINovusProject= {}", properties.getJiraINovusProject());
            logger.info("jiraProjectKey= {}", properties.getJiraProjectKey());
            logger.info("jiraComponent= {}", properties.getJiraComponent());
            logger.info("TriggerIdExclude= {}", Arrays.toString(properties.getTriggerIdExclude()));
            logger.info("TriggersAssigneeDianaHazeeva= {}", Arrays.toString(properties.getTriggers_assignee_ofv()));
            logger.info("zabbixEventDetail= {}", properties.getZabbixEventDetail());
        } catch (Exception s) {
            logger.error(s.getMessage());
        }
    }
}