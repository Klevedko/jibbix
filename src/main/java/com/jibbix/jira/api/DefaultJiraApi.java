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
import com.jibbix.App;
import com.jibbix.jira.map.TriggerMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jibbix.ReadProperties.properties;

public class DefaultJiraApi implements JiraApi {
    public static Logger logger = LoggerFactory.getLogger(App.class);
    static JiraRestClient restClient;

    @Override
    public Issue createIssue(TriggerMap triggerObj, String project, Long key, String summary, String description) {
        Issue issue = null;
        logger.warn("Connecting to create a task... ");
        JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
        try (JiraRestClient restClient = restClientFactory.createWithBasicHttpAuthentication(properties.getJiraServerUri(), properties.getJiraUSER(), properties.getJiraPASS())) {
        } catch (Exception x) {
            logger.error(x.toString());
        }
        IssueInputBuilder issueBuilder = new IssueInputBuilder(project, key, summary);
        issueBuilder.setDescription(description);
        IssueType it = new IssueType(properties.getJiraServerUri(), key, summary, false, "Testing the Issue creation", null);
        issueBuilder.setComponents(new BasicComponent(properties.getJiraServerUri(), 3L, properties.getJiraComponent(), "Generated due to zabbix problem"));
        issueBuilder.setIssueType(it).setFieldValue("customfield_14208", ComplexIssueInputFieldValue.with("value", properties.getJiraExtraProject()));
        issueBuilder.setIssueType(it).setFieldInput(new FieldInput("customfield_18801", triggerObj.getEventId()));
        if ("4".equals(triggerObj.getPriority()))
            issueBuilder.setPriorityId(2L);
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
    }

    @Override
    public boolean IssueExists(String objEventId) {
        AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        AuthenticationHandler auth = new BasicHttpAuthenticationHandler(properties.getJiraUSER(), properties.getJiraPASS());
        restClient = factory.create(properties.getJiraServerUri(), auth);
        //String jql = "project = 10207 and reporter = username and priority = 3 and id = 125916 ";
        String jql = "";//query; //"id = 192775 ";
        int maxPerQuery = 100;
        int startIndex = 0;
        SearchRestClient searchRestClient = restClient.getSearchClient();
        Promise<SearchResult> searchResult = searchRestClient
                .searchJql("\"Экстра-Проект\" = '" + properties.getJiraExtraProject() + "' and ZabbixEvent ~ " + objEventId,
                        maxPerQuery, startIndex, null);
        //.searchJql("id = 193139", maxPerQuery, startIndex, null);
        SearchResult results = searchResult.claim();
        return results.getTotal() > 0;
    }
}