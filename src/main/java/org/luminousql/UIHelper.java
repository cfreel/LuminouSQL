package org.luminousql;

import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.gui2.table.Table;

import java.util.List;

public class UIHelper {
    public static void populateResults(Table<String> table, List<List<String>> results, List<String> colNames) {
        if (!results.isEmpty()) {
            TableModel<String> tableModel = new TableModel(colNames.toArray(new String[]{}));
            for (List<String> row : results) {
                tableModel.addRow(row);
            }
            table.setTableModel(tableModel);
        }
    }



}
