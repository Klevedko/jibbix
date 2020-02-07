package com.emitter;

import com.emitter.cron.ScheduledTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

@SpringBootApplication
@EnableScheduling
public class WebApp {
    public static Logger logger = LoggerFactory.getLogger(WebApp.class);
    public static Properties properties;

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
        properties = new Properties();
        properties.setZabbixAddress(System.getenv("ZABBIX_ADDRESS"));
        properties.setZabbixUser(System.getenv("ZABBIX_USER"));
        properties.setZabbixPassword(System.getenv("ZABBIX_PASSWORD"));
        properties.setZabbixEventDetail(properties.getZabbixAddress().replace("api_jsonrpc.php", "tr_events.php?"));

        properties.setJiraServerUri(URI.create(System.getenv("JIRA_ADDRESS")));
        properties.setJiraUSER(System.getenv("JIRA_USER"));
        properties.setJiraPASS(System.getenv("JIRA_PASSWORD"));
        properties.setScanIssueStatus(System.getenv("SCANISSUESTATUS"));
        properties.setCloseIssueTrainsition(Integer.parseInt(System.getenv("CLOSEISSUETRAINSITION")));

        properties.setRegioncomJiraUri(System.getenv("REGIONCOMJIRAURI"));
        properties.setRegioncomJiraUser(System.getenv("REGIONCOMJIRAUSER"));
        properties.setRegioncomJiraPassword(System.getenv("REGIONCOMJIRAPASSWORD"));
        printProperties();
    }

    protected static void readPropertiesFromConfigFile() {
        try (InputStream input = WebApp.class.getClassLoader().getResourceAsStream("config.properties")) {
            java.util.Properties prop = new java.util.Properties();
            InputStreamReader reader = null;
            reader = new InputStreamReader(input, "UTF-8");
            prop.load(reader);
            properties = new Properties();
            logger.debug("get the property values");
            properties.setZabbixAddress(prop.getProperty("ZabbixAddress"));
            properties.setZabbixUser(prop.getProperty("ZabbixUser"));
            properties.setZabbixPassword(prop.getProperty("ZabbixPassword"));
            properties.setZabbixEventDetail(properties.getZabbixAddress().replace("api_jsonrpc.php", "tr_events.php?"));

            properties.setJiraServerUri(URI.create(prop.getProperty("JiraAddress")));
            properties.setJiraUSER(prop.getProperty("JiraUser"));
            properties.setJiraPASS(prop.getProperty("JiraPassword"));
            properties.setScanIssueStatus(prop.getProperty("ScanIssueStatus").replaceAll("'", ""));
            properties.setCloseIssueTrainsition(Integer.parseInt(prop.getProperty("CloseIssueTrainsition")));

            properties.setRegioncomJiraUri(prop.getProperty("RegioncomJiraUri"));
            properties.setRegioncomJiraUser(prop.getProperty("RegioncomJiraUser"));
            properties.setRegioncomJiraPassword(prop.getProperty("RegioncomJiraPassword"));
            printProperties();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    protected static void printProperties() {
        logger.info("printProperties");
        logger.info("zabbixAddress = {}", properties.getZabbixAddress());
        logger.info("zabbixUser = {}", properties.getZabbixUser());
        logger.info("zabbixPassword = {}", properties.getZabbixPassword());
        logger.info("zabbixEventDetail = {}", properties.getZabbixEventDetail());

        logger.info("jiraServerUri = {}", properties.getJiraServerUri());
        logger.info("jiraUSER = {}", properties.getJiraUSER());
        logger.info("ScanIssueStatus = {}", properties.getScanIssueStatus());
        logger.info("CloseIssueTrainsition = {}", properties.getCloseIssueTrainsition());

        logger.info("RegioncomJiraUri = {}", properties.getRegioncomJiraUri());
        logger.info("RegioncomJiraUser = {}", properties.getRegioncomJiraUser());
        logger.info("RegioncomJiraPassword = {}", properties.getRegioncomJiraPassword());
    }
}