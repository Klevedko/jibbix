package com.jibbix.service;

import com.jibbix.App;
import com.jibbix.jira.api.DefaultJiraApi;
import com.jibbix.jira.api.JiraApi;
import com.jibbix.jira.map.TriggerMap;
import com.jibbix.zabbix.api.DefaultZabbixApi;
import com.jibbix.zabbix.api.ZabbixApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static com.jibbix.ReadProperties.properties;

public class Service {
    public static Logger logger = LoggerFactory.getLogger(App.class);
    public static ZabbixApi zabbixApi;
    private static JiraApi jiraApi;

    public static void before() {
        zabbixApi = new DefaultZabbixApi(properties.getZabbixAddress());
        jiraApi = new DefaultJiraApi();
        zabbixApi.init();
        boolean login = zabbixApi.login(properties.getZabbixUser(), properties.getZabbixPassword());
        logger.debug("login: {}", login);
    }

    public static void after() {
        zabbixApi.destroy();
    }

    public static void rests() {
        zabbixApi.getTriggers();
        for (TriggerMap obj : App.triggerMaped) {
            zabbixApi.getEvents(obj);
            zabbixApi.getHosts(obj);
            if (jiraApi.IssueExists(obj.getEventId())) {
                logger.warn("Already exists {}", obj.getDescriptionTemplated());
            } else {
                logger.debug("NOT found an Issue for {} with EventId = {1} with TriggerId = {2}",
                        obj.getDescriptionTemplated(), obj.getEventId(), obj.getTriggerId());
                logger.debug("Creating!");
                jiraApi.createIssue(obj, properties.getJiraProjectKey(), 3L, obj.getDescriptionTemplated(), obj.getGeneratedURL());
            }
        }
        Collections.sort(App.triggerMaped);
    }
}
