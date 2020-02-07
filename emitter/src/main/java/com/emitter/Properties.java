package com.emitter;

import java.net.URI;

public class Properties {
    private String zabbixAddress;
    private String zabbixUser;
    private String zabbixPassword;
    private URI jiraServerUri;
    private String jiraUSER;
    private String jiraPASS;
    private String zabbixEventDetail;

    private String RegioncomJiraUri;
    private String RegioncomJiraUser;
    private String RegioncomJiraPassword;
    private String ScanIssueStatus;

    private int CloseIssueTrainsition;

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


    public String getZabbixEventDetail() {
        return zabbixEventDetail;
    }

    public void setZabbixEventDetail(String zabbixEventDetail) {
        this.zabbixEventDetail = zabbixEventDetail;
    }

    public String getRegioncomJiraUri() {
        return RegioncomJiraUri;
    }

    public void setRegioncomJiraUri(String regioncomJiraUri) {
        RegioncomJiraUri = regioncomJiraUri;
    }

    public String getRegioncomJiraUser() {
        return RegioncomJiraUser;
    }

    public void setRegioncomJiraUser(String regioncomJiraUser) {
        RegioncomJiraUser = regioncomJiraUser;
    }

    public String getRegioncomJiraPassword() {
        return RegioncomJiraPassword;
    }

    public void setRegioncomJiraPassword(String regioncomJiraPassword) {
        RegioncomJiraPassword = regioncomJiraPassword;
    }

    public String getScanIssueStatus() {
        return ScanIssueStatus;
    }

    public void setScanIssueStatus(String scanIssueStatus) {
        ScanIssueStatus = scanIssueStatus;
    }

    public int getCloseIssueTrainsition() {
        return CloseIssueTrainsition;
    }

    public void setCloseIssueTrainsition(int closeIssueTrainsition) {
        CloseIssueTrainsition = closeIssueTrainsition;
    }
}
