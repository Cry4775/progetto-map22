package gui;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;
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

    public static void updateRoomInformations(String roomName, String imageURL) {
        gui.getLblRoomName().setText(roomName);
        Image roomImg = new ImageIcon(imageURL).getImage()
                .getScaledInstance(581, 300, Image.SCALE_SMOOTH);
        gui.getLblRoomImage().setIcon(new ImageIcon(roomImg));
        CompassManager.updateCompass();
    }
}
