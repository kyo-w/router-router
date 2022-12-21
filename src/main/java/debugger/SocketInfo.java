package debugger;

public class SocketInfo {
    private String hostname;
    private String port;
    private String timeout;

    public SocketInfo(String hostname, String port, String timeout) {
        this.hostname = hostname;
        this.port = port;
        this.timeout = timeout;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
