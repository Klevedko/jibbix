package com.jibbix_Inovus.zabbix.api;

import com.alibaba.fastjson.JSONObject;
import com.jibbix_Inovus.jira.map.TriggerMap;
import com.jibbix_Inovus.service.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.function.Consumer;


public interface ZabbixApi {

	void init();

	void destroy();

	String apiVersion();

	JSONObject call(Request request);

	boolean login(String user, String password);

	void getTriggers(ArrayList<TriggerMap> triggerMaped, Consumer<TriggerMap> triggerMapConsumer);

	void getEvents( ArrayList<TriggerMap> triggerMaped, TriggerMap obj);

	void getHosts(ArrayList<TriggerMap> triggerMaped, TriggerMap obj);
}
