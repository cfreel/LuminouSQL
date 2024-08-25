package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.AbstractDialogBuilder;

public class QueryDialogBuilder extends AbstractDialogBuilder<QueryDialogBuilder,QueryDialog> {
    private final TerminalSize suggestedSize;
    private final WindowBasedTextGUI textGUI;

    public QueryDialogBuilder(TerminalSize terminalSize, WindowBasedTextGUI textGUI) {
        super("Query Builder");
        this.suggestedSize = terminalSize;
        this.textGUI = textGUI;
    }

    @Override
    protected QueryDialogBuilder self() {
        return null;
    }

    @Override
    protected QueryDialog buildDialog() {
        return new QueryDialog(suggestedSize, textGUI);
    }

}
