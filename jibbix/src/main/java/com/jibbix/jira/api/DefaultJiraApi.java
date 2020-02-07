package com.jibbix.jira.api;

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
import com.jibbix.WebApp;
import com.jibbix.jira.map.TriggerMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class DefaultJiraApi implements JiraApi {
    public static Logger logger = LoggerFactory.getLogger(DefaultJiraApi.class);
    static JiraRestClient restClient;
    private final Set<String> templateIdAssegneedOFV = new HashSet<String>(Arrays.asList(WebApp.properties.getTriggers_assignee_ofv()));
    private int maxPerQuery = 100;
    private int startIndex = 0;

    public static StringBuffer parseHostArray(TriggerMap triggerObj) {
        StringBuffer hostLine = new StringBuffer();
        for (String hostip : triggerObj.getHostIp()) {
            hostLine.append(hostip).append(" ");
        }
        return hostLine;
    }

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
            issueBuilder.setIssueType(it).setFieldInput(new FieldInput("customfield_19400", parseHostArray(triggerObj).toString()));
            issueBuilder.setPriorityId("5".equals(triggerObj.getPriority()) ? 1L : 2L);
            if (templateIdAssegneedOFV.contains(triggerObj.getTemplateId())) {
                logger.info("Assigning issue {} OFV", triggerObj.getTemplateId());
                issueBuilder.setAssigneeName(WebApp.properties.getAssigneeOFV());
            } else {
                issueBuilder.setAssigneeName(WebApp.properties.getAssigneeOTV());
            }
            IssueInput issueInput = issueBuilder.build();
            Promise<BasicIssue> promise = restClient.getIssueClient().createIssue(issueInput);
            BasicIssue basicIssue = promise.claim();
            Promise<Issue> promiseJavaIssue = restClient.getIssueClient().getIssue(basicIssue.getKey());
            issue = promiseJavaIssue.claim();
            logger.info("NEW issue created with id : {} {}\r\n", issue.getId(), issue.getSummary());
            return issue;
        } catch (Exception x) {
            logger.error(x.getMessage());
        } finally {
            try {
            } catch (Exception closing) {
                logger.error(closing.getMessage());
            }
        }
        throw new RuntimeException();
    }

    @Override
    public boolean IssueExists(TriggerMap obj) {
        AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        AuthenticationHandler auth = new BasicHttpAuthenticationHandler(WebApp.properties.getJiraUSER(), WebApp.properties.getJiraPASS());
        restClient = factory.create(WebApp.properties.getJiraServerUri(), auth);
        SearchRestClient searchRestClient = restClient.getSearchClient();
        Promise<SearchResult> searchResult = searchRestClient.searchJql("\"Проект Ай-Новус\" = '" + WebApp.properties.getJiraINovusProject() + "' and ZabbixEvent ~ " + obj.getEventId(), maxPerQuery, startIndex, null);
        SearchResult results = searchResult.claim();
        if (results.getTotal() > 0)
            logger.info("ALREADY EXISTS {}", obj.getDescriptionTemplated());
        else
            logger.info("does not exist {}", obj.getDescriptionTemplated());
        return results.getTotal() > 0;
    }
}