package com.emitter.service;

import com.emitter.WebApp;
import com.emitter.jira.api.DefaultJiraApi;
import com.emitter.jira.api.JiraApi;
import com.emitter.zabbix.api.DefaultZabbixApi;
import com.emitter.zabbix.api.ZabbixApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class Service {
    public static Logger logger = LoggerFactory.getLogger(Service.class);
    public JiraApi jiraApi;
    public ZabbixApi zabbixApi;

    public void rests(JdbcTemplate jdbcTemplate) {
        zabbixApi = new DefaultZabbixApi(WebApp.properties.getZabbixAddress());
        jiraApi = new DefaultJiraApi();
        zabbixApi.init();
        boolean login = zabbixApi.login(WebApp.properties.getZabbixUser(), WebApp.properties.getZabbixPassword());
        logger.info("login: {}", login);
        jiraApi.scanJira(zabbixApi,jdbcTemplate);

    }
    public void after() {
        zabbixApi.destroy();
    }
}