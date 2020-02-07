create TABLE IF NOT EXISTS public.jibbix_emitter
(
    jira_issue_id character varying,
    jira_issue_key character varying,
    jira_issue_customfield_19400 character varying,
    jira_issue_customfield_18801 character varying,
    jira_issue_description character varying,
    regioncom_link character varying,
    regioncom_response character varying,
    participiants_added boolean default false,
    jira_issue_closed boolean default false,
    jira_issue_commented boolean default false,
    zabbix_acked boolean default false,
    post_errors character varying,
    PRIMARY KEY (jira_issue_id)
);