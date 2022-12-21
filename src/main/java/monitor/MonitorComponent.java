package monitor;

import logger.LogOutput;
import utils.Css;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class MonitorComponent {
    private JPanel root;
    private LogOutput stdout;
    private Set<Component> fontList = new HashSet<>();
    MyTableModel myTableModel = new MyTableModel(new String[][]{});
    private JScrollPane jScrollPane;

    public MonitorComponent(JPanel root) {
        this.root = root;
        init();
    }

    public void setStdout(LogOutput stdout) {
        this.stdout = stdout;
    }

    public void init() {
        JTable jTable = new JTable(myTableModel);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        this.jScrollPane = jScrollPane;
        jTable.setRowHeight(50);
        double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.2;
        double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.2;
        jScrollPane.setPreferredSize(new Dimension(1000, (int) height));
        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectText = myTableModel.getRowData()[jTable.getSelectedRow()][jTable.getSelectedColumn()];

                    copyToClipBoard(selectText);
                    stdout.tableInfo("已复制到剪切板", selectText);
                }
            }
        });

        root.add(jScrollPane);
        registryFont(jTable);
        registryFont(jScrollPane);
        registryFont(jTable.getTableHeader());
        render();
    }

    public void refresh(String[][] rowData) {
        myTableModel.refresh(rowData);
    }

    public void copyToClipBoard(String info) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(info);
        systemClipboard.setContents(stringSelection, null);
    }


    public void registryFont(Component font){
        fontList.add(font);
    }
    public void render(){
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int height = root.getHeight();
                double font = height / 1000.0 * 60;
                for (Component component : fontList) {
                    component.setFont(new Font(Font.SERIF, Font.PLAIN, (int) font));
                }
            }
        });
    }

    public void frameAddListener(JFrame frame){
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = frame.getWidth();
                int height = frame.getHeight();

                jScrollPane.setPreferredSize(new Dimension(width - 40, (height / 8 * 3) -10));
            }
        });
    }

    public String[][] getData(){
        return myTableModel.getRowData();
    }

    public String[] getTitle(){
        return myTableModel.getTitle();
    }
}
