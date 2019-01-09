package com.jibbix.zabbix.api;

import com.alibaba.fastjson.JSONObject;
import com.jibbix.jira.map.TriggerMap;

public interface ZabbixApi {

	void init();

	void destroy();

	String apiVersion();

	JSONObject call(Request request);

	boolean login(String user, String password);

	void getTriggers();

	void getEvents(TriggerMap obj);

	void getHosts(TriggerMap obj);
}
