package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.AbstractDialogBuilder;

public class GetPasscodeDialogBuilder extends AbstractDialogBuilder<GetPasscodeDialogBuilder,GetPasscodeDialog> {
    private final TerminalSize suggestedSize;
    private final WindowBasedTextGUI textGUI;

    public GetPasscodeDialogBuilder(TerminalSize suggestedSize, WindowBasedTextGUI textGUI) {
        super("Create Passcode");
        this.suggestedSize = suggestedSize;
        this.textGUI = textGUI;
    }

    @Override
    protected GetPasscodeDialogBuilder self() {
        return this;
    }

    @Override
    protected GetPasscodeDialog buildDialog() {
        return new GetPasscodeDialog(suggestedSize, textGUI);
    }

}
