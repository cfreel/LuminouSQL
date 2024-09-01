package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.AbstractDialogBuilder;

public class CreatePasscodeDialogBuilder extends AbstractDialogBuilder<CreatePasscodeDialogBuilder,CreatePasscodeDialog> {
    private final TerminalSize suggestedSize;
    private final WindowBasedTextGUI textGUI;

    public CreatePasscodeDialogBuilder(TerminalSize suggestedSize, WindowBasedTextGUI textGUI) {
        super("Create Passcode");
        this.suggestedSize = suggestedSize;
        this.textGUI = textGUI;
    }

    @Override
    protected CreatePasscodeDialogBuilder self() {
        return this;
    }

    @Override
    protected CreatePasscodeDialog buildDialog() {
        return new CreatePasscodeDialog(suggestedSize, textGUI);
    }
}
