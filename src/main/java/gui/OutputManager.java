package gui;

import java.awt.Color;
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
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/** Manages the Output text pane and Input text field. */
public class OutputManager {
    private static JTextPane txtPane;
    private static JTextField txtInput;

    private static StringBuilder output = new StringBuilder();
    private static boolean currentlyPrinting = false;
    private static long startTime = 0;
    private static long currentTime = 0;

    protected static void registerGUI(MainFrame mainFrame) {
        Objects.requireNonNull(mainFrame);

        txtPane = mainFrame.getTxtPane();
        txtInput = mainFrame.getTxtInput();
    }

    /**
     * @return {@code true} if the current output string is empty, {@code false} otherwise.
     */
    protected static boolean isOutputEmpty() {
        return output.toString().isEmpty() ? true : false;
    }

    /**
     * @param string the string to append to the current output string.
     */
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

    /**
     * @param string the string to clean up.
     * @return the string trimmed of starting whitespaces and ending newlines.
     */
    private static String getCleanString(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != '\n') {
                return string.substring(i, string.length()).trim();
            }
        }
        return string;
    }

    /**
     * Requests to print the current saved output string in "Output" format,
     * which is black and with nothing added to it.
     */
    protected static void printAsOutput() {
        Objects.requireNonNull(txtPane);

        write(output.toString(), false);

        reset();
    }

    /**
     * Requests to print the string in "Input" format,
     * which is gray and with a ">" before the string.
     * 
     * @param text the string to print
     */
    protected static void printAsInput(String text) {
        Objects.requireNonNull(txtPane);

        write(text, true);

    }

    /**
     * Prints the requested text, in the desired format.
     * It prints one character at time, with a delay of 15ms, if it's output,
     * prints instantly otherwise.
     * 
     * @param text the string to print.
     * @param inputText {@code true} if it's an Input, {@code false} otherwise.
     */
    private static void write(String text, boolean inputText) {
        StyledDocument doc = txtPane.getStyledDocument();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (inputText) {
                        if (txtPane.getStyle("InputText") == null) {
                            Style style = txtPane.addStyle("InputText", null);
                            StyleConstants.setForeground(style, Color.GRAY);
                        }

                        doc.insertString(doc.getLength(), "\n\n> " + text, txtPane.getStyle("InputText"));
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

    /**
     * Prints the string one character at time at the desired rate.
     * Gives chance to skip the animation by pressing ENTER key.
     * 
     * @param message the string to print.
     * @param millisPerChar milliseconds to wait for each character.
     * @throws BadLocationException
     */
    private static void printSlowly(String message, int millisPerChar) throws BadLocationException {
        if (message == null || message.isEmpty())
            return;

        currentlyPrinting = true;
        txtInput.setEditable(false);
        txtInput.setFocusable(false);

        StyledDocument doc = txtPane.getStyledDocument();
        if (doc.getLength() > 0)
            doc.insertString(doc.getLength(), "\n\n", null);

        Timer timer = new Timer(millisPerChar, null);
        AtomicInteger counter = new AtomicInteger(0);

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTime = System.currentTimeMillis();

                try {
                    if ((currentTime - startTime) >= getWaitTime()) {
                        doc.insertString(doc.getLength(), String.valueOf(message.charAt(counter.getAndIncrement())),
                                null);
                        startTime = System.currentTimeMillis();
                    } else {
                        return;
                    }
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

            private int getWaitTime() {
                if (counter.get() > 1) {
                    if (message.substring(counter.get() - 2, counter.get()).equals(". ")
                            || message.substring(counter.get() - 2, counter.get()).equals(".\n")) {
                        return 300;
                    } else if (message.substring(counter.get() - 2, counter.get()).equals(": ")
                            || message.substring(counter.get() - 2, counter.get()).equals("..")) {
                        return 200;
                    } else if (message.substring(counter.get() - 2, counter.get()).equals("- ")) {
                        return 100;
                    }
                }

                return 0;
            }
        });

        startTime = System.currentTimeMillis();
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

    /**
     * When invoked, the execution stops until ENTER key is pressed.
     * Waiting is done on a dedicated thread.
     */
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

    /** Empty the current output string. */
    private static void reset() {
        output.setLength(0);
    }
}
