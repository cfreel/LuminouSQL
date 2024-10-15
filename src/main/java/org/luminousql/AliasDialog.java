package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;

public class AliasDialog extends DialogWindow {
    TextBox curAliasNameField;
    TextBox curAliasUserField;
    TextBox curAliasPassField;
    ComboBox<String> curAliasDriverField;
    TextBox curAliasConnectionField;
    ActionListBox aliasesListBox;

    protected AliasDialog(TerminalSize dialogSize, WindowBasedTextGUI textGUI) {
        super("Aliases");

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        contentPanel.addComponent(new Label("Existing Aliases:"));
        contentPanel.addComponent(new EmptySpace());

        aliasesListBox = new ActionListBox(new TerminalSize(50, 5));
        Configuration.aliases.forEach(e -> {
            aliasesListBox.addItem(e.name, new Runnable() {
                @Override
                public void run() {
                    // populate edit fields
                    curAliasNameField.setText(e.name);
                    curAliasUserField.setText(e.user);
                    curAliasPassField.setText(e.pass);
                    int idx = -1;
                    for (int i=0; i<curAliasDriverField.getItemCount(); i++) {
                        if (curAliasDriverField.getItem(i).equals(e.driver)) {
                            idx = i;
                            break;
                        }
                    }
                    if (idx >= 0)
                        curAliasDriverField.setSelectedIndex(idx);
                    curAliasConnectionField.setText(e.connection);

                }
            });
        });
        contentPanel.addComponent(aliasesListBox);

        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL).setPreferredSize(
                        new TerminalSize(dialogSize.getColumns(), 1)));

        Panel editFieldsPanel = new Panel(new GridLayout(2));
        TerminalSize editFieldSize = new TerminalSize(40, 1);
        editFieldsPanel.addComponent(new Label("Alias Name: "));
        curAliasNameField = new TextBox(editFieldSize);
        editFieldsPanel.addComponent(curAliasNameField);

        editFieldsPanel.addComponent(new EmptySpace());
        editFieldsPanel.addComponent(new EmptySpace());

        editFieldsPanel.addComponent(new Label("Driver: "));
        curAliasDriverField = new ComboBox<>();
        Configuration.configuredDrivers.forEach(e->{
            curAliasDriverField.addItem(e.name);
        });

        editFieldsPanel.addComponent(curAliasDriverField);

        editFieldsPanel.addComponent(new EmptySpace());
        editFieldsPanel.addComponent(new EmptySpace());

        editFieldsPanel.addComponent(new Label("Alias User: "));
        curAliasUserField = new TextBox(editFieldSize);
        editFieldsPanel.addComponent(curAliasUserField);

        editFieldsPanel.addComponent(new EmptySpace());
        editFieldsPanel.addComponent(new EmptySpace());

        editFieldsPanel.addComponent(new Label("Alias Password: "));
        curAliasPassField = new TextBox(editFieldSize);
        curAliasPassField.setMask('*');
        editFieldsPanel.addComponent(curAliasPassField);

        editFieldsPanel.addComponent(new EmptySpace());
        editFieldsPanel.addComponent(new EmptySpace());

        editFieldsPanel.addComponent(new Label("Connection: "));
        curAliasConnectionField = new TextBox(editFieldSize);
        editFieldsPanel.addComponent(curAliasConnectionField);

        contentPanel.addComponent(editFieldsPanel);

        contentPanel.addComponent(
                new Separator(Direction.HORIZONTAL).setPreferredSize(
                        new TerminalSize(dialogSize.getColumns(), 1)));

        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

        buttonPanel.addComponent(new Button("Add", new Runnable() {
            @Override
            public void run() {
                // todo: add validation, but just for starters assume correctness

                Alias configuredAlias = new Alias(curAliasNameField.getText(),
                        curAliasUserField.getText(), curAliasPassField.getText(),
                        curAliasDriverField.getText(), curAliasConnectionField.getText());
                Configuration.addAlias(configuredAlias);
                aliasesListBox.addItem(configuredAlias.name, new Runnable() {
                    @Override
                    public void run() {
                        curAliasNameField.setText(configuredAlias.name);
                        curAliasUserField.setText(configuredAlias.user);
                        curAliasPassField.setText(configuredAlias.pass);
                        int idx = -1;
                        for (int i=0; i<curAliasDriverField.getItemCount(); i++) {
                            if (curAliasDriverField.getItem(i).equals(configuredAlias.driver)) {
                                idx = i;
                                break;
                            }
                        }
                        if (idx >= 0)
                            curAliasDriverField.setSelectedIndex(idx);
                        curAliasConnectionField.setText(configuredAlias.connection);
                    }
                });
            }
        }));

        buttonPanel.addComponent(new Button("Delete", new Runnable() {
            @Override
            public void run() {
                String AliasName = curAliasNameField.getText();
                int idx = -1;
                for (int i=0; i<Configuration.aliases.size(); i++) {
                    if (Configuration.aliases.get(i).name.equals(AliasName)) {
                        idx = i;
                    }
                }
                if (idx >= 0)
                    aliasesListBox.removeItem(idx);
                Configuration.removeAlias(AliasName);
            }
        }));

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
