package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;

import java.util.ArrayList;

public class QueryDialog extends DialogWindow {
    ComboBox<String> aliases;
    TextBox query;

    protected QueryDialog(TerminalSize dialogSize, WindowBasedTextGUI textGUI) {
        super("Drivers");

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        contentPanel.addComponent(new Label("Choose alias:"));
        aliases = new ComboBox<>();
        Configuration.aliases.forEach(e->{
            aliases.addItem(e.name);
        });

        contentPanel.addComponent(aliases);

        contentPanel.addComponent(new EmptySpace());

        TerminalSize size = new TerminalSize(dialogSize.getColumns()-4, dialogSize.getRows()/2);
        query = new TextBox(size);
        contentPanel.addComponent(query);

        contentPanel.addComponent(new EmptySpace());

        Panel buttonPanel = new Panel(new GridLayout(2));
        buttonPanel.addComponent(new Button("Run query", new Runnable() {
            @Override
            public void run() {
                // todo: interstitial?
                Configuration.columns = new ArrayList<>();
                Configuration.queryResults = DatabaseDAO.runQuery(query.getText(), Configuration.columns,
                        aliases.getText());
                UIHelper.populateResults(UIThread.table, Configuration.queryResults, Configuration.columns);
                close();
            }
        }));

        buttonPanel.addComponent(new Button("Cancel", new Runnable() {
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
