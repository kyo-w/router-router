package debugger;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.spi.Connection;
import logger.LogOutput;
import monitor.MonitorComponent;
import utils.ConnectionInfo;
import utils.StatusInfo;
import utils.Vaildate;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DebuggerManger {
    private JLabel status;

    private LogOutput stdout;

    private static volatile boolean canRun;

    private String hostname;

    private String port;

    private static volatile VirtualMachine attach;

    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Thread currentThread;


    public DebuggerManger(JLabel status, LogOutput stdout) {
        this.status = status;
        this.stdout = stdout;
        canRun = true;
    }

    public boolean checkRun() {
        if (!canRun) {
            stdout.printSocketInfo(ConnectionInfo.REPORT_CONNECTION, hostname, port);
        }
        return canRun;
    }


    public void connect(MonitorComponent monitorComponent) {
        //仅支持单线程
        if (!canRun) {
            stdout.printSocketInfo(ConnectionInfo.REPORT_CONNECTION, hostname, port);
            return;
        }

        //检测参数
        if (!checkArg()) {
            stdout.printSocketInfo(ConnectionInfo.BAD_ARGMENTS, hostname, port);
            return;
        }
        DebuggerThread debuggerThread = new DebuggerThread(hostname, port, stdout, monitorComponent, status);
        Thread thread = new Thread(debuggerThread);
        currentThread = thread;
        currentThread.start();
    }

    public void stopConnect() {
        if (currentThread == null || attach == null) {
            currentThread = null;
            stdout.simpleInfo(ConnectionInfo.CLOSE_CONNECTION);
            canRun = true;
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    attach.dispose();
                } catch (VMDisconnectedException e) {
                    attach = null;
                    canRun = true;
                    stdout.printSocketInfo(ConnectionInfo.CONNECTION_REPEAT, hostname, port);
                    return;
                }
            }
        }).start();
        currentThread.stop();
        status.setText(StatusInfo.WAIT_CONNECTION);
        stdout.printSocketInfo(ConnectionInfo.CLOSE_CONNECTION, hostname, port);
        canRun = true;
        currentThread = null;
    }


    public void setArgs(String hostname, String port) {
        this.hostname = hostname;
        this.port = port;
    }

    private boolean checkArg() {
        return (Vaildate.isIPV4(hostname) && Vaildate.isPort(port));
    }

    static class DebuggerThread implements Runnable {

        private String hostname;
        private String port;
        private LogOutput logOutput;
        private MonitorComponent monitor;
        private JLabel status;

        public DebuggerThread(String hostname, String port, LogOutput logOutput, MonitorComponent monitorComponent, JLabel status) {
            this.hostname = hostname;
            this.port = port;
            this.logOutput = logOutput;
            this.monitor = monitorComponent;
            this.status = status;
        }

        @Override
        public void run() {
            canRun = false;

            DebuggerCore debuggerCore = new DebuggerCore(hostname, port, logOutput, status, monitor);
            attach = debuggerCore.initConnect();
            debuggerCore.start();
            canRun = true;
        }
    }
}
