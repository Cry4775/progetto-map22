package gui;

import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import engine.GameManager;

public class GUIManager {
    private static MainFrame gui;

    private static String lastDescription;

    protected static void registerGUI(MainFrame mainFrame) {
        Objects.requireNonNull(mainFrame);

        gui = mainFrame;
        OutputManager.registerGUI(mainFrame);
        CompassManager.registerGUI(mainFrame);
    }

    public static void waitUntilEnterIsPressed() {
        OutputManager.waitForEnterKey();
    }

    public static void appendOutput(StringBuilder stringBuilder) {
        OutputManager.append(stringBuilder.toString());
    }

    public static void appendOutput(String string) {
        OutputManager.append(string);
    }

    public static void printOutput() {
        OutputManager.printAsOutput();
    }

    protected static void printInput(String text) {
        OutputManager.printAsInput(text);
    }

    public static boolean isOutputEmpty() {
        return OutputManager.isOutputEmpty();
    }

    public static void updateRoomInformations() {
        updateRoomInformations(false);
    }

    public static void updateRoomInformations(boolean dark) {
        // TODO é hardcoded, bisogna permettere la personalizzazione
        String description;
        if (dark) {
            updateRoomInformations("Buio", "resources/img/buio.jpg");
            description = "È completamente buio e non riesci a vedere niente.";
        } else {
            updateRoomInformations(GameManager.getCurrentRoom().getName(), GameManager.getCurrentRoom().getImgPath());
            description = GameManager.getCurrentRoom().getDescription();
        }

        if (!description.equals(lastDescription)) {
            lastDescription = description;
            appendOutput(description);
        }

        printOutput();
    }

    private static void updateRoomInformations(String roomName, String imageURL) {
        CompassManager.updateCompass();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.getLblRoomName().setText(roomName);
                Image roomImg = new ImageIcon(imageURL).getImage().getScaledInstance(581, 300, Image.SCALE_SMOOTH);
                gui.getLblRoomImage().setIcon(new ImageIcon(roomImg));
            }
        });
    }

    public static void showFatalError(String message) {
        gui.showFatalError(message);
    }

    public static int askLoadingConfirmation() {
        AtomicInteger chosenOption = new AtomicInteger();

        try {
            Runnable runnable = new Runnable() {
                public void run() {
                    chosenOption.set(JOptionPane.showConfirmDialog(null,
                            "An existing savegame has been found. Do you wish to load it?",
                            "Loading savegame",
                            JOptionPane.YES_NO_OPTION));
                }
            };

            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeAndWait(runnable);
            } else {
                runnable.run();
            }
        } catch (InvocationTargetException | InterruptedException e) {
            throw new Error(e);
        }

        return chosenOption.get();

    }

    public static void increaseActionsCounter() {
        String[] strings = gui.getLblActions().getText().split(".*: ");
        for (String string : strings) {
            if (string.matches("[0-9]+")) {
                int oldCounterVal = Integer.parseInt(string);
                gui.getLblActions().setText("Azioni: " + Integer.toString(oldCounterVal + 1));
                return;
            }
        }
    }
}
