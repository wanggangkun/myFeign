package com.kun.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kun
 * @data 2022/1/16 14:00
 */
@ConfigurationProperties(prefix = HttpConsumerProperties.PREFIX)
public class HttpConsumerProperties {

    public static final String PREFIX = "spring.http.client";

    private int coreThreads = 10;
    private int maxThreads = 20;
    private long keepAliveTime = 500L;
    private int maxIdleConnections = 5;
    private long connectionKeepAliveTime = 5L;
    private int connectTimeOut = 10;
    private int readTimeOut = 10;
    private int writeTimeOut = 10;
    private int maxRequests = 10;

    public int getCoreThreads() {
        return coreThreads;
    }

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    public long getConnectionKeepAliveTime() {
        return connectionKeepAliveTime;
    }

    public void setConnectionKeepAliveTime(long connectionKeepAliveTime) {
        this.connectionKeepAliveTime = connectionKeepAliveTime;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getWriteTimeOut() {
        return writeTimeOut;
    }

    public void setWriteTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }
}
