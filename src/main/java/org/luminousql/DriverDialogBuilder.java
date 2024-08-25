package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.LocalizedString;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.AbstractDialogBuilder;

public class DriverDialogBuilder extends AbstractDialogBuilder<DriverDialogBuilder,DriverDialog> {
    private final TerminalSize suggestedSize;
    private final WindowBasedTextGUI textGUI;

    public DriverDialogBuilder(TerminalSize suggestedSize, WindowBasedTextGUI textGUI) {
        super("Drivers");
        this.suggestedSize = suggestedSize;
        this.textGUI = textGUI;
    }

    @Override
    protected DriverDialogBuilder self() {
        return this;
    }

    @Override
    protected DriverDialog buildDialog() {
        return new DriverDialog(suggestedSize, textGUI);
    }
}
