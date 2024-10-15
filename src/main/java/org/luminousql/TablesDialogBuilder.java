package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.AbstractDialogBuilder;

public class TablesDialogBuilder extends AbstractDialogBuilder<TablesDialogBuilder,TablesDialog> {
    private final TerminalSize suggestedSize;
    private final WindowBasedTextGUI textGUI;

    public TablesDialogBuilder(TerminalSize terminalSize, WindowBasedTextGUI textGUI) {
        super("Tables");
        this.suggestedSize = terminalSize;
        this.textGUI = textGUI;
    }

    @Override
    protected TablesDialogBuilder self() {
        return null;
    }

    @Override
    protected TablesDialog buildDialog() {
        return new TablesDialog(suggestedSize, textGUI);
    }
}
