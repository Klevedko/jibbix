package com.jibbix;

import java.net.URI;

public class Properties {
    private String zabbixAddress;
    private String zabbixUser;
    private String zabbixPassword;
    private URI jiraServerUri;
    private String jiraUSER;
    private String jiraPASS;
    private String jiraExtraProject;
    private String jiraProjectKey;
    private String jiraComponent;
    private String zabbixEventDetail;

    public Properties() {
    }

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

    public String getJiraExtraProject() {
        return jiraExtraProject;
    }

    public void setJiraExtraProject(String jiraExtraProject) {
        this.jiraExtraProject = jiraExtraProject;
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
}
