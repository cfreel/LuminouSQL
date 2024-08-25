package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class UIThread implements Runnable {
    static ComboBox<String> comboBox;
    static List<String> tableNames = new ArrayList<>();
    static Table<String> table;
    static MenuItem pullDataMenuItem;

    public void run() {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
            int width = screen.getTerminalSize().getColumns();

            final WindowBasedTextGUI textGUI =
                    new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.CYAN));
            final Window window = new BasicWindow();

            List<Window.Hint> hints = new ArrayList<>();
            hints.add(Window.Hint.FULL_SCREEN);
            window.setHints(hints);

            Theme theme = getTheme();
            window.setTheme(theme);

            Panel contentPanel = new Panel(new BorderLayout());
            contentPanel.setTheme(theme);

            Panel topPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            addMenuToPanel(topPanel, textGUI, screen);

            topPanel.addComponent(
                    new Separator(Direction.HORIZONTAL).setPreferredSize(new TerminalSize(width, 1)));

            TableModel<String> tableModel = new TableModel<>("");
            table = new Table<>(tableModel);
            table.setEscapeByArrowKey(false);
            table.setCellSelection(true);

            Panel bottomPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            bottomPanel.addComponent(
                    new EmptySpace()
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            bottomPanel.addComponent(
                    new Separator(Direction.HORIZONTAL).
                            setPreferredSize(new TerminalSize(screen.getTerminalSize().getColumns(), 1)));

            contentPanel.addComponent(topPanel, BorderLayout.Location.TOP);
            contentPanel.addComponent(table, BorderLayout.Location.CENTER);
            contentPanel.addComponent(bottomPanel, BorderLayout.Location.BOTTOM);

            window.setComponent(contentPanel);
            textGUI.addWindowAndWait(window);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(screen != null) {
                try {
                    screen.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void addMenuToPanel(Panel panel, WindowBasedTextGUI textGUI, Screen screen) {
        MenuBar menubar = new MenuBar();

        // "File" menu
        Menu fileMenu = new Menu("File");
        menubar.add(fileMenu);
        MenuItem fileOpen = new MenuItem("Open...", new Runnable() {
            public void run() {
                File file = new FileDialogBuilder().build().showDialog(textGUI);
                if (file != null)
                    MessageDialog.showMessageDialog(
                            textGUI, "Open", "Selected file:\n" + file, MessageDialogButton.OK);
            }
        });
        MenuItem fileExit = new MenuItem("Exit", new Runnable() {
            public void run() {
                System.exit(0);
            }
        });
        fileMenu.add(fileOpen);
        fileMenu.add(fileExit);

        Menu menuDB = new Menu("DB");
        menubar.add(menuDB);

        menuDB.add(new MenuItem("Drivers", new Runnable() {
            @Override
            public void run() {
                TerminalSize parentSize = screen.getTerminalSize();
                TerminalSize size = new TerminalSize(parentSize.getColumns()-8,
                        parentSize.getRows()-8);
                new DriverDialogBuilder(size, textGUI).build().showDialog(textGUI);
            }
        }));

        menuDB.add(new MenuItem("Aliases", new Runnable() {
            @Override
            public void run() {
                TerminalSize parentSize = screen.getTerminalSize();
                TerminalSize size = new TerminalSize(parentSize.getColumns()-8,
                        parentSize.getRows()-6);
                new AliasDialogBuilder(size, textGUI).build().showDialog(textGUI);
            }
        }));

        menuDB.add(new MenuItem("Load Tables", new Runnable() {
            @Override
            public void run() {
                tableNames = DatabaseDAO.getAllTableNames();
                pullDataMenuItem.setEnabled(true);
            }
        }));

        pullDataMenuItem = new MenuItem("Pull Data for Table...", new Runnable() {
            @Override
            public void run() {
                ActionListDialogBuilder aldb = new ActionListDialogBuilder()
                        .setTitle("Available Tables")
                        .setDescription("Choose a Table to View...");

                for (String name : tableNames) {
                    aldb = aldb.addAction(name, new Runnable() {
                        @Override
                        public void run() {
                            runQuery("select * from " + name);
                        }
                    });
                }
                aldb.build().showDialog(textGUI);
            }
        });
        pullDataMenuItem.setEnabled(false);
        menuDB.add(pullDataMenuItem);

        Menu menuQuery = new Menu("Queries");
        menubar.add(menuQuery);
        menuQuery.add(new MenuItem("Run Query", new Runnable() {
            @Override
            public void run() {
                TerminalSize parentSize = screen.getTerminalSize();
                TerminalSize size = new TerminalSize(parentSize.getColumns()-8,
                        parentSize.getRows()-6);
                new QueryDialogBuilder(size, textGUI).build().showDialog(textGUI);
            }
        }));

        menuQuery.add(new MenuItem("Save Results", new Runnable() {
            @Override
            public void run() {
                // just file dialog (or message if validation error)
                File file = new FileDialogBuilder().build().showDialog(textGUI);
                if (file != null) {
                    // todo: write data to file

                }

            }
        }));

        // "Help" menu
        Menu menuHelp = new Menu("Help");
        menubar.add(menuHelp);
        menuHelp.add(new MenuItem("Homepage", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "Homepage", "https://github.com/mabe02/lanterna", MessageDialogButton.OK);
            }
        }));
        menuHelp.add(new MenuItem("About", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "About", "LuminSQL version 0.1", MessageDialogButton.OK);
            }
        }));

        panel.addComponent(menubar);
    }


    private static void addFileLoadToPanel(Panel contentPanel, WindowBasedTextGUI textGUI) {
        contentPanel.addComponent(new Button("Choose File", new Runnable() {
            @Override
            public void run() {
                File file = new FileDialogBuilder()
                        .setTitle("Open File")
                        .setDescription("Choose a file")
                        .setActionLabel("Open")
                        .build()
                        .showDialog(textGUI);
                if (file != null) {
                    System.out.println("File deets:" + file.getAbsolutePath() + ", " + file.getPath() + ", " +
                            file.getName());
                    try {
                        TableModel<String> tableModel = null;
                        List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
                        int numCols = 0;
                        for (String line : lines) {
                            String[] fields = line.split(",");
                            if (fields.length > numCols)
                                numCols = fields.length;
                            if (tableModel == null)
                                tableModel = new TableModel<>(fields);
                            else
                                tableModel.addRow(fields);
                        }
                        table.setTableModel(tableModel);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }

                } else {
                    System.out.println("File was null.");
                }
            }
        }));
    }


    private static void runQuery(String query) {
        List<String> colNames = new ArrayList<>();
        List<List<String>> results = DatabaseDAO.runQuery(query, colNames);

        populateResults(results, colNames);
    }

    static void populateResults(List<List<String>> results, List<String> colNames) {
        if (!results.isEmpty()) {
            TableModel<String> tableModel = new TableModel<>(colNames);
            for (List<String> row : results) {
                tableModel.addRow(row);
            }
            table.setTableModel(tableModel);
        }
    }

    public static class CBListner implements ComboBox.Listener {

        @Override
        public void onSelectionChanged(int selectedIndex, int previousSelection, boolean changedByUserInteraction) {
            String selected = comboBox.getSelectedItem();
            if (selected != null && !selected.trim().isEmpty()) {
                List<String> columnNames = DatabaseDAO.getColumnNames(selected);
                List<List<String>> tableData = DatabaseDAO.getAllTableData(selected);
                if (!tableData.isEmpty()) {
                    TableModel<String> tableModel = new TableModel<>(columnNames);
                    // List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
                    for (List<String> row : tableData) {
                        tableModel.addRow(row);
                    }
                    table.setTableModel(tableModel);
                }
            }
        }
    }

    public static Theme getTheme() {
        Theme myTheme = SimpleTheme.makeTheme(true, TextColor.ANSI.WHITE,
                TextColor.ANSI.BLACK, TextColor.ANSI.BLUE_BRIGHT, new TextColor.RGB(20,20,20),
                new TextColor.RGB(190,190,190), new TextColor.RGB(10,10,10),TextColor.ANSI.BLACK);
        return myTheme;
    }
}