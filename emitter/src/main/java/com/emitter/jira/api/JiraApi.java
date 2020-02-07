package com.emitter.jira.api;

import com.emitter.zabbix.api.ZabbixApi;
import org.springframework.jdbc.core.JdbcTemplate;

public interface JiraApi {
    void scanJira(ZabbixApi zabbixApi, JdbcTemplate jdbcTemplate);

    void closeJiraIssue(JdbcTemplate jdbcTemplate) throws Exception;

    void commentJiraIssue(JdbcTemplate jdbcTemplate);

    void ackZabbixEvent(ZabbixApi zabbixApi, JdbcTemplate jdbcTemplate);
}
