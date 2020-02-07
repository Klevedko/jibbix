package com.jibbix;

import java.net.URI;

public class Properties {
    private String zabbixAddress;
    private String zabbixUser;
    private String zabbixPassword;
    private URI jiraServerUri;
    private String jiraUSER;
    private String jiraPASS;
    private String jiraINovusProject;
    private String jiraProjectKey;
    private String jiraComponent;
    private String zabbixEventDetail;
    private String[] triggerIdExclude;
    private String[] triggers_assignee_ofv;

    private String assigneeOTV;
    private String assigneeOFV;

    public String getZabbixAddress() {
        return zabbixAddress;
    }

    public void setZabbixAddress(String zabbixAddress) {
        this.zabbixAddress = zabbixAddress;
    }

    public String getZabbixUser() {
        return zabbixUser;
    }

    public void setZabbixUser(String zabbixUser) {
        this.zabbixUser = zabbixUser;
    }

    public String getZabbixPassword() {
        return zabbixPassword;
    }

    public void setZabbixPassword(String zabbixPassword) {
        this.zabbixPassword = zabbixPassword;
    }

    public URI getJiraServerUri() {
        return jiraServerUri;
    }

    public void setJiraServerUri(URI jiraServerUri) {
        this.jiraServerUri = jiraServerUri;
    }

    public String getJiraUSER() {
        return jiraUSER;
    }

    public void setJiraUSER(String jiraUSER) {
        this.jiraUSER = jiraUSER;
    }

    public String getJiraPASS() {
        return jiraPASS;
    }

    public void setJiraPASS(String jiraPASS) {
        this.jiraPASS = jiraPASS;
    }

    public String getJiraINovusProject() {
        return jiraINovusProject;
    }

    public void setJiraINovusProject(String jiraINovusProject) {
        this.jiraINovusProject = jiraINovusProject;
    }

    public String getJiraProjectKey() {
        return jiraProjectKey;
    }

    public void setJiraProjectKey(String jiraProjectKey) {
        this.jiraProjectKey = jiraProjectKey;
    }

    public String getJiraComponent() {
        return jiraComponent;
    }

    public void setJiraComponent(String jiraComponent) {
        this.jiraComponent = jiraComponent;
    }

    public String getZabbixEventDetail() {
        return zabbixEventDetail;
    }

    public void setZabbixEventDetail(String zabbixEventDetail) {
        this.zabbixEventDetail = zabbixEventDetail;
    }

    public String[] getTriggerIdExclude() {
        return triggerIdExclude;
    }

    public void setTriggerIdExclude(String[] triggerIdExclude) {
        this.triggerIdExclude = triggerIdExclude;
    }

    public String[] getTriggers_assignee_ofv() {
        return triggers_assignee_ofv;
    }

    public void setTriggers_assignee_ofv(String[] triggers_assignee_ofv) {
        this.triggers_assignee_ofv = triggers_assignee_ofv;
    }

    public String getAssigneeOTV() {
        return assigneeOTV;
    }

    public void setAssigneeOTV(String assigneeOTV) {
        this.assigneeOTV = assigneeOTV;
    }

    public String getAssigneeOFV() {
        return assigneeOFV;
    }

    public void setAssigneeOFV(String assigneeOFV) {
        this.assigneeOFV = assigneeOFV;
    }
}
