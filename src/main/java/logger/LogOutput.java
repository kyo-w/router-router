package logger;

import utils.TimeUtils;

import java.io.PrintStream;

public class LogOutput extends PrintStream {
    private TextAreaOutputStream out;
    public LogOutput(TextAreaOutputStream out) {
        super(out, true);
        this.out = out;
    }

    public void clearLog(){
        out.clear();
    }

    public void printSocketInfo(String info, String hostname, String port){
        this.println(TimeUtils.getNow() + ": " + "{address: " + hostname + " , port: " + port + " }: " + info );
        this.flush();
    }

    public void tableInfo(String title, String info){
        this.println(TimeUtils.getNow() + ": " + title + ": " + info);
        this.flush();
    }

    public void simpleInfo(String info){
        this.println(TimeUtils.getNow() + ": " + info);
        this.flush();
    }

}
