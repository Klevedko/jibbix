package com.jibbix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class ReadProperties {
    public static Logger logger = LoggerFactory.getLogger(App.class);
    public static com.jibbix.Properties properties = new com.jibbix.Properties();
    ;

    protected static void readPropertiesFromEnvFile() {
        logger.debug("get the property values");

        properties.setZabbixAddress(System.getenv("ZABBIX_ADDRESS"));
        properties.setZabbixUser(System.getenv("ZABBIX_USER"));
        properties.setZabbixPassword(System.getenv("ZABBIX_PASSWORD"));
        properties.setJiraServerUri(URI.create(System.getenv("JIRA_ADDRESS")));
        properties.setJiraUSER(System.getenv("JIRA_USER"));
        properties.setJiraPASS(System.getenv("JIRA_PASSWORD"));
        properties.setJiraExtraProject(System.getenv("JIRA_EXTRA_PROJECT").replaceAll("'", ""));
        properties.setJiraProjectKey(System.getenv("JIRA_PROJECT_KEY"));
        properties.setJiraComponent(System.getenv("JIRA_COMPONENT"));
        properties.setZabbixEventDetail(properties.getZabbixAddress().replace("api_jsonrpc.php", "tr_events.php?"));
        printProperties();
    }

    protected static void readPropertiesFromConfigFile() {
        try (InputStream input = new FileInputStream("config.properties")) {
            java.util.Properties prop = new java.util.Properties();
            InputStreamReader reader = null;
            reader = new InputStreamReader(input, "UTF-8");
            prop.load(reader);

            logger.debug("get the property values");
            properties.setZabbixAddress(prop.getProperty("ZabbixAddress"));
            properties.setZabbixUser(prop.getProperty("ZabbixUser"));
            properties.setZabbixPassword(prop.getProperty("ZabbixPassword"));
            properties.setJiraServerUri(URI.create(prop.getProperty("JiraAddress")));
            properties.setJiraUSER(prop.getProperty("JiraUser"));
            properties.setJiraPASS(prop.getProperty("JiraPassword"));
            properties.setJiraExtraProject(prop.getProperty("JiraExtraProject").replaceAll("'", ""));
            properties.setJiraProjectKey(prop.getProperty("JiraProjectKey"));
            properties.setJiraComponent(prop.getProperty("JiraComponent"));
            properties.setZabbixEventDetail(properties.getZabbixAddress().replace("api_jsonrpc.php", "tr_events.php?"));
            input.close();
            printProperties();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected static void printProperties() {
        logger.debug("zabbixAddress = {}", properties.getZabbixAddress());
        logger.debug("zabbixUser = {}", properties.getZabbixUser());
        logger.debug("zabbixPassword = {}", properties.getZabbixPassword());
        logger.debug("jiraServerUri = {}", properties.getJiraServerUri());
        logger.debug("jiraUSER = {}", properties.getJiraUSER());
        logger.debug("jiraPASS = {}", properties.getJiraPASS());
        logger.debug("jiraExtraProject = {}", properties.getJiraExtraProject());
        logger.debug("jiraProjectKey = {}", properties.getJiraProjectKey());
        logger.debug("jiraComponent = {}", properties.getJiraComponent());
        logger.debug("zabbixEventDetail = {}", properties.getZabbixEventDetail());
    }
}
