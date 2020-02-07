package com.jibbix_Inovus.zabbix.api;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestBuilder {
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private Request request = new Request();

    private RequestBuilder() {
    }

    static public RequestBuilder newBuilder() {
        return new RequestBuilder();
    }

    public Request build() {
        if (request.getId() == null) {
            request.setId(nextId.getAndIncrement());
        }
        return request;
    }

    public RequestBuilder version(String version) {
        request.setJsonrpc(version);
        return this;
    }

    public RequestBuilder paramEntry(String key, Object value) {
        request.putParam(key, value);
        return this;
    }

    public RequestBuilder auth(String auth) {
        request.setAuth(auth);
        return this;
    }

    public RequestBuilder method(String method) {
        request.setMethod(method);
        return this;
    }

    public RequestBuilder id(Integer id) {
        request.setId(id);
        return this;
    }
}
