package com.example.trx.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionCache {
    public static final ConcurrentMap<String, String> SESSION_BY_USER = new ConcurrentHashMap<>();
    private SessionCache() { }
}
