package com.kyodream.debugger.pojo;

public class DebuggerArgs {
    private String port;
    private String hostname;
    private String timeout;

    public DebuggerArgs(String port, String hostname, String timeout) {
        this.port = port;
        this.hostname = hostname;
        this.timeout = timeout;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}