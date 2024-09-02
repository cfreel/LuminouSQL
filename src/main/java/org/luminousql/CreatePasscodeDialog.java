package org.luminousql;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

public class CreatePasscodeDialog extends DialogWindow {
    TextBox passcode;

    protected CreatePasscodeDialog(TerminalSize dialogSize, WindowBasedTextGUI textGUI) {
        super("Create Passcode");

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        contentPanel.addComponent(new Label("Enter a passcode:"));
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
                if (pCode==null ||  pCode.length()!=6) {
                    new MessageDialogBuilder().
                            setTitle("Error").
                            setText("Invalid passcode length").
                            addButton(MessageDialogButton.OK).
                            build().showDialog(textGUI);
                    return;
                }
                try {
                    Configuration.setPasscodeInConfig(passcode.getText());
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.err.println(t.getMessage());
                    System.exit(-1);
                }
                Configuration.passcode = passcode.getText();
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
