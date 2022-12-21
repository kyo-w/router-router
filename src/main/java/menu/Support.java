package menu;

import javax.swing.*;
import java.awt.*;
import manager.GUIAdaption;

public class Support extends JDialog {

    public Support(GUIAdaption guiAdaption){
        this.setTitle("支持");
        this.setVisible(true);
        this.setSize(200,250);
        //add one label
        Container contentPane = this.getContentPane();
        JLabel jLabel = new JLabel("Spring/Tomcat/Jersey/Jetty/Resin/Struts");

        contentPane.add(jLabel);
        //center 居中
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
