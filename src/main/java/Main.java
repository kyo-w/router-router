import debugger.DebuggerManger;
import logger.LogOutput;
import logger.TextAreaOutputStream;
import manager.GUIAdaption;
import menu.SaveFileMenu;
import menu.Support;
import monitor.MonitorComponent;
import utils.StatusInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main extends JFrame {

    private static MonitorComponent monitorComponent;
    private static LogOutput stdout;

    private static DebuggerManger debuggerManger;

    /**
     * status控件，表示每个连接的状态
     */
    private static JLabel status;

    private GUIAdaption adaption = new GUIAdaption(this);

    public void setMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        // 主菜单
        JMenu menu = new JMenu("Menu");
        JMenuItem export = new JMenuItem("export(XLS)");
        menu.add(export);
        JFrame jFrame = this;
        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(monitorComponent.getData().length == 0){
                    stdout.simpleInfo("无数据导出");
                    return;
                }
                SaveFileMenu saveFileMenu = SaveFileMenu.fileSelect();
                String exportFile = saveFileMenu.export(monitorComponent.getTitle(), monitorComponent.getData());
                if(exportFile != null) {
                    stdout.simpleInfo("保存路径: " + exportFile);
                }
            }
        });

//        帮助菜单
        JMenu help = new JMenu("Help");
        JMenuItem support = new JMenuItem("support");
        help.add(support);
        support.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                new Support(adaption);
                stdout.simpleInfo("支持Spring/Tomcat/Jetty/Jersey/Resin/Struts(框架或web容器)");
            }
        });

        jMenuBar.add(menu);
        jMenuBar.add(help);
        this.setJMenuBar(jMenuBar);
        adaption.registryFont(menu);
        adaption.registryFont(export);
        adaption.registryFont(help);
        adaption.registryFont(support);
        adaption.registryJMenu(jMenuBar);
    }

    public void setController(JPanel controller) {
        controller.setLayout(new BoxLayout(controller, BoxLayout.Y_AXIS));

        // 输入框组件
        JPanel inputPanel = new JPanel();
        controller.add(inputPanel);
        JLabel address = new JLabel("address");
        inputPanel.add(address);
        JTextField addressText = new JTextField();
        inputPanel.add(addressText);
        JLabel port = new JLabel("port");
        inputPanel.add(port);
        JTextField portText = new JTextField();
        inputPanel.add(portText);
        JButton listener = new JButton("Listen");
        listener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!debuggerManger.checkRun()) {
                    return;
                }
                status.setText(StatusInfo.TRY_CONNECT);
                debuggerManger.setArgs(addressText.getText(), portText.getText());
                debuggerManger.connect(monitorComponent);
            }
        });
        inputPanel.add(listener);

        // status状态区组件
        JPanel statusPanel = new JPanel();
        controller.add(statusPanel);
        JLabel status = new JLabel(StatusInfo.WAIT_CONNECTION);
        this.status = status;
        statusPanel.add(status);


        JPanel featurePanel = new JPanel();
        controller.add(featurePanel);
        JButton closeListen = new JButton("Close Listen");
        closeListen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                debuggerManger.stopConnect();
            }
        });
        JButton clear_table = new JButton("clear Table");
        clear_table.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                monitorComponent.refresh(new String[][]{});
            }
        });
        JButton clear_log = new JButton("Clear log");
        clear_log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stdout.clearLog();
            }
        });
        featurePanel.add(clear_log);
        featurePanel.add(closeListen);
        featurePanel.add(clear_table);

        adaption.registryFont(address);
        adaption.registryFont(addressText);
        adaption.registryFont(portText);
        adaption.registryFont(port);
        adaption.registryFont(listener);
        adaption.registryFont(status);
        adaption.registryFont(closeListen);
        adaption.registryFont(clear_log);
        adaption.registryFont(clear_table);
        adaption.registryTextInput(addressText);
        adaption.registryTextInput(portText);
    }


    public static void main(String[] args) {

        Main main = new Main();
        main.setTitle("Router Kill");
        main.setMenu();

        JPanel controller = new JPanel();
        JPanel monitor = new JPanel();
        JTextArea loggerMonitor = new JTextArea();
        loggerMonitor.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(loggerMonitor);
        TextAreaOutputStream textAreaOutputStream = new TextAreaOutputStream(loggerMonitor);
        stdout = new LogOutput(textAreaOutputStream);


        main.setController(controller);
        monitorComponent = new MonitorComponent(monitor);
        monitorComponent.setStdout(stdout);
        debuggerManger = new DebuggerManger(status, stdout);

        main.adaption.registryFont(loggerMonitor);
        main.adaption.registryPanel(controller, 2);
        main.adaption.registryPanel(monitor, 3);
        main.adaption.registryPanel(jScrollPane, 2);
        monitorComponent.frameAddListener(main);
        main.add(controller);
        main.add(monitor);
        main.add(jScrollPane);

        main.adaption.render();
        main.setVisible(true);
    }
}
