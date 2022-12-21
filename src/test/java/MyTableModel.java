
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class MyTableModel
extends AbstractTableModel {
    private final String[] columnNames =
            {"title", "info"};
    private String[][] rowData = null;

    public MyTableModel(String[][] rowData){
        this.rowData =rowData;
    }

    @Override
    public int getRowCount() {
        return rowData.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowData[rowIndex][columnIndex];
    }

    public void refresh(String[][] rowData){
        this.rowData = rowData;
        this.fireTableDataChanged();
    }
}
