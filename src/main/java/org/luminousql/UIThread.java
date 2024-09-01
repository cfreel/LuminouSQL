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
import java.util.ArrayList;
import java.util.List;

public class UIThread implements Runnable {
    static List<String> tableNames = new ArrayList<>();
    static Table<String> table;
    static MenuItem pullDataMenuItem;

    private static final String FAILED_PASSCODE_MSG = "Incorrect passcode";
    private static final String OTHER_PASSCODE_ERROR = "Error processing passcode";

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

            TerminalSize dialogSize = new TerminalSize(screen.getTerminalSize().getColumns()/2,
                    screen.getTerminalSize().getRows()/2);

            if (!Configuration.passcodeExists()) {
                // create passcode dialog
                new CreatePasscodeDialogBuilder(dialogSize, textGUI).build().showDialog(textGUI);
            } else {
                // get & check passcode dialog
                new GetPasscodeDialogBuilder(dialogSize, textGUI).buildDialog().showDialog(textGUI);
            }

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

    public static Theme getTheme() {
        Theme myTheme = SimpleTheme.makeTheme(true, TextColor.ANSI.WHITE,
                TextColor.ANSI.BLACK, TextColor.ANSI.BLUE_BRIGHT, new TextColor.RGB(20,20,20),
                new TextColor.RGB(190,190,190), new TextColor.RGB(10,10,10),TextColor.ANSI.BLACK);
        return myTheme;
    }
}