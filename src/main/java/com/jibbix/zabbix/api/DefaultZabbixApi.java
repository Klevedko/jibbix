package com.jibbix.zabbix.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jibbix.App;
import com.jibbix.jira.map.TriggerMap;
import com.jibbix.service.Service;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.jibbix.ReadProperties.properties;

public class DefaultZabbixApi implements ZabbixApi {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private CloseableHttpClient httpClient;

    private URI uri;

    private volatile String auth;

    public DefaultZabbixApi(String url) {
        try {
            uri = new URI(url.trim());
        } catch (URISyntaxException e) {
            throw new RuntimeException("url invalid", e);
        }
    }

    public DefaultZabbixApi(URI uri) {
        this.uri = uri;
    }

    public DefaultZabbixApi(String url, CloseableHttpClient httpClient) {
        this(url);
        this.httpClient = httpClient;
    }

    public DefaultZabbixApi(URI uri, CloseableHttpClient httpClient) {
        this(uri);
        this.httpClient = httpClient;
    }

    @Override
    public void init() {
        if (httpClient == null) {
            httpClient = HttpClients.custom().build();
        }
    }

    @Override
    public void destroy() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("close httpclient error!", e);
            }
        }
    }

    @Override
    public boolean login(String user, String password) {
        this.auth = null;
        Request request = RequestBuilder.newBuilder().paramEntry("user", user).paramEntry("password", password)
                .method("user.login").build();
        JSONObject response = call(request);
        String auth = response.getString("result");
        if (auth != null && !auth.isEmpty()) {
            this.auth = auth;
            return true;
        }
        return false;
    }

    @Override
    public String apiVersion() {
        Request request = RequestBuilder.newBuilder().method("apiinfo.version").build();
        JSONObject response = call(request);
        return response.getString("result");
    }

    public boolean hostExists(String name) {
        Request request = RequestBuilder.newBuilder().method("host.exists").paramEntry("name", name).build();
        JSONObject response = call(request);
        return response.getBooleanValue("result");
    }

    public String hostCreate(String host, String groupId) {
        JSONArray groups = new JSONArray();
        JSONObject group = new JSONObject();
        group.put("groupid", groupId);
        groups.add(group);
        Request request = RequestBuilder.newBuilder().method("host.create").paramEntry("host", host)
                .paramEntry("groups", groups).build();
        JSONObject response = call(request);
        return response.getJSONObject("result").getJSONArray("hostids").getString(0);
    }

    public boolean hostgroupExists(String name) {
        Request request = RequestBuilder.newBuilder().method("hostgroup.exists").paramEntry("name", name).build();
        JSONObject response = call(request);
        return response.getBooleanValue("result");
    }

    /**
     * @param name
     * @return groupId
     */
    public String hostgroupCreate(String name) {
        Request request = RequestBuilder.newBuilder().method("hostgroup.create").paramEntry("name", name).build();
        JSONObject response = call(request);
        return response.getJSONObject("result").getJSONArray("groupids").getString(0);
    }

    @Override
    public JSONObject call(Request request) {
        if (request.getAuth() == null) {
            request.setAuth(this.auth);
        }

        try {
            HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder.post().setUri(uri)
                    .addHeader("Content-Type", "application/json")
                    .setEntity(new StringEntity(JSON.toJSONString(request), ContentType.APPLICATION_JSON)).build();
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            byte[] data = EntityUtils.toByteArray(entity);
            return (JSONObject) JSON.parse(data);
        } catch (IOException e) {
            throw new RuntimeException("DefaultZabbixApi call exception!", e);
        }
    }


    @Override
    public void getHosts(TriggerMap object) {
        Request request = RequestBuilder.newBuilder().method("host.get")
                //.paramEntry("eventids","378417")
                .paramEntry("hostids", object.getEventHostId())
                .build();
        JSONObject result = Service.zabbixApi.call(request);

        JSONArray jsonArray = result.getJSONArray("result");
        logger.debug(JSON.toJSONString(result, true));
        for (Object schema : jsonArray) {
            JSONObject x = (JSONObject) schema;

            object.setHostName(x.getString("host"));
            object.setHostId(x.getString("hostid"));
            object.setDescriptionTemplated(object.getDescription().replace("{HOST.NAME}", object.getHostName()));
        }
    }

    @Override
    public void getTriggers() {
        JSONObject filter = new JSONObject();
        filter.put("value", "1");
        filter.put("status", "0");
        Request request = RequestBuilder.newBuilder().method("trigger.get")
                //.paramEntry("only_true", "true")
                //.paramEntry("expandComment" , "true")
                //.paramEntry("output", "extend")
                //.paramEntry("selectFunctions", "extend")
                //.paramEntry("triggerids", "30456")
                .paramEntry("filter", filter)
                //.paramEntry("selectTags", "extend")
                .paramEntry("sortfield", "triggerid")
                .paramEntry("sortorder", "desc")
                .paramEntry("min_severity", "4")
                .paramEntry("withLastEventUnacknowledged", "true")
                .paramEntry("selectDependencies", "true")
                .paramEntry("skipDependent", "true")
                .build();
        JSONObject result = Service.zabbixApi.call(request);
        logger.debug(JSON.toJSONString(result, true));
        JSONArray jsonArray = result.getJSONArray("result");

        for (Object schema : jsonArray) {
            JSONObject x = (JSONObject) schema;
            App.triggerMaped.add(new TriggerMap(x.getString("triggerid"),
                    x.getString("description"),
                    x.getString("lastchange"),
                    x.getString("priority"),
                    x.getString("dependencies")));
        }
    }

    @Override
    public void getEvents(TriggerMap objectId) {
        Request request = RequestBuilder.newBuilder().method("event.get")
                //.paramEntry("eventids", "377408")
                .paramEntry("objectids", objectId.getTriggerId())
                .paramEntry("acknowledged", false)
                .paramEntry("value", "1")
                //.paramEntry("output", "extend")
                .paramEntry("selectRelatedObject", "description")
                .paramEntry("selectHosts", "name")
                .paramEntry("sortfield", "clock")
                .paramEntry("sortorder", "desc")
                .paramEntry("time_from", objectId.getLastchange())
                .build();
        JSONObject result = Service.zabbixApi.call(request);
        logger.debug(JSON.toJSONString(result, true));
        JSONArray jsonArray = result.getJSONArray("result");
        try {
            JSONObject x1 = new JSONObject();
            for (Object schema : jsonArray) {
                JSONObject x = (JSONObject) schema;
                JSONArray jsonArray1 = x.getJSONArray("hosts");
                for (Object schema1 : jsonArray1)
                    x1 = (JSONObject) schema1;
                objectId.setEventId(x.getString("eventid"));
                objectId.setEventHostId(x1.getString("hostid"));
                objectId.setEventClock(x.getString("clock"));
                objectId.setGeneratedURL(properties.getZabbixEventDetail().concat("triggerid=".concat(objectId.getTriggerId())
                        .concat("&eventid=").concat(objectId.getEventId())));
            }
        } catch (Exception nullable) {
            logger.error(nullable.toString());
        }
    }
}
