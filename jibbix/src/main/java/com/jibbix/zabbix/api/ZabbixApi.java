package com.jibbix.zabbix.api;

import com.alibaba.fastjson.JSONObject;
import com.jibbix.jira.map.TriggerMap;
import com.jibbix.service.Service;

import java.util.ArrayList;
import java.util.function.Consumer;

public interface ZabbixApi {

    void init();

    void destroy();

    String apiVersion();

    JSONObject call(Request request);

    boolean login(String user, String password);

    void getTriggers(ZabbixApi zabbixApi, ArrayList<TriggerMap> triggerMaped, Consumer<TriggerMap> triggerMapConsumer);

    void getTemplates(Service service, String getTemplates);

    void getEvents(ZabbixApi zabbixApi, ArrayList<TriggerMap> triggerMaped, TriggerMap obj);

    void getHosts(ZabbixApi zabbixApi, ArrayList<TriggerMap> triggerMaped, TriggerMap obj);

    void getHostinterface(ZabbixApi zabbixApi, ArrayList<TriggerMap> triggerMaped, TriggerMap obj);

    void addComment(ZabbixApi zabbixApi, String eventId, String messsage);

}
