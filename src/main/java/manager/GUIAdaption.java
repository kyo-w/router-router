package manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;

/**
 * GUI组件大小自适应
 */
public class GUIAdaption {

    //菜单管理
    private JMenuBar jMenuBar;

    //    功能区管理
    private Map<Component, Integer> contentPanelList = new LinkedHashMap<>();

    private JFrame root;

    //    字体大小字样管理
    private Set<Component> fontList = new HashSet<>();

    //    输入框管理
    private Set<Component> textList = new HashSet<>();


    public GUIAdaption(JFrame root) {
        this.root = root;
        root.getContentPane().setLayout(null);
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        root.setSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 6 * 4)));
        root.setBackground(Color.lightGray);
    }

    public void addAdaptToMenuList() {

    }

    /**
     * 自适应JMenuBar
     *
     * @param jMenuBar
     */
    public void registryJMenu(JMenuBar jMenuBar) {
        this.jMenuBar = jMenuBar;
    }

    public void registryFont(Component component) {
        fontList.add(component);
    }

    public void registryPanel(Component component, int weight) {
        contentPanelList.put(component, weight);
    }

    public void registryTextInput(Component component) {
        textList.add(component);
    }

    public void render() {
        renderPanel();
        renderFont();
        renderTextInput();
    }

    private void renderFont() {
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int height = root.getHeight();
                double font = height / 1000.0 * 25;
                for (Component component : fontList) {
                    component.setFont(new Font(Font.SERIF, Font.PLAIN, (int) font));
                }
            }
        });
    }

    private void renderPanel() {
        int tmp = 0;
        Collection<Integer> values = contentPanelList.values();
        for (Integer weight : values) {
            tmp += weight;
        }
        final int denominator = tmp + 1;
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = root.getWidth();
                int height = root.getHeight();
                int location = 0;
                for (Component component : contentPanelList.keySet()) {
                    component.setBounds(0, location, width - 30, height / denominator * contentPanelList.get(component));
                    location += height / denominator * contentPanelList.get(component);
                }
            }
        });
    }

    private void renderTextInput() {
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = (int) (root.getWidth() / 1000.0 * 125);
                int height = (int) (root.getHeight() / 1000.0 * 35);

                for (Component textInput : textList) {
                    textInput.setPreferredSize(new Dimension(width, height));
                }
            }
        });
    }
}
