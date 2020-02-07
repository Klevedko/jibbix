package com.emitter.zabbix.api;

import com.alibaba.fastjson.JSONObject;
import com.emitter.service.Service;

public interface ZabbixApi {

    void init();

    void destroy();

    String apiVersion();

    JSONObject call(Request request);

    boolean login(String user, String password);

    void updateACK(ZabbixApi zabbixApi,String eventId, String message);
}
