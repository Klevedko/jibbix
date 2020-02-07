package com.jibbix.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.jibbix.WebApp;
import com.jibbix.jira.api.DefaultJiraApi;
import com.jibbix.jira.api.JiraApi;
import com.jibbix.jira.map.TriggerMap;
import com.jibbix.zabbix.api.DefaultZabbixApi;
import com.jibbix.zabbix.api.ZabbixApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.jibbix.jira.api.DefaultJiraApi.parseHostArray;

public class Service {
    public static Logger logger = LoggerFactory.getLogger(Service.class);
    public ZabbixApi zabbixApi;
    public JiraApi jiraApi;
    private Set<String> triggerIdExclude = new HashSet<String>(Arrays.asList(WebApp.properties.getTriggerIdExclude()));
    public ArrayList<TriggerMap> triggerMaped = new ArrayList<>();

    public void rests() {
        zabbixApi = new DefaultZabbixApi(WebApp.properties.getZabbixAddress());
        jiraApi = new DefaultJiraApi();
        zabbixApi.init();
        boolean login = zabbixApi.login(WebApp.properties.getZabbixUser(), WebApp.properties.getZabbixPassword());
        logger.info("Login {}", login);
        zabbixApi.getTriggers(zabbixApi, triggerMaped, obj -> {
            // Check triggers for ignore...
            if (!triggerIdExclude.contains(obj.getTriggerId())) {
                zabbixApi.getEvents(zabbixApi, triggerMaped, obj);
                zabbixApi.getHosts(zabbixApi, triggerMaped, obj);
                zabbixApi.getHostinterface(zabbixApi, triggerMaped, obj);
                if (!jiraApi.IssueExists(obj)) {
                    Issue createdJiraIssue = jiraApi.createIssue(obj, WebApp.properties.getJiraProjectKey(), 3L, parseHostArray(obj).append(obj.getDescriptionTemplated()).toString(), obj.getGeneratedURL());
                     zabbixApi.addComment(zabbixApi, obj.getEventId(), createdJiraIssue.getKey());
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
