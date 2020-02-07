package com.emitter.jira.api;

import com.emitter.PostAddParticipant;
import com.emitter.PostAddJiraComment;
import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.*;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.emitter.PostCreateForeignIssue;
import com.emitter.WebApp;
import com.emitter.zabbix.api.ZabbixApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import java.net.URI;
import java.util.*;

import static com.emitter.utils.Utils.getValueFromRegioncomResponse;

public class DefaultJiraApi implements JiraApi {
    public static Logger logger = LoggerFactory.getLogger(DefaultJiraApi.class);
    static JiraRestClient restClient;
    private static int startIndex = 0;
    private static int maxPerQuery = 2000;
    public String regioncomRequestWebLinkLink;
    public String regioncomSummary = "Недоступен сервер ";
    final String JIBBIX_EMITTER_QUERY = "select * from public.jibbix_emitter ";

    @Override
    public void scanJira(ZabbixApi zabbixApi, JdbcTemplate jdbcTemplate) {
        try {
            logger.info("ScanJira");
            AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            AuthenticationHandler auth = new BasicHttpAuthenticationHandler(WebApp.properties.getJiraUSER(), WebApp.properties.getJiraPASS());
            restClient = factory.create(WebApp.properties.getJiraServerUri(), auth);
            SearchRestClient searchRestClient = restClient.getSearchClient();
            Promise<SearchResult> searchResult = searchRestClient
                    .searchJql("status=" + "\"" + WebApp.properties.getScanIssueStatus() + "\"", maxPerQuery, startIndex, null);
            SearchResult results = searchResult.claim();
            for (Issue iterable_issue : results.getIssues()) {
                logger.info("...Found Issue in JIRA: {}", iterable_issue.getKey());
            }
            addIssuesToDb(results, jdbcTemplate);
            createRegioncomIssue(jdbcTemplate);
            createRegioncomParticipant(jdbcTemplate);
            ackZabbixEvent(zabbixApi, jdbcTemplate);
            closeJiraIssue(jdbcTemplate);
            commentJiraIssue(jdbcTemplate);
        } catch (Exception scan) {
            logger.error(scan.getMessage());
        } finally {
            try {
                restClient.close();
            } catch (Exception cl) {
                logger.error(cl.getMessage());
            }
        }
    }

    public void addIssuesToDb(SearchResult results, JdbcTemplate jdbcTemplate) {
        try {
            logger.info("addIssuesToDb...");
            Iterator<Issue> issueIterator = results.getIssues().iterator();
            //logger.info("issueIterator ".concat(issueIterator.hasNext() ? "is not empty" : "is empty"));
            while (issueIterator.hasNext()) {
                Issue oneIssue = issueIterator.next();
                logger.info("...Adding Issue: {} {}", oneIssue.getKey(), oneIssue.getSummary());
                jdbcTemplate.update(
                        "INSERT INTO jibbix_emitter (jira_issue_id, " +
                                "jira_issue_key, " +
                                "jira_issue_customfield_18801, " +
                                "jira_issue_customfield_19400," +
                                "jira_issue_description" +
                                ") values(?,?,?,?,?)",
                        oneIssue.getId(),
                        oneIssue.getKey(),
                        oneIssue.getField("customfield_18801").getValue().toString(),
                        oneIssue.getField("customfield_19400").getValue().toString(),
                        oneIssue.getDescription()
                );
            }
        } catch (Exception x) {
            logger.error(x.getMessage());
        }
    }

