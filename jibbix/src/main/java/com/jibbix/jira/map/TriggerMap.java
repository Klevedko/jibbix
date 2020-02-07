package com.jibbix.jira.map;

import java.util.ArrayList;

public class TriggerMap implements Comparable<TriggerMap> {
    private String triggerId;
    private String templateId;
    private String description;
    private String descriptionTemplated;
    private String lastchange;
    private String eventId;
    private String eventHostId;
    private String hostId;
    private String HostName;
    private String eventClock;
    private String priority;
    private String dependencies;
    private String generatedURL;
    private ArrayList<String> hostIp;

    public TriggerMap(String triggerId, String templateId, String description, String lastchange, String priority, String dependencies) {
        this.triggerId = triggerId;
        this.templateId=templateId;
        this.description = description;
        this.lastchange = lastchange;
        this.priority = priority;
        this.dependencies = dependencies;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastchange() {
        return lastchange;
    }

    public void setLastchange(String lastchange) {
        this.lastchange = lastchange;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventHostId() {
        return eventHostId;
    }

    public void setEventHostId(String eventHostId) {
        this.eventHostId = eventHostId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return HostName;
    }

    public void setHostName(String hostName) {
        HostName = hostName;
    }

    public String getDescriptionTemplated() {
        return descriptionTemplated;
    }

    public void setDescriptionTemplated(String descriptionTemplated) {
        this.descriptionTemplated = descriptionTemplated;
    }

    public String getEventClock() {
        return eventClock;
    }

    public void setEventClock(String eventClock) {
        this.eventClock = eventClock;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getGeneratedURL() {
        return generatedURL;
    }

    public void setGeneratedURL(String generatedURL) {
        this.generatedURL = generatedURL;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public ArrayList<String>  getHostIp() {
        return hostIp;
    }

    public void setHostIp(ArrayList<String>  hostIp) {
        this.hostIp = hostIp;
    }

    @Override
    public int compareTo(TriggerMap o) {
        int result = this.triggerId.compareTo(o.triggerId);
        if (result != 0) {
            return result;
        } else {
            return new String(this.triggerId).compareTo(new String(o.triggerId));
        }
    }
}