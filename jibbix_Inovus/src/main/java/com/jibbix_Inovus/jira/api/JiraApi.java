package com.jibbix_Inovus.jira.api;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.jibbix_Inovus.jira.map.TriggerMap;

public interface JiraApi {
    Issue createIssue(TriggerMap triggerObj, String project, Long key, String summary, String description);

    boolean IssueExists(TriggerMap obj);
}