    @Override
    public void closeJiraIssue(JdbcTemplate jdbcTemplate) throws Exception {
        JiraRestClient client = null;
        try {
            logger.info("closeJiraIssues");
            List<Map<String, Object>> xx = jdbcTemplate.query(JIBBIX_EMITTER_QUERY +
                            "where regioncom_response is not null " +
                            "and regioncom_link is not null " +
                            //"and participiants_added " +
                            "and zabbix_acked " +
                            "and not jira_issue_closed",
                    new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1));
            if (xx.size() == 0) {
                logger.warn("No issues to close! Skipping this stage!");
                return;
            } else {
                logger.debug("{} issues to close!", xx.size());
                JiraRestClientFactory restFactory = new AsynchronousJiraRestClientFactory();
                URI jiraUri = new URI(WebApp.properties.getJiraServerUri().toString());
                client = restFactory.createWithBasicHttpAuthentication(jiraUri, WebApp.properties.getJiraUSER(), WebApp.properties.getJiraPASS());
                IssueRestClient issueClient = client.getIssueClient();
                for (Map<String, Object> line : xx) {
                    logger.info("closing jira with key = {} ?", line.get("jira_issue_key").toString());
                    List<IssueRestClient.Expandos> expand = new ArrayList<IssueRestClient.Expandos>();
                    expand.add(IssueRestClient.Expandos.TRANSITIONS);
                    Promise<Issue> promisedParent = issueClient.getIssue(line.get("jira_issue_key").toString(), expand);
                    Issue issue = promisedParent.claim();
                    logger.info("--> Current Issue's status = " + issue.getStatus().getName());
                    if (issue.getStatus().getName().equals("Разрешен")) {
                        logger.error("This issue has been closed already. Can't close it!");
                    } else {
                        try {
                            logger.info("Closing!");
                            Promise<Iterable<Transition>> ptransitions = issueClient.getTransitions(issue);
                            Iterable<Transition> transitions = ptransitions.claim();
                            for (Transition t : transitions) {
                                logger.info("Avaliable transitions: {} + {}", t.getName(), t.getId());
                            }
                            TransitionInput tinput = new TransitionInput(WebApp.properties.getCloseIssueTrainsition());
                            client.getIssueClient().transition(issue, tinput).claim();
                        } catch (Exception close) {
                            logger.error(close.getMessage());
                            throw new Exception(close);
                        }
                    }
                    try {
                        jdbcTemplate.update(
                                "Update jibbix_emitter set jira_issue_closed = true where jira_issue_id = ?",
                                line.get("jira_issue_id").toString()
                        );
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        post_errors(jdbcTemplate, e, line.get("jira_issue_key").toString());
                    }
                }
            }
        } catch (Exception sqs) {
            logger.error(sqs.getMessage());
        } finally {
            try {
                if (client != null)
                    client.close();
            } catch (Exception x) {
                logger.error(x.getMessage());
            }
        }
    }

    @Override
    public void commentJiraIssue(JdbcTemplate jdbcTemplate) {
        JiraRestClient client = null;
        try {
            logger.info("commentJiraIssue....");
            List<Map<String, Object>> xx = jdbcTemplate.query(JIBBIX_EMITTER_QUERY +
                            "where regioncom_response !='' " +
                            "and regioncom_link is not null " +
                            //"and participiants_added " +
                            "and zabbix_acked " +
                            "and jira_issue_closed " +
                            "and not jira_issue_commented",
                    new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1));
            if (xx.size() == 0) {
                logger.warn("Empty select. skipping this stage");
                return;
            } else {
                JiraRestClientFactory restFactory = new AsynchronousJiraRestClientFactory();
                URI jiraUri = new URI(WebApp.properties.getJiraServerUri().toString());
                client = restFactory.createWithBasicHttpAuthentication(jiraUri, WebApp.properties.getJiraUSER(), WebApp.properties.getJiraPASS());
                IssueRestClient issueClient = client.getIssueClient();
                for (Map<String, Object> line : xx) {
                    logger.info("Commenting jira with key = {}", line.get("jira_issue_key").toString());
                    try {
                        List<IssueRestClient.Expandos> expand = new ArrayList<IssueRestClient.Expandos>();
                        expand.add(IssueRestClient.Expandos.TRANSITIONS);
                        Promise<Issue> promisedParent = issueClient.getIssue(line.get("jira_issue_key").toString(), expand);
                        Issue issue = promisedParent.claim();
                        logger.info("Parsing response from Regincom. Getting web link...");
                        regioncomRequestWebLinkLink = getValueFromRegioncomResponse(line.get("regioncom_response").toString(), "_links", "web");
                        PostAddJiraComment postAddJiraComment = new PostAddJiraComment(regioncomRequestWebLinkLink, issue);
                        postAddJiraComment.main();
                        jdbcTemplate.update(
                                "Update jibbix_emitter set jira_issue_commented = true where jira_issue_id = ?",
                                line.get("jira_issue_id").toString()
                        );
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        post_errors(jdbcTemplate, e, line.get("jira_issue_key").toString());
                    }
                }
            }
        } catch (Exception sqs) {
            logger.error(sqs.getMessage());
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (Exception x) {
                logger.error(x.getMessage());
            }
        }
    }

    public void createRegioncomIssue(JdbcTemplate jdbcTemplate) {
        try {
            logger.info("createRegioncomIssue....");
            List<Map<String, Object>> issues = jdbcTemplate.query(JIBBIX_EMITTER_QUERY + "where regioncom_link is null",
                    new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1));
            if (issues.size() == 0) {
                logger.info("empty select. skipping this stage");
            } else {
                for (Map<String, Object> line : issues) {
                    if (line.get("regioncom_link") == null) {
                        logger.info(line.get("jira_issue_id").toString());
                        PostCreateForeignIssue postCreateForeignIssue = new PostCreateForeignIssue(
                                regioncomSummary.concat(line.get("jira_issue_customfield_19400").toString()),
                                line.get("jira_issue_description").toString(),
                                line.get("jira_issue_id").toString(),
                                jdbcTemplate);
                        postCreateForeignIssue.main();
                    }
                }
            }
        } catch (Exception sqs) {
            logger.error(sqs.getMessage());
        }
    }

    public void createRegioncomParticipant(JdbcTemplate jdbcTemplate) {
        try {
            logger.info("createRegioncomParticipant....");
            List<Map<String, Object>> issues = jdbcTemplate.query(JIBBIX_EMITTER_QUERY +
                            "where regioncom_response is not null " +
                            "and not participiants_added",
                    new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1));
            if (issues.size() == 0) {
                logger.info("Empty select. skipping this stage");
            } else {
                for (Map<String, Object> line : issues) {
                    if (line.get("participiants_added").toString().equals("false")) {
                        PostAddParticipant postAddParticipant = new PostAddParticipant(jdbcTemplate, line.get("jira_issue_id").toString());
                        postAddParticipant.main(getValueFromRegioncomResponse(line.get("regioncom_response").toString(), "", "issueId"));
                    } else
                        logger.debug("createRegioncomParticipant.... strange moment, check the query conditions! ");
                }
            }
        } catch (Exception sqs) {
            logger.error(sqs.getMessage());
        }
    }

    @Override
    public void ackZabbixEvent(ZabbixApi zabbixApi, JdbcTemplate jdbcTemplate) {
        try {
            logger.info("ackZabbixEvent....");
            List<Map<String, Object>> issues = jdbcTemplate.query(JIBBIX_EMITTER_QUERY +
                            "where regioncom_response is not null " +
                            "and regioncom_link is not null " +
                            //"and participiants_added " +
                            "and not zabbix_acked",
                    new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1));
            if (issues.size() == 0) {
                logger.debug("Empty select. skipping this stage");
            } else {
                for (Map<String, Object> line : issues) {
                    logger.info("update zabbix_acked for {}", line.get("jira_issue_id"));
                    zabbixApi.updateACK(zabbixApi, line.get("jira_issue_customfield_18801").toString(), line.get("regioncom_link").toString());
                    logger.info("update DB");
                    jdbcTemplate.update(
                            "Update jibbix_emitter set zabbix_acked = true where jira_issue_id = ?",
                            line.get("jira_issue_id").toString()
                    );
                }
            }
        } catch (Exception sqs) {
            logger.error(sqs.getMessage());
        }
    }

    public void post_errors(JdbcTemplate jdbcTemplate, Exception e, String jira_issue_key) {
        logger.info("set post_errors.....");
        jdbcTemplate.update(
                "Update jibbix_emitter set post_errors = ? where jira_issue_id = ?", e.toString(), jira_issue_key
        );
    }
}