package org.luminousql;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;

import java.io.File;

public class DriverDialog extends DialogWindow {

    TextBox curDriverNameField;
    TextBox curDriverPathField;
    TextBox curDriverClassField;
    ActionListBox driversListBox;

    protected DriverDialog(TerminalSize dialogSize, WindowBasedTextGUI textGUI) {
        super("Drivers");

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        contentPanel.addComponent(new Label("Existing Drivers:"));

        driversListBox = new ActionListBox(new TerminalSize(50, 6));
        Configuration.configuredDrivers.forEach(e -> {
            driversListBox.addItem(e.name, new Runnable() {
                @Override
                public void run() {
                    // populate edit fields
                    curDriverNameField.setText(e.name);
                    curDriverPathField.setText(e.path);
                    curDriverClassField.setText(e.className);
                }
            });
        });
        contentPanel.addComponent(driversListBox);

        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL).setPreferredSize(
                        new TerminalSize(dialogSize.getColumns(), 1)));

        Panel editFieldsPanel = new Panel(new GridLayout(2));
        TerminalSize editFieldSize = new TerminalSize(40, 1);
        editFieldsPanel.addComponent(new Label("Driver Name: "));
        curDriverNameField = new TextBox(editFieldSize);
        editFieldsPanel.addComponent(curDriverNameField);

        editFieldsPanel.addComponent(new EmptySpace());
        editFieldsPanel.addComponent(new EmptySpace());

        editFieldsPanel.addComponent(new Label("Driver File: "));
        curDriverPathField = new TextBox(editFieldSize);

        Panel selectFilePanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        selectFilePanel.addComponent(curDriverPathField);
        selectFilePanel.addComponent(new Button("Select File", new Runnable() {
            @Override
            public void run() {
                File file = new FileDialogBuilder().build().showDialog(textGUI);
                if (file != null) {
                    curDriverPathField.setText(file.getAbsolutePath());
                }
            }
        }));
        editFieldsPanel.addComponent(selectFilePanel);

        editFieldsPanel.addComponent(new EmptySpace());
        editFieldsPanel.addComponent(new EmptySpace());

        editFieldsPanel.addComponent(new Label("Driver Class: "));
        curDriverClassField = new TextBox(editFieldSize);
        editFieldsPanel.addComponent(curDriverClassField);

        contentPanel.addComponent(editFieldsPanel);

        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL).setPreferredSize(
                        new TerminalSize(dialogSize.getColumns(), 1)));

        Panel buttonPanel = new Panel(new GridLayout(2));

        Panel addDelPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        addDelPanel.addComponent(new Button("Add", new Runnable() {
            @Override
            public void run() {
                // todo: add validation, but just for starters assume correctness
                // validation would include checking the file exists, trial reading it, and
                // finding the driver class the user specified within it

                ConfiguredDriver configuredDriver = new ConfiguredDriver(curDriverNameField.getText(),
                        curDriverPathField.getText(), curDriverClassField.getText());
                Configuration.addDriver(configuredDriver);
                driversListBox.addItem(configuredDriver.name, new Runnable() {
                    @Override
                    public void run() {
                        curDriverNameField.setText(configuredDriver.name);
                        curDriverPathField.setText(configuredDriver.path);
                        curDriverClassField.setText(configuredDriver.className);
                    }
                });
            }
        }));

        addDelPanel.addComponent(new Button("Delete", new Runnable() {
            @Override
            public void run() {
                String driverName = curDriverNameField.getText();
                int idx = -1;
                for (int i=0; i<Configuration.configuredDrivers.size(); i++) {
                    if (Configuration.configuredDrivers.get(i).name.equals(driverName)) {
                        idx = i;
                    }
                }
                if (idx >= 0)
                    driversListBox.removeItem(idx);
                Configuration.removeDriver(driverName);
            }
        }));
        buttonPanel.addComponent(addDelPanel);

        buttonPanel.addComponent(new Button(LocalizedString.Close.toString(), new Runnable() {
            @Override
            public void run() {
                close();
            }
        }));

        contentPanel.addComponent(buttonPanel);

        setComponent(contentPanel);
        setFixedSize(dialogSize);
    }
}
