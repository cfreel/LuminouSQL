package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;

import java.util.Arrays;
import java.util.List;

public class TablesDialog extends DialogWindow {

    ComboBox<String> aliases;
    ActionListBox tablesListBox;
    Table<String> fieldsTable;

    protected TablesDialog(TerminalSize dialogSize, WindowBasedTextGUI textGUI) {
        super("Tables");

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        contentPanel.addComponent(new Label("Choose alias:"));
        aliases = new ComboBox<>();
        Configuration.aliases.forEach(e->{
            aliases.addItem(e.name);
        });

        contentPanel.addComponent(aliases);

        contentPanel.addComponent(new EmptySpace());
        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL).setPreferredSize(
                        new TerminalSize(dialogSize.getColumns(), 1)));

        tablesListBox = new ActionListBox(new TerminalSize(50, 5));

        contentPanel.addComponent(new Button("Load Tables", new Runnable() {
            @Override
            public void run() {
                List<String> tableNames = DatabaseDAO.getAllTableNames(aliases.getText());

                tableNames.forEach(e -> {
                    tablesListBox.addItem(e, new Runnable() {
                        @Override
                        public void run() {
                            List<List<String>> metadata = DatabaseDAO.getTableMetadata(aliases.getText(), e);
                            populateGrid(metadata);
                        }
                    });
                });
            }
        }));

        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL).setPreferredSize(
                        new TerminalSize(dialogSize.getColumns(), 1)));

        Panel tablesPanel = new Panel();
        tablesPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        tablesPanel.addComponent(tablesListBox);

        TableModel<String> tableModel = new TableModel<>("");
        fieldsTable = new Table<>(tableModel.getColumnLabels().toArray(new String[]{}));
        fieldsTable.setEscapeByArrowKey(false);
        fieldsTable.setCellSelection(true);
        TerminalSize tableSize = new TerminalSize(25, 8);
        fieldsTable.setPreferredSize(tableSize);
        tablesPanel.addComponent(fieldsTable);

        contentPanel.addComponent(tablesPanel);

        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL).setPreferredSize(
                        new TerminalSize(dialogSize.getColumns(), 1)));

        contentPanel.addComponent(new Button(LocalizedString.Close.toString(), new Runnable() {
            @Override
            public void run() {
                close();
            }
        }));

        setComponent(contentPanel);
        setFixedSize(dialogSize);
    }

    private void populateGrid(List<List<String>> metaData) {
        List<String> colNames = Arrays.asList("Column Name", "Type", "Size");
        UIHelper.populateResults(fieldsTable, metaData, colNames);
    }

}
