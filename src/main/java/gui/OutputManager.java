package gui;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class OutputManager {
    private static MainFrame gui;
    private static StringBuilder output = new StringBuilder();
    private static JTextPane txtPane;
    private static JTextField txtInput;

    private static boolean currentlyPrinting = false;

    protected static void registerGUI(MainFrame mainFrame) {
        Objects.requireNonNull(mainFrame);

        gui = mainFrame;
        txtPane = mainFrame.getTxtPane();
        txtInput = mainFrame.getTxtInput();
    }

    protected static boolean isOutputEmpty() {
        return output.toString().isEmpty() ? true : false;
    }

    protected static void append(String string) {
        if (string != null && !string.isEmpty()) {
            if (output.length() > 0) {
                if (output.length() > 1) {
                    if (output.charAt(output.length() - 1) == '\n'
                            && output.charAt(output.length() - 2) == '\n') {
                        output.append(getCleanString(string));
                    } else if (output.charAt(output.length() - 1) == '\n') {
                        output.append("\n");
                        output.append(getCleanString(string));
                    } else {
                        output.append("\n\n");
                        output.append(getCleanString(string));
                    }
                } else if (output.charAt(output.length() - 1) == '\n') {
                    output.append("\n");
                    output.append(getCleanString(string));
                } else {
                    output.append("\n\n");
                    output.append(getCleanString(string));
                }
            } else {
                output.append(getCleanString(string));
            }
        }
    }

    private static String getCleanString(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != '\n') {
                return string.substring(i, string.length());
            }
        }
        return string;
    }

    protected static void printAsOutput() {
        Objects.requireNonNull(gui);

        write(output.toString(), false);

        reset();
    }

    protected static void printAsInput(String text) {
        Objects.requireNonNull(gui);

        write(text, true);

    }

    private static void write(String text, boolean inputText) {
        StyledDocument doc = txtPane.getStyledDocument();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (inputText) {
                        doc.insertString(doc.getLength(), String.format("\n\n>%s", text), null);
                    } else {
                        printSlowly(text, 15);
                    }
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        };

        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InvocationTargetException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            runnable.run();
        }
    }

    private static void printSlowly(String message, int millisPerChar) throws BadLocationException {
        if (message == null || message.isEmpty())
            return;

        currentlyPrinting = true;
        txtInput.setEditable(false);
        txtInput.setFocusable(false);

        StyledDocument doc = txtPane.getStyledDocument();
        doc.insertString(doc.getLength(), "\n\n", null);

        Timer timer = new Timer(millisPerChar, null);
        AtomicInteger counter = new AtomicInteger(0);

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    doc.insertString(doc.getLength(), String.valueOf(message.charAt(counter.getAndIncrement())), null);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                txtPane.setCaretPosition(txtPane.getDocument().getLength());
                if (counter.get() >= message.length()) {
                    timer.stop();
                    txtInput.setEditable(true);
                    txtInput.setFocusable(true);
                    txtInput.requestFocusInWindow();
                    currentlyPrinting = false;
                }
            }
        });

        timer.start();

        // Skip writing animation
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        && e.getID() == KeyEvent.KEY_RELEASED) {
                    timer.stop();
                    try {
                        doc.insertString(doc.getLength(), message.substring(counter.get()), null);
                        txtInput.setEditable(true);
                        txtInput.setFocusable(true);
                        txtInput.requestFocusInWindow();
                        currentlyPrinting = false;
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                    manager.removeKeyEventDispatcher(this);
                } else if (counter.get() >= message.length()) {
                    manager.removeKeyEventDispatcher(this);
                }

                return true;
            }

        });

    }

    protected static void waitForEnterKey() {
        Thread thread = new Thread("WaitForInputThread") {
            public void run() {
                AtomicBoolean waiting = new AtomicBoolean(true);

                try {
                    while (currentlyPrinting) {
                        sleep(30);
                    }

                    SwingUtilities.invokeLater(() -> {
                        txtInput.setText("Premere INVIO per continuare...");
                        txtInput.setFocusable(false);
                        txtInput.setEditable(false);

                        KeyboardFocusManager manager =
                                KeyboardFocusManager.getCurrentKeyboardFocusManager();
                        manager.addKeyEventDispatcher(new KeyEventDispatcher() {

                            @Override
                            public boolean dispatchKeyEvent(KeyEvent e) {
                                if (e.getKeyCode() == KeyEvent.VK_ENTER
                                        && e.getID() == KeyEvent.KEY_RELEASED) {
                                    txtInput.setText("");
                                    txtInput.setEditable(true);
                                    txtInput.setFocusable(true);
                                    txtInput.requestFocusInWindow();
                                    manager.removeKeyEventDispatcher(this);
                                    waiting.set(false);
                                }
                                return true;
                            }
                        });
                    });

                    while (waiting.get()) {
                        sleep(30);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void reset() {
        output.setLength(0);
    }
}
