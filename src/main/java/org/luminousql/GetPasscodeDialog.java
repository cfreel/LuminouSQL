package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

public class GetPasscodeDialog extends DialogWindow {
    TextBox passcode;
    private static int failedPasscodeCount = 0;
    private static final int MAX_PASSCODE_FAILURES = 4;

    protected GetPasscodeDialog(TerminalSize dialogSize, WindowBasedTextGUI textGUI) {
        super("Enter Passcode");

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        contentPanel.addComponent(new Label("Enter passcode:"));
        passcode = new TextBox(new TerminalSize(16, 1));
        passcode.setMask('*');

        contentPanel.addComponent(passcode);

        contentPanel.addComponent(new EmptySpace());

        contentPanel.addComponent(new EmptySpace());

        Panel buttonPanel = new Panel(new GridLayout(2));
        buttonPanel.addComponent(new Button("Continue", new Runnable() {
            @Override
            public void run() {
                String pCode = passcode.getText();
                if (pCode==null || pCode.isEmpty()) {
                    new MessageDialogBuilder().
                            setTitle("Error").
                            setText("Invalid passcode length").
                            addButton(MessageDialogButton.OK).
                            build().showDialog(textGUI);
                    return;
                }
                boolean valid = false;
                try {
                    valid = Configuration.passcodeIsValid(pCode);
                } catch (Throwable t) {
                    // no action necessary; sometimes using a different size code will cause an exception
                    // due to mismatched block size
                }

                if (!valid) {
                    failedPasscodeCount++;
                    new MessageDialogBuilder().
                            setTitle("Error").
                            setText("Incorrect passcode").
                            addButton(MessageDialogButton.OK).
                            build().showDialog(textGUI);
                    if (failedPasscodeCount>MAX_PASSCODE_FAILURES)
                        System.exit(-1);
                    return;
                }
                Configuration.passcode = pCode;
                close();
            }
        }));

        buttonPanel.addComponent(new Button("Exit", new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }));

        contentPanel.addComponent(buttonPanel);
        setComponent(contentPanel);
        setFixedSize(dialogSize);
    }

}
