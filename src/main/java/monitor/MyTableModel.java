package monitor;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {
    private final String[] columnNames =
            {"container(容器)", "api(接口)", "className(类名)"};
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

    /**
     * 禁用编辑
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowData[rowIndex][columnIndex];
    }

    public void refresh(String[][] rowData){
        this.rowData = rowData;
        this.fireTableDataChanged();
    }

    public String[][] getRowData(){
        return this.rowData;
    }

    public String[] getTitle(){
        return this.columnNames;
    }
}
