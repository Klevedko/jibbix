package com.emitter.zabbix.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

public class DefaultZabbixApi implements ZabbixApi {
    private static final Logger logger = LoggerFactory.getLogger(DefaultZabbixApi.class);

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
                logger.error(e.getMessage());
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateACK(ZabbixApi zabbixApi, String eventId, String messsage) {
        try {
            Request request = RequestBuilder.newBuilder().method("event.acknowledge")
                    .paramEntry("eventids", eventId)
                    .paramEntry("action", 6)
                    .paramEntry("message", messsage)
                    .build();
            JSONObject result = zabbixApi.call(request);
            String error = getError(result);
            if (error != null)
                throw new RuntimeException(error);
        } catch (RuntimeException x) {
            logger.error(x.getMessage(), x);
            //throw new RuntimeException(x.getMessage());
            throw x;
        }
    }

    private String getError(JSONObject result) {
        try {
            return result.getJSONObject("error").get("data").toString();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
