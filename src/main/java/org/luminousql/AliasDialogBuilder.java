package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.AbstractDialogBuilder;

public class AliasDialogBuilder extends AbstractDialogBuilder<AliasDialogBuilder,AliasDialog> {

    private final TerminalSize suggestedSize;
    private final WindowBasedTextGUI textGUI;

    public AliasDialogBuilder(TerminalSize terminalSize, WindowBasedTextGUI textGUI) {
        super("Aliases");
        this.suggestedSize = terminalSize;
        this.textGUI = textGUI;
    }

    @Override
    protected AliasDialogBuilder self() {
        return null;
    }

    @Override
    protected AliasDialog buildDialog() {
        return new AliasDialog(suggestedSize, textGUI);
    }
}
