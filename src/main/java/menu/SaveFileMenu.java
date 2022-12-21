package menu;

import utils.ExcelUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SaveFileMenu extends JFileChooser {
    private SaveFileMenu() {
    }

    public static SaveFileMenu fileSelect() {
        SaveFileMenu saveFileMenu = new SaveFileMenu();
        saveFileMenu.showOpenDialog(null);
        return saveFileMenu;
    }

    public String export(String[] title, String[][] result) {
        File f = getSelectedFile();
        if(f == null){
            return null;
        }
        String filepath = f.getPath();
        ExcelUtil.saveToXls(filepath, title, result);
        return filepath;
    }
}
