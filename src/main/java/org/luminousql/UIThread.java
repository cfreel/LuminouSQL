package org.luminousql;

import com.googlecode.lanterna.TerminalPosition;
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

import java.io.*;
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
                    new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                            new EmptySpace(new TextColor.RGB(20,20,20)));
            textGUI.setTheme(getTheme());
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
            table = new Table<>(tableModel.getColumnLabels().toArray(new String[]{}));
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

            initConfig(dialogSize, textGUI, theme);

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

    private static void initConfig(TerminalSize dialogSize, WindowBasedTextGUI textGUI, Theme theme) {
        Configuration.initConfig();

        if (!Configuration.passcodeExists()) {
            // create passcode dialog
            CreatePasscodeDialog cpd = new CreatePasscodeDialogBuilder(dialogSize, textGUI).build();
            cpd.setTheme(theme);
            cpd.setPosition(new TerminalPosition(10,10));
            cpd.showDialog(textGUI);
        } else {
            // get & check passcode dialog
            GetPasscodeDialog gpd = new GetPasscodeDialogBuilder(dialogSize, textGUI).buildDialog();
            gpd.setTheme(theme);
            gpd.setPosition(new TerminalPosition(10,10));
            gpd.showDialog(textGUI);
        }

        Configuration.loadAliases();
    }

    private static void addMenuToPanel(Panel panel, WindowBasedTextGUI textGUI, Screen screen) {
        MenuBar menubar = new MenuBar();
        Theme theme = getTheme();
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

        MenuItem drivers = new MenuItem("Drivers", new Runnable() {
            @Override
            public void run() {
                TerminalSize parentSize = screen.getTerminalSize();
                TerminalSize size = new TerminalSize(parentSize.getColumns()-8,
                        parentSize.getRows()-8);
                DriverDialog dd = new DriverDialogBuilder(size, textGUI).build();
                dd.showDialog(textGUI);
            }
        });
        menuDB.add(drivers);

        MenuItem aliases = new MenuItem("Aliases", new Runnable() {
            @Override
            public void run() {
                TerminalSize parentSize = screen.getTerminalSize();
                TerminalSize size = new TerminalSize(parentSize.getColumns()-8,
                        parentSize.getRows()-6);
                AliasDialog ad = new AliasDialogBuilder(size, textGUI).build();
                ad.showDialog(textGUI);
            }
        });
        menuDB.add(aliases);

        MenuItem loadTables = new MenuItem("Explore Table Structure...", new Runnable() {
            @Override
            public void run() {
                TerminalSize parentSize = screen.getTerminalSize();
                TerminalSize size = new TerminalSize(parentSize.getColumns()-8,
                        parentSize.getRows()-6);
                TablesDialog td = new TablesDialogBuilder(size, textGUI).build();
                td.showDialog(textGUI);
            }
        });
        menuDB.add(loadTables);

        Menu menuQuery = new Menu("Queries");
        menubar.add(menuQuery);
        menuQuery.add(new MenuItem("Run Query", new Runnable() {
            @Override
            public void run() {
                TerminalSize parentSize = screen.getTerminalSize();
                TerminalSize size = new TerminalSize(parentSize.getColumns()-8,
                        parentSize.getRows()-6);
                QueryDialog qd = new QueryDialogBuilder(size, textGUI).build();
                qd.showDialog(textGUI);
            }
        }));

        menuQuery.add(new MenuItem("Save Results", new Runnable() {
            @Override
            public void run() {
                try {
                    // just file dialog (or message if validation error)
                    FileDialog fd = new FileDialogBuilder().build();
                    File file = fd.showDialog(textGUI);
                    if (file != null) {
                        if (file.exists()) {
                            MessageDialogButton[] buttons = new MessageDialogButton[]{MessageDialogButton.OK,
                                    MessageDialogButton.Abort };
                            MessageDialogButton overwriteBtn = MessageDialog.showMessageDialog(textGUI, "Confirm Overwrite",
                                    "File exists, OK to overwrite?", buttons);
                            if (overwriteBtn == MessageDialogButton.Abort)
                                return;
                        }
                        try (FileWriter fw = new FileWriter(file);
                             BufferedWriter bw = new BufferedWriter(fw)) {

                            bw.write(String.join(",", table.getTableModel().getColumnLabels()));
                            bw.newLine();
                            for (List<String> row : table.getTableModel().getRows()) {
                                bw.write(String.join(",", row));
                                bw.newLine();
                            }
                            bw.flush();
                        }
                    } else {
                        System.err.println("Failed to write query results to file.");
                    }
                } catch (Throwable t) {
                    System.err.println("Failed to write query results to file.");
                }
            }
        }));

        // "Help" menu
        Menu menuHelp = new Menu("Help");
        menubar.add(menuHelp);
        menuHelp.add(new MenuItem("Homepage", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(
                        textGUI, "Homepage", "https://github.com/cfreel/LuminouSQL", MessageDialogButton.OK);
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

        UIHelper.populateResults(table, results, colNames);
    }

    public static Theme getTheme() {
        TextColor text = TextColor.ANSI.WHITE;
        TextColor background = TextColor.ANSI.BLACK;
        TextColor textColorCurField = new TextColor.RGB(120, 120, 255);

        Theme myTheme = SimpleTheme.makeTheme(true, text,
                background, textColorCurField, new TextColor.RGB(30,30,30),
                new TextColor.RGB(190,190,190), new TextColor.RGB(10,10,10),TextColor.ANSI.BLACK);
        return myTheme;
    }

}