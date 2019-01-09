package com.jibbix;

import com.jibbix.service.Service;


public class Work {
    public static Service service;

    public static void startWork() {
        service = new Service();
        service.before();
        service.rests();
        service.after();
    }
}
