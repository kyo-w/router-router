import com.beust.jcommander.Parameter;

public class Config {
    @Parameter(names = {"-p", "--pid"},
            description = "JVM process")
    private String pid;

    @Parameter(names = {"--ip"}, description = "web server bind")
    private String ip;

    @Parameter(names = {"--port"}, description = "web server bind port")
    private String port;

    @Parameter(names = {"-h", "--help"}, description = "help", required = false)
    private Boolean help;

    @Parameter(names = {"-v", "--verbose"}, description = "jps verbose")
    private Boolean verbose = false;

    public String getPid() {
        return pid;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setHelp(Boolean help) {
        this.help = help;
    }

    public boolean getHelp() {
        if (help == null) {
            return false;
        }
        return help;
    }
}
