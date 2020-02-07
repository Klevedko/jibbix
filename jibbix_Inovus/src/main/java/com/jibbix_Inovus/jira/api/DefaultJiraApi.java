package com.jibbix_Inovus.jira.api;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.jibbix_Inovus.WebApp;
import com.jibbix_Inovus.jira.map.TriggerMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJiraApi implements JiraApi {
    public static Logger logger = LoggerFactory.getLogger(DefaultJiraApi.class);
    static JiraRestClient restClient;

    @Override
    public Issue createIssue(TriggerMap triggerObj, String project, Long key, String summary, String description) throws RuntimeException {
        Issue issue = null;
        logger.warn("Connecting to create a task... ");
        JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
        try {
            JiraRestClient restClient = restClientFactory.createWithBasicHttpAuthentication(WebApp.properties.getJiraServerUri(), WebApp.properties.getJiraUSER(), WebApp.properties.getJiraPASS());
            IssueInputBuilder issueBuilder = new IssueInputBuilder(project, key, summary);
            issueBuilder.setDescription(description);
            IssueType it = new IssueType(WebApp.properties.getJiraServerUri(), key, summary, false, "Testing the Issue creation", null);
            issueBuilder.setComponents(new BasicComponent(WebApp.properties.getJiraServerUri(), 3L, WebApp.properties.getJiraComponent(), "Generated due to zabbix problem"));
            issueBuilder.setIssueType(it).setFieldValue("customfield_14208", ComplexIssueInputFieldValue.with("value", WebApp.properties.getJiraINovusProject()));
            issueBuilder.setIssueType(it).setFieldInput(new FieldInput("customfield_18801", triggerObj.getEventId()));

            if ("5".equals(triggerObj.getPriority()))
                issueBuilder.setPriorityId(1L);
            else
                issueBuilder.setPriorityId(2L);
            IssueInput issueInput = issueBuilder.build();
            Promise<BasicIssue> promise = restClient.getIssueClient().createIssue(issueInput);
            BasicIssue basicIssue = promise.claim();
            Promise<Issue> promiseJavaIssue = restClient.getIssueClient().getIssue(basicIssue.getKey());
            issue = promiseJavaIssue.claim();
            logger.info("New issue created with id : {} {}\r\n", issue.getId(), issue.getSummary());
            return issue;
        } catch (Exception x) {
            logger.error(x.getMessage());
        }
        throw new RuntimeException();
    }

    @Override
    public boolean IssueExists(TriggerMap obj) {
        AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        AuthenticationHandler auth = new BasicHttpAuthenticationHandler(WebApp.properties.getJiraUSER(), WebApp.properties.getJiraPASS());
        restClient = factory.create(WebApp.properties.getJiraServerUri(), auth);
        //String jql = "project = 10207 and reporter = username and priority = 3 and id = 125916 ";
        String jql = "\"Проект компании\" = '" + WebApp.properties.getJiraINovusProject() + "' and ZabbixEvent ~ " + obj.getEventId();
        int maxPerQuery = 400;
        int startIndex = 0;
        SearchRestClient searchRestClient = restClient.getSearchClient();
        Promise<SearchResult> searchResult = searchRestClient
                .searchJql(jql, maxPerQuery, startIndex, null);
        SearchResult results = searchResult.claim();
        if (results.getTotal() > 0)
            logger.info("ALREADY EXISTS {}", obj.getDescriptionTemplated());
        else
            logger.info("does not exist {}", obj.getDescriptionTemplated());
        return results.getTotal() > 0;
    }
}