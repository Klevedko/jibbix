package com.jibbix_Inovus.service;

import com.jibbix_Inovus.WebApp;
import com.jibbix_Inovus.jira.api.DefaultJiraApi;
import com.jibbix_Inovus.jira.api.JiraApi;
import com.jibbix_Inovus.jira.map.TriggerMap;
import com.jibbix_Inovus.zabbix.api.DefaultZabbixApi;
import com.jibbix_Inovus.zabbix.api.ZabbixApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Service {
    public static Logger logger = LoggerFactory.getLogger(Service.class);
    public static ZabbixApi zabbixApi;
    private static JiraApi jiraApi;
    private static final Set<String> triggerIdExclude = new HashSet<String>(Arrays.asList(WebApp.properties.getTriggerIdExclude()));
    public final ArrayList<TriggerMap> triggerMaped = new ArrayList<>();

    public void rests() {
        try {
            zabbixApi = new DefaultZabbixApi(WebApp.properties.getZabbixAddress());
            jiraApi = new DefaultJiraApi();
            zabbixApi.init();
            boolean login = zabbixApi.login(WebApp.properties.getZabbixUser(), WebApp.properties.getZabbixPassword());
            logger.info("login: {}", login);
        } catch (Exception x) {
            logger.error(x.getMessage());
        }
        zabbixApi.getTriggers(triggerMaped, obj -> {
            // Check triggers for ignore...
            if (!triggerIdExclude.contains(obj.getTriggerId())) {
                zabbixApi.getEvents(triggerMaped, obj);
                zabbixApi.getHosts(triggerMaped, obj);
                logger.info("GOT a problem {}", obj.getDescriptionTemplated());
                if (!jiraApi.IssueExists(obj)) {
                    jiraApi.createIssue(obj, WebApp.properties.getJiraProjectKey(), 12101L, obj.getDescriptionTemplated(), obj.getGeneratedURL());
                }
            } else {
                logger.info("WE ARE IGNORING TRIGGER WITH ID = {}", obj.getTriggerId());
            }
        });
        //Collections.sort(App.triggerMaped);
    }

    public void after() {
        zabbixApi.destroy();
    }
}